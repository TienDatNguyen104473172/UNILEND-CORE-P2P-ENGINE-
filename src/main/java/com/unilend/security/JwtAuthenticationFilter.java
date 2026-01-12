//The "Filter" will check each request to see if the token is valid.
package com.unilend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Get JWT from the request (Header: Authorization)
        String jwt = parseJwt(request);
        // 2. If you have a valid JWT (the code for checking the token will be in JwtUtils)
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            // 3. Get username from JWT
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            // 4. get user information from the database.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // 5. Set the user's information in the Security Context (so Spring knows, "Ah, this guy is already logged in").
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 6. Allow the request to proceed to other filters.
        filterChain.doFilter(request, response);
    }
    // Subfunction: Get the JWT string from the "Authorization: Bearer <token>" header.
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Remove the word "Bearer" to get the token.
        }
        return null;
    }
}
