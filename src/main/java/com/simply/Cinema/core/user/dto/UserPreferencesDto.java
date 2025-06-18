package com.simply.Cinema.core.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPreferencesDto {
    private List<String> preferredGenres;
    private List<String> preferredLanguages;
    private Boolean notificationEmail;
    private Boolean notificationSms;
    private Boolean notificationPush;
    private Boolean marketingEmails;
    private String seatPreference;
}
