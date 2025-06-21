package com.simply.Cinema.validation.otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepo extends JpaRepository<OtpVerification, Long> {

  //find by email or phone
  OtpVerification findByEmail(String email);
  OtpVerification findByPhone(String phone);
  OtpVerification findByOtp(String otp);

}
