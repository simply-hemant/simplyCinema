package com.simply.Cinema.core.user.dto;

import lombok.Data;

@Data
public class EmailOtpLoginDto {

//    @NotBlank(message = "Email is required")
//    @Email(message = "Please provide a valid email address")
    private String email;
    private String otp;
}
