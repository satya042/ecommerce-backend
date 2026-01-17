package com.ecommerce.userservice.security;

import com.ecommerce.userservice.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
//    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("JWT Filter: {} {}", request.getMethod(), request.getRequestURI());
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.debug("JWT Filter: No valid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = authorizationHeader.substring(7);

        // Check if access token is blacklisted (only for logged-out tokens)
//        if (userService.isTokenBlacklisted(jwt)) {
//            logger.warn("Access token has been blacklisted");
//            filterChain.doFilter(request, response);
//            return;
//        }

        // Validate token by signature only (no DB lookup for access token)
        if (!jwtUtil.validateToken(jwtToken)) {
            log.warn("JWT Filter: Invalid access token signature");
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtUtil.extractUsername(jwtToken);
        log.debug("JWT Filter: Token validated for user={}", username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = userService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, List.of());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            log.info("JWT Filter: Authentication set for user={}", username);
        }
        filterChain.doFilter(request, response);
    }
}
