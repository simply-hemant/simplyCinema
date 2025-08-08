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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response); // skip JWT check
            return;
        }

        String jwt = request.getHeader(JwtConstants.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7); // Remove "Bearer "

            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                System.out.println("Subject:---------------------------------------------------------- " + claims.getSubject());



                String email = claims.getSubject(); // ✅ correct
                String roles = String.join(",", (List<String>) claims.get("roles")); // ✅ fix: roles not authorities

                List<GrantedAuthority> authorities =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(roles);

                User user = userRepo.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                CustomUserDetails userDetails = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                logger.warn("Invalid token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
