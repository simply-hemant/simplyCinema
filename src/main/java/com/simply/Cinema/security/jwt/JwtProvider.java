package com.simply.Cinema.security.jwt;

import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtProvider {

    // 24-hour expiration
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 1000;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());
    }

    // ✅ Spring Flow: Email + Password
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roleNames = new ArrayList<>();
        for (UserRole role : user.getRoles()) {
            if (Boolean.TRUE.equals(role.getIsActive())) {
                roleNames.add(role.getRole().name()); // e.g., "ROLE_CUSTOMER"
            }
        }

        claims.put("roles", roleNames);       // ✅ correct claim key
        claims.put("userId", user.getId());

        return Jwts.builder()
                .setSubject(user.getEmail())                // ✅ this is your "email"
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // explicitly use HS256
                .compact();
    }

    public String generateTokenDirect(String subject, List<UserRoleEnum> roles) {
        Map<String, Object> claims = new HashMap<>();

        // Convert enums to strings
        List<String> roleNames = new ArrayList<>();
        for (UserRoleEnum role : roles) {
            roleNames.add(role.name()); // e.g., "ROLE_CUSTOMER"
        }

        claims.put("roles", roleNames);
        claims.put("loginType", "OTP"); // Optional: mark this as OTP login

        return Jwts.builder()
                .setSubject(subject) // subject = email or phone
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 hour
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // explicitly use HS256
                .compact();
    }



    public String getEmailFromJwtToken(String jwt) {
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        } else {
            throw new IllegalArgumentException("Invalid Token Format");
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return claims.getSubject(); // This gives you the email
    }



}
