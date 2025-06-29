package com.simply.Cinema.core.user.dto;

import lombok.Data;

@Data
public class ChangeEmailOrPhoneDto {

    private String newEmail;
    private String newPhone;
    private String otp;

}
