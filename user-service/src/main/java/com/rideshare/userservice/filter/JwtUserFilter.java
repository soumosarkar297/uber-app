package com.rideshare.userservice.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rideshare.userservice.context.UserContext;
import com.rideshare.userservice.entity.User;
import com.rideshare.userservice.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUserFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                String phoneNumber = jwt.getSubject();
                Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
                user.ifPresent(UserContext::setCurrentUser);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}
