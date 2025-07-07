package com.simply.Cinema.service;

import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<String> roleNames = new ArrayList<>();
        for (UserRole userRole : user.getRoles()) {
            if (userRole.getIsActive()) {
                // Remove "ROLE_" prefix if needed
                String roleName = userRole.getRole().name().replace("ROLE_", "");
                roleNames.add(roleName);
            }
        }

        String[] rolesArray = new String[roleNames.size()];
        roleNames.toArray(rolesArray);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles(rolesArray)
                .build();
    }
}
