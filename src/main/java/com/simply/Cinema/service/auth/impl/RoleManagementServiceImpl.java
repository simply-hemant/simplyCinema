package com.simply.Cinema.service.auth.impl;

import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.auth.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleManagementServiceImpl implements RoleManagementService {

    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;

    @Override
    public void assignRole(Long userId, String roleName) throws UserException {

        // 🔐 Get current authenticated user's email
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String adminEmail = auth.getName();

        // ✅ Fetch admin
        User admin = userRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new UserException("Admin not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));

        //convert string to enum
        UserRoleEnum roleEnum;
        try {
            roleEnum = UserRoleEnum.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + roleName);
        }

        // ❌ Prevent duplicate role assignment
        for (UserRole existingRole : user.getRoles()) {
            if (existingRole.getRole() == roleEnum) {
                throw new UserException("User already has role: " + roleName);
            }
        }

        UserRole newUserRole = new UserRole();
        newUserRole.setUser(user);
        newUserRole.setRole(roleEnum);
        newUserRole.setAssignedBy(admin.getId());

        user.getRoles().add(newUserRole);
        userRepo.save(user);

    }

    @Override
    public Set<String> getRolesByUser(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id " + userId));

        Set<String> roleNames = new HashSet<>();

        List<UserRole> userRoles = user.getRoles();  // Assuming User has getRoles() mapped

        for (UserRole userRole : userRoles) {
            if (userRole.getIsActive() != null && userRole.getIsActive()) {
                roleNames.add(userRole.getRole().name());
            }
        }
        return roleNames;
    }


    @Override
    public void deleteRole(Long roleId) {

        // ✅ Find the role by ID
        UserRole userRole = userRoleRepo.findById(roleId)
                .orElseThrow(() -> new UserException("Role not found with ID: " + roleId));

        // ✅ Delete the role
        userRoleRepo.delete(userRole);
    }

}