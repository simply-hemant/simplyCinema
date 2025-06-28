package com.simply.Cinema.validation.otp;

import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.validation.sms.SmsService;
import com.simply.Cinema.validation.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepo otpVerificationRepo;
    private final EmailService emailService;
    private final SmsService smsService;

    @Override
    public void sendOtp(String contact) throws UserException, MessagingException {

        String otp = OtpUtils.generateOtp();
        OtpVerificationCode otpVerificationCode;
        if (isEmail(contact)) {
            otpVerificationCode = otpVerificationRepo.findByEmail(contact);

            if (otpVerificationCode == null) {
                otpVerificationCode = new OtpVerificationCode();
                otpVerificationCode.setEmail(contact);
            }

            otpVerificationCode.setOtp(otp);
            otpVerificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(2));
            otpVerificationRepo.save(otpVerificationCode);

            emailService.sendVerificationOtpEmail(contact, otp);

        } else if (isPhone(contact)) {

            String formattedPhone = formatPhoneNumber(contact);

            otpVerificationCode = otpVerificationRepo.findByPhone(contact);

            if (otpVerificationCode == null) {
                otpVerificationCode = new OtpVerificationCode();
                otpVerificationCode.setPhone(formattedPhone);
            }

            otpVerificationCode.setOtp(otp);
            otpVerificationCode.setExpiryTime(LocalDateTime.now().plusMinutes(2));
            otpVerificationRepo.save(otpVerificationCode);

            smsService.sendSms(formattedPhone, otp);

        } else {
            throw new UserException("Invalid contact format.");
        }
    }

    @Override
    public void verifyOtp(String contact, String otp) throws UserException {

        OtpVerificationCode otpVerificationCode;

        if (isEmail(contact)) {
            otpVerificationCode = otpVerificationRepo.findByEmail(contact);
        } else if (isPhone(contact)) {
            String formattedPhone = formatPhoneNumber(contact);

            otpVerificationCode = otpVerificationRepo.findByPhone(contact);
        } else {
            throw new UserException("Invalid contact format.");
        }

        if (otpVerificationCode == null) {
            throw new UserException("No OTP request found for the provided contact.");
        }

        if (otpVerificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new UserException("OTP has expired.");
        }

        if (otpVerificationCode.getAttempts() >= 5) {
            throw new UserException("Maximum OTP attempts exceeded. Please request a new OTP.");
        }

        if (!otpVerificationCode.getOtp().equals(otp)) {
            otpVerificationCode.setAttempts(otpVerificationCode.getAttempts() + 1);
            otpVerificationRepo.save(otpVerificationCode);
            throw new UserException("Invalid OTP.");
        }

        // ✅ OTP is correct, delete OTP
       // otpVerificationRepo.delete(otpVerificationCode);

    }

    private boolean isEmail(String contact) {
        return contact.contains("@");
    }

    private boolean isPhone(String contact) {

        // Allow with or without country code
        return contact.matches("^\\+?\\d{10,15}$"); // supports +91xxxxxxxxxx or xxxxxxxxxx

    }

    private String formatPhoneNumber(String phone) {
        if (phone.startsWith("+")) {
            return phone;
        }
        return "+91" + phone; // assuming India. You can make this dynamic later.
    }

}
