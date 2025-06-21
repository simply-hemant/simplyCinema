package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.EmailOtpLoginDto;
import com.simply.Cinema.core.user.dto.PhoneOtpLoginDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.emun.UserRoleEnum;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.response.AuthResponse;
import com.simply.Cinema.validation.otp.OtpVerification;
import jakarta.mail.MessagingException;

public interface AuthService {

    // 🔹 Normal Signup with Email and Password (no OTP)
    String createUser(UserRegistrationDto req) throws UserException;

    // 🔹 Normal Login with Email and Password (returns JWT token)
    AuthResponse loginUser(UserLoginDto req) throws UserException;

    // 🔹 Email OTP Signup
     void sendEmailOtpForSignup(String email) throws UserException, MessagingException;
     String verifyEmailOtpAndRegister(OtpVerification req) throws UserException;

    // 🔹 Phone OTP Signup
     void sendPhoneOtpForSignup(String phone, UserRoleEnum role) throws UserException;
     String verifyPhoneOtpAndRegister(OtpVerification req) throws UserException;

    // 🔹 Email OTP Login
     void sendEmailOtpForLogin(String email) throws UserException, MessagingException;
     String loginWithEmailOtp(EmailOtpLoginDto req) throws UserException;

    // 🔹 Phone OTP Login
     void sendPhoneOtpForLogin(String phone) throws UserException, MessagingException;
     String loginWithPhoneOtp(PhoneOtpLoginDto req) throws UserException;

}
