package com.simply.Cinema.service.UserService;

import com.simply.Cinema.core.user.dto.ChangeEmailOrPhoneDto;
import com.simply.Cinema.core.user.dto.UserPreferencesDto;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.exception.UserException;
import jakarta.mail.MessagingException;

public interface UserService {

    User updateUserProfile(Long userId, UserProfileDto dto) throws UserException;

    User getUserById(Long userId) throws UserException;

    void deleteUser(Long userId) throws UserException;

    User updateUserPreferences(Long userId, UserPreferencesDto dto) throws UserException;

    UserPreferencesDto getUserPreferences(Long userId) throws UserException;

    UserProfileDto findUserBYJwtToken(String jwt) throws UserException;

    UserProfileDto getUserByEmail(String Email) throws UserException;

    void sendEmailChangeOtp( String newEmail) throws UserException, MessagingException;

    void verifyAndChangeEmail(Long userId, ChangeEmailOrPhoneDto req) throws UserException;


}
