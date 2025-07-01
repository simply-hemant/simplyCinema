package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.UserException;

import javax.management.relation.Role;
import java.util.List;
import java.util.Set;

public interface RoleManagementService {

    void assignRole(Long userId, String roleName) throws UserException;

    Set<String> getRolesByUser(Long userId);

 //   boolean validateAccess(Long userId, String resource); // Simplified access validation

   // Role createRole(String roleName);

    void deleteRole(Long roleId);

  //  UserProfileDto changeUserRole(Long userId, String role) throws ResourceNotFoundException, BusinessException;

}
