package com.simply.Cinema.core.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.service.config.AuditLogService;
import jakarta.persistence.Convert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepo userRepo;
    private final AuditLogService auditLogService;

    public User save(User user){

        User user1 = new User();

        user1.setFirstName(user.getFirstName());
        user1.setLastName(user.getLastName());
        user1.setEmail(user.getEmail());

        User savedUser = userRepo.save(user1);

        // Convert new object to JSON string
        String newValue = convertToJson(savedUser);

        // Call audit log
        auditLogService.logEvent("users",
                savedUser.getId(),
                "CREATE",
                null,
                newValue,
                user1.getId()); // You can replace 1L with logged-in user id later

        return savedUser;
    }

    private String convertToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            return "{}";
        }
    }

}
