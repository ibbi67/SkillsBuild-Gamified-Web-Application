package com.example.backend.config;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import com.example.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (
            request.getRequestURI().startsWith("/auth/login") ||
            request.getRequestURI().startsWith("/auth/signup") ||
            request.getRequestURI().startsWith("/auth/refresh") ||
            request.getRequestURI().startsWith("/v3/api-docs") ||
            request.getRequestURI().startsWith("/swagger-ui")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            unauthorizedResponse(response, "No authentication tokens found");
            return;
        }

        String accessToken = null;
        String refreshToken = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                accessToken = cookie.getValue();
            } else if (cookie.getName().equals("refresh_token")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null && !jwtService.verifyToken(accessToken)) {
            unauthorizedResponse(response, "Missing authentication tokens");
            return;
        }

        try {
            if (jwtService.verifyToken(accessToken)) {
                User user = jwtService.getUserDetails(accessToken);
                SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtService.verifyToken(refreshToken)) {
                User user = jwtService.getUserDetails(refreshToken);
                String username = jwtService.getUserDetails(refreshToken).getUsername();

                // Generate new access token
                response.addCookie(jwtService.generateAccessTokenCookie(username));

                SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
                filterChain.doFilter(request, response);
                return;
            }

            unauthorizedResponse(response, "Invalid tokens");
        } catch (Exception e) {
            unauthorizedResponse(response, "Authentication failed");
        }
    }

    private void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        new ObjectMapper()
            .writeValue(response.getOutputStream(), ApiResponse.failed(HttpStatus.UNAUTHORIZED.value(), message));
    }
}
