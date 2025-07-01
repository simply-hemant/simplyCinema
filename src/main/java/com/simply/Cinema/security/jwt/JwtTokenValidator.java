package com.simply.Cinema.security.jwt;

import com.simply.Cinema.config.CustomUserDetails;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {

    @Autowired
    private UserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Read token from Authorization header
        String jwt = request.getHeader(JwtConstants.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer "))
        {
            jwt = jwt.substring(7);                       // ✅ Remove the prefix

            try {
                // Create secret key
                SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

                // Parse token
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();


                // Extract email and roles
                String email = String.valueOf(claims.get("email" ));
                String authorities = String.valueOf(claims.get("authorities"));

                // Convert roles to Spring Security format
                List<GrantedAuthority> auths = AuthorityUtils
                        .commaSeparatedStringToAuthorityList(authorities);

                // ✅ Fetch user by email
                User user = userRepo.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // ✅ Wrap user in CustomUserDetails
                CustomUserDetails userDetails = new CustomUserDetails(user);

                // ✅ Create authentication token with CustomUserDetails as principal
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, auths);

//                // Create authentication object
//                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);

                // Set authentication in Spring Security Context
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                //an avoid exceptions crashing the app during token parsing:
                // Don't throw RuntimeException - just skip setting auth
                logger.warn("Invalid token: " + e.getMessage());
            }
        }
        // Continue to next filter or controller
        filterChain.doFilter(request, response);

    }
}