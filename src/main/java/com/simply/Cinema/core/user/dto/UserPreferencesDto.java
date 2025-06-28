package com.simply.Cinema.core.user.dto;

import com.simply.Cinema.core.user.Enum.GenreType;
import com.simply.Cinema.core.user.Enum.Language;
import com.simply.Cinema.core.user.Enum.SeatPreference;
import lombok.Data;

import java.util.List;

@Data
public class UserPreferencesDto {

    private List<GenreType> preferredGenres;
    private List<Language> preferredLanguages;
    private Boolean notificationEmail;
    private Boolean notificationSms;
    private Boolean notificationPush;
    private Boolean marketingEmails;
    private SeatPreference seatPreference;
}
