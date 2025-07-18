package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.ChangeEmailOrPhoneDto;
import com.simply.Cinema.core.user.dto.UserPreferencesDto;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.UserService.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(
            @RequestHeader("Authorization")String jwt) throws UserException {

        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestBody UserProfileDto dto,
            @RequestHeader("Authorization") String jwt) {

        // 🔐 Extract user from JWT
        UserProfileDto loggedInUser = userService.findUserBYJwtToken(jwt);

        // 🔄 Update profile
        User updatedUser = userService.updateUserProfile(loggedInUser.getId(), dto);

        // 🎯 Convert updated User to UserProfileDto to return
        UserProfileDto updatedDto = new UserProfileDto();
        updatedDto.setId(updatedUser.getId());
        updatedDto.setEmail(updatedUser.getEmail());
        updatedDto.setFirstName(updatedUser.getFirstName());
        updatedDto.setLastName(updatedUser.getLastName());
        updatedDto.setPhone(updatedUser.getPhone());
        updatedDto.setDateOfBirth(updatedUser.getDateOfBirth() != null
                ? updatedUser.getDateOfBirth().toString() : null);
        updatedDto.setGender(updatedUser.getGender());
        updatedDto.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        updatedDto.setPreferredLanguage(updatedUser.getPreferredLanguage());
        updatedDto.setPreferredCityId(updatedUser.getPreferredCityId());
        updatedDto.setMoodPreferences(updatedUser.getMoodPreferences());

        return ResponseEntity.ok(updatedDto);
    }


    @PostMapping("/change-email/request")
    public ResponseEntity<String> requestEmailChangeOtp(@RequestBody ChangeEmailOrPhoneDto request,
                                                        @RequestHeader("Authorization") String jwt) throws MessagingException {
        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        userService.sendEmailChangeOtp(request.getNewEmail()); // Only new email needed here
        return ResponseEntity.ok("📨 OTP sent to new email.");
    }

    @PostMapping("/change-email/confirm")
    public ResponseEntity<String> confirmEmailChange(@RequestBody ChangeEmailOrPhoneDto request,
                                                     @RequestHeader("Authorization")String jwt) {
        UserProfileDto user = userService.findUserBYJwtToken(jwt);

        userService.verifyAndChangeEmail(user.getId(), request);

        return ResponseEntity.ok("✅ Email changed successfully. Please log in again.");
    }

    @PutMapping("/preferences/update")
    public ResponseEntity<String> updateUserPreferences(@RequestBody UserPreferencesDto preferencesDto,
                                                        @RequestHeader("Authorization") String jwt) {
        try {
            UserProfileDto user = userService.findUserBYJwtToken(jwt); // Get user from token
            userService.updateUserPreferences(user.getId(), preferencesDto); // Update prefs
            return ResponseEntity.ok("✅ Preferences updated successfully.");
        } catch (UserException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferencesDto> getUserPreferences(
            @RequestHeader("Authorization") String jwt) {

        UserProfileDto user = userService.findUserBYJwtToken(jwt); // extract user from JWT
        UserPreferencesDto preferences = userService.getUserPreferences(user.getId());

        return ResponseEntity.ok(preferences);
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<String> deleteOwnAccount(@RequestHeader("Authorization") String jwt) throws UserException {

        UserProfileDto user = userService.findUserBYJwtToken(jwt);
        userService.deleteUser(user.getId());
        return ResponseEntity.ok("Your account has been deleted successfully.");
    }


    //Get user bookings
    //Upload profile picture


}
