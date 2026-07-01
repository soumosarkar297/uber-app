package com.rideshare.driverservice.filter;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.rideshare.driverservice.context.DriverContext;
import com.rideshare.driverservice.entity.Driver;
import com.rideshare.driverservice.repository.DriverRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtDriverFilter extends OncePerRequestFilter {

    private final DriverRepository driverRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                String phoneNumber = jwt.getSubject();
                Optional<Driver> driver = driverRepository.findByPhoneNumber(phoneNumber);
                driver.ifPresent(DriverContext::setCurrentDriver);
            }
            filterChain.doFilter(request, response);
        } finally {
            DriverContext.clear();
        }
    }
}
