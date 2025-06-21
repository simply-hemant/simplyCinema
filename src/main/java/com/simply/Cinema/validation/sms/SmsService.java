package com.simply.Cinema.validation.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final TwilioConfig twilioConfig;

    public void sendSms(String toPhoneNumber, String otp) {
        Twilio.init(twilioConfig.getAccount_sid(), twilioConfig.getAuth_token());

        String messageBody = "Your OTP for login is: " + otp + "The OTP is valid for 2 minutes.\n\n Thank you!";

        Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                new com.twilio.type.PhoneNumber(twilioConfig.getPhone_number()),
                messageBody
        ).create();
    }
}
