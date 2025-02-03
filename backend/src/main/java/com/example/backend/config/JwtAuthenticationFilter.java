package com.example.backend.config;

import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.User;
import com.example.backend.service.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!token.startsWith("Bearer ")) {
            ApiResponse<Void> apiResponse = ApiResponse.failed(401, "Invalid token format");
            response.setStatus(apiResponse.getStatus());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getOutputStream(), apiResponse);
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        if (!jwtService.verifyToken(token)) {
            ApiResponse<Void> apiResponse = ApiResponse.failed(401, "Invalid token");
            response.setStatus(apiResponse.getStatus());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getOutputStream(), apiResponse);
            filterChain.doFilter(request, response);
            return;
        }

        User userDetails = jwtService.getUserDetails(token);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                null,
                AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
