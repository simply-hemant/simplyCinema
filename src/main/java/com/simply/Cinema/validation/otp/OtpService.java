package com.simply.Cinema.validation.otp;

import com.simply.Cinema.exception.UserException;
import jakarta.mail.MessagingException;

public interface OtpService {

    void sendOtp(String contact) throws UserException, MessagingException;

    void verifyOtp(String contact, String otp) throws UserException;

}
