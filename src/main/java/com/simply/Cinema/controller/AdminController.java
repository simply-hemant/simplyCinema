package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.UserService.UserService;
import com.simply.Cinema.service.auth.AdminService;
import com.simply.Cinema.service.auth.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final RoleManagementService roleManagementService;
    private final UserService userService;

    @GetMapping("/all/users")
    public ResponseEntity<List<UserProfileDto>> getAllUsers(){

        return ResponseEntity.ok(adminService.getAllUsers());

    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable long userId){

        UserProfileDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);

    }

    @GetMapping("/user-role/{userId}")
    public ResponseEntity<Set<String>> getUserRole(@PathVariable long userId){

        Set<String> roles = roleManagementService.getRolesByUser(userId);
        return ResponseEntity.ok(roles);

    }

    // ------------Role management--------------
    @PostMapping("/assign")
    public ResponseEntity<String> assignRole (
            @RequestParam Long userId,
            @RequestParam String roleName)throws BusinessException, ResourceNotFoundException {

        try{
            roleManagementService.assignRole(userId, roleName);
            return ResponseEntity.ok("Role '" + roleName + "' assigned to user " + userId);
        } catch (BusinessException | ResourceNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<String> deleteUserRole(@PathVariable Long roleId) {
        roleManagementService.deleteRole(roleId);  // soft delete
        return ResponseEntity.ok("Role deactivated (soft deleted) successfully.");
    }


}


