package com.example.hello_spring.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    // 🔑 Secret key (later move to application.properties)
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // ⏰ Token validity (1 hour)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    // =============================
    // CREATE TOKEN
    // =============================
    public String generateToken(String username, List<String> roles) {

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }

    // =============================
    // VALIDATE TOKEN
    // =============================
    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =============================
    // EXTRACT USERNAME
    // =============================
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // =============================
    // EXTRACT ROLES
    // =============================
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractAllClaims(token).get("roles");
    }

    // =============================
    // INTERNAL: PARSE CLAIMS
    // =============================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
