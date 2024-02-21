package com.exam.in.config;
import com.exam.in.service.impl.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Get token
        String requestTokenHeader = request.getHeader("Authorization");

        // Bearer 23r3443hkjfgk
        logger.debug("Request Token Header: {}", requestTokenHeader);

        String username = null;
        String token = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
            token = requestTokenHeader.substring(7);
            if (token != null) {
                try {
                    // Give token to jwtHelper to get username
                    username = this.jwtHelper.extractUsername(token);
                } catch (IllegalArgumentException e) {
                    logger.error("Unable to get jwt token", e);
                } catch (ExpiredJwtException e) {
                    logger.error("JWT token has expired", e);
                } catch (MalformedJwtException e) {
                    logger.error("Invalid jwt", e);
                }
            } else {
                logger.warn("Token is null");
            }
        } else {
            logger.warn("JWT token does not begin with Bearer");
        }

        // Once we get the token, now validate
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

            if (this.jwtHelper.validateToken(token, userDetails)) {
                // Token is valid
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.warn("Invalid jwt token");
            }
        } else {
            logger.warn("Username is null or context is not null");
        }

        filterChain.doFilter(request, response);
    }
}