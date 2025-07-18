package com.simply.Cinema.service.UserService;

import com.simply.Cinema.core.user.dto.ChangeEmailOrPhoneDto;
import com.simply.Cinema.core.user.dto.UserPreferencesDto;
import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserPreferences;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.security.jwt.JwtProvider;
import com.simply.Cinema.validation.otp.OtpService;
import com.simply.Cinema.validation.otp.OtpVerificationCode;
import com.simply.Cinema.validation.otp.OtpVerificationRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final JwtProvider jwtProvider;
    private final OtpService otpService;
    private final OtpVerificationRepo otpVerificationRepo;

    @Override
    public User updateUserProfile(Long userId, UserProfileDto dto) throws UserException {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with ID: " + userId));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getProfilePictureUrl() != null) user.setProfilePictureUrl(dto.getProfilePictureUrl());
        if (dto.getPreferredLanguage() != null) user.setPreferredLanguage(dto.getPreferredLanguage());
        if (dto.getPreferredCityId() != null) user.setPreferredCityId(dto.getPreferredCityId());
        if (dto.getMoodPreferences() != null) user.setMoodPreferences(dto.getMoodPreferences());

        return userRepo.save(user);
    }


    @Override
    public UserProfileDto getUserById(Long userId) throws UserException {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with ID: " + userId));

        UserProfileDto dto = new UserProfileDto();

        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender());
        dto.setLoyaltyPoints(user.getLoyaltyPoints());

        List<String> roleNames = new ArrayList<>();

        for (UserRole userRole : user.getRoles()) {
            roleNames.add(userRole.getRole().name()); // Convert Enum to String
        }

        dto.setRoles(roleNames);

        return dto;

    }

    @Override
    public void deleteUser(Long userId) throws UserException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found with ID: " + userId));
        userRepo.delete(user);
    }

    @Override
    public User updateUserPreferences(Long userId, UserPreferencesDto dto) throws UserException {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found."));

        UserPreferences pref = user.getUserPreferences();

        if(pref == null){
            pref = new UserPreferences();
            pref.setUser(user);
        }

        pref.setPreferredGenres(dto.getPreferredGenres());
        pref.setPreferredLanguages(dto.getPreferredLanguages());
        pref.setSeatPreference(dto.getSeatPreference());

        pref.setNotificationEmail(dto.getNotificationEmail());
        pref.setNotificationSms(dto.getNotificationSms());
        pref.setNotificationPush(dto.getNotificationPush());
        pref.setMarketingEmails(dto.getMarketingEmails());

        user.setUserPreferences(pref); // cascade will take care
        return userRepo.save(user);

    }

    @Override
    public UserPreferencesDto getUserPreferences(Long userId) throws UserException {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found."));

        UserPreferences prefs = user.getUserPreferences();
        if (prefs == null) {
            throw new UserException("Preferences not set for user.");
        }

        //Populate DTO directly with Enums
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setPreferredGenres(prefs.getPreferredGenres());
        dto.setPreferredLanguages(prefs.getPreferredLanguages());
        dto.setSeatPreference(prefs.getSeatPreference());

        //  Set notification and marketing flags
        dto.setNotificationEmail(prefs.getNotificationEmail());
        dto.setNotificationSms(prefs.getNotificationSms());
        dto.setNotificationPush(prefs.getNotificationPush());
        dto.setMarketingEmails(prefs.getMarketingEmails());

        return dto;
    }


    @Override
    public UserProfileDto findUserBYJwtToken(String jwt) throws UserException {

        String email = jwtProvider.getEmailFromJwtToken(jwt);

        return this.getUserByEmail(email);
    }

    @Override
    public UserProfileDto getUserByEmail(String email) throws UserException {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found with email: " + email));

        UserProfileDto dto = new UserProfileDto();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setGender(user.getGender());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setIsEmailVerified(user.getIsEmailVerified());
        dto.setIsPhoneVerified(user.getIsPhoneVerified());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setPreferredCityId(user.getPreferredCityId());

//        // Correct role mapping
//        List<String> roleNames = user.getRoles().stream()
//                .map(userRole -> userRole.getRole().name()) // Convert Enum to String
//                .toList();

        List<String> roleNames = new ArrayList<>();

        for (UserRole userRole : user.getRoles()) {
            roleNames.add(userRole.getRole().name()); // Convert Enum to String
        }

        dto.setRoles(roleNames);

        return dto;
    }

    @Override
    public void sendEmailChangeOtp(String newEmail) throws UserException, MessagingException {

        if (userRepo.existsByEmail(newEmail)) {
            throw new UserException("Email already in use.");
        }

        otpService.sendOtp(newEmail);

    }

    @Override
    public void verifyAndChangeEmail(Long userId, ChangeEmailOrPhoneDto req) throws UserException {

        String newEmail = req.getNewEmail();
        String otp = req.getOtp();

        // Check if OTP exists for this email
        OtpVerificationCode otpCode = otpVerificationRepo.findByEmail(newEmail);

        if (otpCode == null)
            throw new UserException("OTP not requested for this email.");

        if (otpCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpVerificationRepo.delete(otpCode);
            throw new UserException("OTP has expired.");
        }

        if (otpCode.getAttempts() >= 5) {
            otpVerificationRepo.delete(otpCode);
            throw new UserException("Maximum OTP attempts exceeded.");
        }

        if (!otpCode.getOtp().equals(otp)) {
            otpCode.setAttempts(otpCode.getAttempts() + 1);
            otpVerificationRepo.save(otpCode);
            throw new UserException("Invalid OTP.");
        }

        // ✅ Verified OTP, now update email
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserException("User not found"));

        user.setEmail(newEmail);
        user.setIsEmailVerified(true);

        userRepo.save(user);
        otpVerificationRepo.delete(otpCode);
    }
}
