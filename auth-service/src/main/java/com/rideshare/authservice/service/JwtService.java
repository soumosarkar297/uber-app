package com.rideshare.authservice.service;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

/**
 * Service for JWT token management including generation, validation, JWKS
 * export, and token blacklisting. Uses RS256 algorithm with RSA 2048-bit key
 * pair.
 *
 * @author Soumo Sarkar
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
public class JwtService {

    private static final String BLACKLIST_KEY_PREFIX = "token:blacklist:";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final RedisTemplate<String, Object> redisTemplate;
    private final int accessTokenExpiryMinutes;
    private final int refreshTokenExpiryDays;
    private final String issuer;
    private final KeyPair keyPair;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtService(
            KeyPair rsaKeyPair,
            RedisTemplate<String, Object> redisTemplate,
            @Value("${jwt.access-token-expiry-minutes:60}") int accessTokenExpiryMinutes,
            @Value("${jwt.refresh-token-expiry-days:30}") int refreshTokenExpiryDays,
            @Value("${jwt.issuer:http://localhost:8081}") String issuer) {
        this.keyPair = rsaKeyPair;
        this.redisTemplate = redisTemplate;
        this.accessTokenExpiryMinutes = accessTokenExpiryMinutes;
        this.refreshTokenExpiryDays = refreshTokenExpiryDays;
        this.issuer = issuer;
    }

    /**
     * Generates an access token with 1-hour expiry.
     *
     * @param phoneNumber the user's phone number (subject)
     * @param userType the user type (RIDER or DRIVER)
     * @param deviceId the device identifier
     * @return the signed JWT access token
     */
    public String generateAccessToken(String phoneNumber, String userType, String deviceId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessTokenExpiryMinutes * 60L);

        return Jwts.builder()
                .subject(phoneNumber)
                .claim("userType", userType)
                .claim("deviceId", deviceId)
                .claim("tokenType", TOKEN_TYPE_ACCESS)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .id(UUID.randomUUID().toString())
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Generates a refresh token with 30-day expiry.
     *
     * @param phoneNumber the user's phone number (subject)
     * @param deviceId the device identifier
     * @return the signed JWT refresh token
     */
    public String generateRefreshToken(String phoneNumber, String deviceId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(refreshTokenExpiryDays * 24L * 60L * 60L);

        return Jwts.builder()
                .subject(phoneNumber)
                .claim("deviceId", deviceId)
                .claim("tokenType", TOKEN_TYPE_REFRESH)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .id(UUID.randomUUID().toString())
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    /**
     * Validates a JWT token and returns its claims. Verifies signature, expiry,
     * and issuer.
     *
     * @param token the JWT token to validate
     * @return the parsed claims
     * @throws JwtException if token is invalid, expired, or has wrong issuer
     */
    public Claims validateToken(String token) {
        if (isBlacklisted(token)) {
            throw new JwtException("Token has been revoked");
        }

        try {
            return Jwts.parser()
                    .verifyWith((RSAPublicKey) keyPair.getPublic())
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token has expired", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid token signature", e);
        } catch (JwtException e) {
            throw new JwtException("Invalid token: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the RSA public key for JWKS endpoint.
     *
     * @return the RSA public key
     */
    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    /**
     * Returns the RSA private key for signing.
     *
     * @return the RSA private key
     */
    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }

    /**
     * Returns the JWK Set as a JSON node for the /oauth2/jwks endpoint.
     *
     * @return JWK Set containing the public key
     */
    public JsonNode getJwkSet() {
        RSAPublicKey publicKey = getPublicKey();
        String kid = UUID.randomUUID().toString();

        ObjectNode jwk = objectMapper.createObjectNode();
        jwk.put("kty", "RSA");
        jwk.put("use", "sig");
        jwk.put("alg", "RS256");
        jwk.put("kid", kid);
        jwk.put("n", base64UrlEncode(publicKey.getModulus().toByteArray()));
        jwk.put("e", base64UrlEncode(publicKey.getPublicExponent().toByteArray()));

        ObjectNode jwks = objectMapper.createObjectNode();
        jwks.set("keys", objectMapper.createArrayNode().add(jwk));

        return jwks;
    }

    /**
     * Adds a token to the Redis blacklist with TTL matching token expiry.
     *
     * @param token the JWT token to blacklist
     * @param ttlSeconds time-to-live in seconds
     */
    public void blacklistToken(String token, long ttlSeconds) {
        String key = BLACKLIST_KEY_PREFIX + hashToken(token);
        redisTemplate.opsForValue().set(key, "revoked", Duration.ofSeconds(ttlSeconds));
    }

    /**
     * Checks if a token is blacklisted (revoked).
     *
     * @param token the JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_KEY_PREFIX + hashToken(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Extracts the phone number (subject) from a token without full validation.
     *
     * @param token the JWT token
     * @return the phone number (subject)
     * @throws JwtException if token cannot be parsed
     */
    public String extractPhoneNumber(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((RSAPublicKey) keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new JwtException("Failed to extract phone number from token", e);
        }
    }

    /**
     * Extracts the deviceId claim from a token without full validation.
     *
     * @param token the JWT token
     * @return the deviceId claim value
     * @throws JwtException if token cannot be parsed or claim is missing
     */
    public String extractDeviceId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((RSAPublicKey) keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("deviceId", String.class);
        } catch (JwtException e) {
            throw new JwtException("Failed to extract deviceId from token", e);
        }
    }

    /**
     * Extracts the tokenType claim from a token.
     *
     * @param token the JWT token
     * @return the tokenType claim value (access or refresh)
     */
    public String extractTokenType(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((RSAPublicKey) keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("tokenType", String.class);
        } catch (JwtException e) {
            throw new JwtException("Failed to extract tokenType from token", e);
        }
    }

    /**
     * Extracts the userType claim from an access token.
     *
     * @param token the JWT access token
     * @return the userType claim value (RIDER or DRIVER)
     */
    public String extractUserType(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((RSAPublicKey) keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.get("userType", String.class);
        } catch (JwtException e) {
            throw new JwtException("Failed to extract userType from token", e);
        }
    }

    /**
     * Returns the access token expiry in seconds.
     *
     * @return access token expiry in seconds
     */
    public long getAccessTokenExpirySeconds() {
        return accessTokenExpiryMinutes * 60L;
    }

    /**
     * Returns the refresh token expiry in seconds.
     *
     * @return refresh token expiry in seconds
     */
    public long getRefreshTokenExpirySeconds() {
        return refreshTokenExpiryDays * 24L * 60L * 60L;
    }

    /**
     * Creates a SHA-256 hash of the token for use as Redis key. Avoids storing
     * full tokens in Redis.
     *
     * @param token the token to hash
     * @return hex-encoded SHA-256 hash
     */
    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Converts byte array to hex string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Base64Url encodes a byte array (RFC 7515).
     */
    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
