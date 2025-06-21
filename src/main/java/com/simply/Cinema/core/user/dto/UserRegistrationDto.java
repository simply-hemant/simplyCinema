package com.simply.Cinema.core.user.dto;

import com.simply.Cinema.core.user.emun.UserRoleEnum;
import lombok.Data;

@Data
public class UserRegistrationDto {

        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        private String dateOfBirth; // Consider using String for input, convert to LocalDate in service layer
        private String gender;
        private String preferredLanguage;
        private Long preferredCityId;
        private UserRoleEnum role;

}
