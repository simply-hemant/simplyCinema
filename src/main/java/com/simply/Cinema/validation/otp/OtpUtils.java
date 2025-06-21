package com.simply.Cinema.validation.otp;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class OtpUtils {

    private OtpUtils() {
        // Prevent instantiation
    }

    public static String generateOtp(){
        return generateOtp(6);
    }

    public static String generateOtp(int otpLength){

        Random random = new Random();
        StringBuilder otp = new StringBuilder(otpLength);

        for(int i=0; i<otpLength; i++){
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }

}
