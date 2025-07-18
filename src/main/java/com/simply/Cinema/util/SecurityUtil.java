package com.simply.Cinema.util;

import com.simply.Cinema.config.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtil {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();  // ✅ getId() comes from your CustomUserDetails wrapper
        }                                // ✅ Direct access to your User ID

        throw new RuntimeException("Unable to extract user ID from SecurityContext");
    }

//    public static boolean hasRole(String roleName) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null) {
//            return authentication.getAuthorities().stream()
//                    .anyMatch(auth -> auth.getAuthority().equals(roleName));
//        }
//        return false;
//    }

    public static boolean hasRole(String roleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_" + roleName)) {
                    return true;
                }
            }
        }
        return false;
    }


}
