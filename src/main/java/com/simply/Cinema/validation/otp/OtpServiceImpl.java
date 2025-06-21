package com.simply.Cinema.validation.otp;

import com.simply.Cinema.core.user.emun.UserRoleEnum;
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
    public void sendOtp(String contact, String contactType) throws UserException, MessagingException {

        String otp = OtpUtils.generateOtp();
        OtpVerification otpVerification;

        if(contactType.equalsIgnoreCase("EMAIL")){
            otpVerification = otpVerificationRepo.findByEmail(contact);

            if(otpVerification == null){
                otpVerification = new OtpVerification();
                otpVerification.setEmail(contact);
            }

            otpVerification.setOtp(otp);
            otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(2));
            otpVerificationRepo.save(otpVerification);

            emailService.sendVerificationOtpEmail(contact, otp);
        } else if (contactType.equalsIgnoreCase("PHONE")) {
                otpVerification = otpVerificationRepo.findByPhone(contact);
                if(otpVerification == null){
                    otpVerification = new OtpVerification();
                    otpVerification.setPhone(contact);
                }
            otpVerification.setOtp(otp);
            otpVerification.setExpiryTime(LocalDateTime.now().plusMinutes(2));
            otpVerificationRepo.save(otpVerification);

                smsService.sendSms(contact, otp);
        }else {
            throw new UserException("Invalid contact type.");
        }

    }

    @Override
    public void verifyOtp(String contact, String contactType, String otp) throws UserException {

        OtpVerification otpVerification;

        if (contactType.equalsIgnoreCase("EMAIL")) {
            otpVerification = otpVerificationRepo.findByEmail(contact);
        } else if (contactType.equalsIgnoreCase("PHONE")) {
            otpVerification = otpVerificationRepo.findByPhone(contact);
        } else {
            throw new UserException("Invalid contact type.");
        }

        if (otpVerification == null) {
            throw new UserException("No OTP request found for the provided contact.");
        }

        if (otpVerification.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new UserException("OTP has expired.");
        }

        if (!otpVerification.getOtp().equals(otp)) {
            throw new UserException("Invalid OTP.");
        }


        // ✅ OTP is correct
        // Optionally, mark OTP as used or delete the OTP record
      //  otpVerificationRepo.delete(otpVerification);

    }
}
