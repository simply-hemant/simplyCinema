package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.auth.AdminService;
import com.simply.Cinema.service.auth.RoleManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final RoleManagementService roleManagementService;

    @GetMapping("/all/users")
    public ResponseEntity<List<UserProfileDto>> getAllUsers(){

        return ResponseEntity.ok(adminService.getAllUsers());

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

}


