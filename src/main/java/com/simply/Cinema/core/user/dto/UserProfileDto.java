package com.simply.Cinema.core.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserProfileDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String dateOfBirth; // Can also be LocalDate if required
    private String gender;
    private String profilePictureUrl;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private String preferredLanguage;
    private Long preferredCityId;
    private Integer loyaltyPoints;
    private String moodPreferences;
    private List<String> roles;

}
