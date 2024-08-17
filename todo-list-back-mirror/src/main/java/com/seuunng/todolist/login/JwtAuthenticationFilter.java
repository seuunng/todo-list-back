package com.seuunng.todolist.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;
        
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/auth/signup")) {
            filterChain.doFilter(request, response);
            return;
        }
        token = header.substring(7);
        if (jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            email = claims.getSubject();
            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);
            
            List<SimpleGrantedAuthority> authorities = ((List<?>) claims.get("role")).stream()
            		.map(role -> new SimpleGrantedAuthority((String) role))
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Authenticated user: " + email+ " with authorities: " + authorities);
        } else {
            logger.error("Invalid JWT Token");
        }

        filterChain.doFilter(request, response);
    }
}