package com.simply.Cinema.config;

import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        String adminEmail = "simply23hemant@gmail.com";

        if (userRepo.findByEmail(adminEmail).isEmpty()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setEmail(adminEmail);
            admin.setPhone("9999999999");
            admin.setPasswordHash(passwordEncoder.encode("admin123")); // encode!
            admin.setIsActive(true);

            // Create ADMIN role object
            UserRole adminRole = new UserRole();
            adminRole.setRole(UserRoleEnum.ROLE_ADMIN);
            adminRole.setUser(admin); // important — set owner

            // Set the role list in user
            admin.setRoles(List.of(adminRole));

            userRepo.save(admin);

            System.out.println("✅ Admin user created with email: " + adminEmail);
        } else {
            System.out.println("✅ Admin already exists. Skipping seeding.");
        }
    }
}
