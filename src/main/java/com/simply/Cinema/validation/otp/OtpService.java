package com.simply.Cinema.validation.otp;

import com.simply.Cinema.core.user.emun.UserRoleEnum;
import com.simply.Cinema.exception.UserException;
import jakarta.mail.MessagingException;

public interface OtpService {

    void sendOtp(String contact, String contactType) throws UserException, MessagingException;

    void verifyOtp(String contact, String contactType, String otp) throws UserException;

}
