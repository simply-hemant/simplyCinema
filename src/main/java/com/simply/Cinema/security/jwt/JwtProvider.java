package com.simply.Cinema.security.jwt;

import com.simply.Cinema.core.user.emun.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtProvider {

    SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

    // 🔹 Generate JWT Token
    public String generateTokenDirect(String email, List<String> roles) {
        String roleString = String.join(",", roles);

        long EXPIRATION_TIME = 1000 * 60 * 60 * 24;  // 24 hours

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + EXPIRATION_TIME))
                .claim("email", email)
                .claim("authorities", roleString)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Generate JWT Token
    public String generateToken(Authentication auth){
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        long EXPIRATION_TIME = 1000 * 60 * 60 * 24;  // 24 hours

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date(). getTime() + EXPIRATION_TIME))
                .claim("email", auth.getName())   // Add email in token
                .claim("authorities", roles)      // Add roles in token
                .signWith(key, SignatureAlgorithm.HS256) // 🔥 Best Practice: Specify algorithm
                .compact();
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        Set<String> auths = new HashSet<>();

        for(GrantedAuthority authority : authorities){
            auths.add(authority.getAuthority());  // Extract role name
        }

        return String.join(",", auths); // Combines all roles into a single comma-separated string

    }

    public String getEmailFromJwtToken(String jwt){
        if(jwt != null && jwt.startsWith("Bearer ")){
            jwt = jwt.substring(7);
        }
        else{
            throw new IllegalArgumentException("Invalid Token Format");
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return String.valueOf(claims.get("email"));

    }

}
