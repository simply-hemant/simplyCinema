package com.simply.Cinema.validation.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpVerificationRepo extends JpaRepository<OtpVerificationCode, Long> {

  //find by email or phone
  OtpVerificationCode findByEmail(String email);
  OtpVerificationCode findByPhone(String phone);
  OtpVerificationCode findByOtp(String otp);

}
