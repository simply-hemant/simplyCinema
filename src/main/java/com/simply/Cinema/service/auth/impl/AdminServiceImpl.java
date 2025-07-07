package com.simply.Cinema.service.auth.impl;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.auth.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepo userRepo;

    @Override
    public UserProfileDto manageUser(Long userId) throws ResourceNotFoundException {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + userId));

        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());

        // 🔑 Get user roles as strings
       dto.setRoles(getUserRoleNames(user));

        return dto;
    }

    @Override
    public List<UserProfileDto> getAllUsers() {

        List<User> users = userRepo.findAll();

        List<UserProfileDto> userDtos = new ArrayList<>();


        for(User user : users){
            UserProfileDto dto = new UserProfileDto();

            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setRoles(getUserRoleNames(user)); // or extract role names if needed
            // set other fields as needed
            userDtos.add(dto);
        }

        return userDtos;

    }


    public List<String> getUserRoleNames(User user) {
        List<String> roleNames = new ArrayList<>();

        for (UserRole role : user.getRoles()) {
            roleNames.add(role.getRole().name());
        }
        return roleNames;
    }
}
