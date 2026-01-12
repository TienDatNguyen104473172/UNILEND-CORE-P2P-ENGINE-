//Okay, this is the "brain" that processes Token. This class is responsible for three things: Token creation, Token decryption, and Token verification.
package com.unilend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Get the value from application.properties
    @Value("${unilend.app.jwtSecret}")
    private String jwtSecret;

    @Value("${unilend.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // 1. CREATE TOKENS FROM USER INFORMATION
    public String generateJwtToken(Authentication authentication) {
        // Get the information of the currently logged-in user.
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Create tokens
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername())) // Lưu username vào token
                .setIssuedAt(new Date()) // Thời điểm tạo
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Thời điểm hết hạn
                .signWith(key(), SignatureAlgorithm.HS256) // Ký tên bằng thuật toán HS256
                .compact();
    }

    // 2. DECODE THE TOKEN TO GET THE USERNAME
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // 3. VERIFY TOKEN VALIDITY (Ticket Verification)
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    // Sub-function: Converts the Secret string into an encrypted Key object.
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
