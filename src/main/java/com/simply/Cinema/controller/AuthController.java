package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.OtpDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.response.AuthResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepo userRepo;

    @PostMapping("/signup/pass")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegistrationDto req) throws UserException{

        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
      //  res.setRole(UserRoleEnum.ROLE_CUSTOMER);

        List<UserRoleEnum> roles = new ArrayList<>();
        roles.add(UserRoleEnum.ROLE_CUSTOMER);
        res.setRoles(roles); // 🔁 not setRole()


        return ResponseEntity.ok(res);
    }

    @PostMapping("/login/pass")
    public ResponseEntity<AuthResponse> loginUser(
            @RequestBody UserLoginDto req) throws UserException{

        AuthResponse authResponse = authService.loginUser(req);

        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/send-otp/email")
    public ResponseEntity<String> sendOtpEmail(@RequestBody OtpDto req) throws UserException, MessagingException {

        String email = req.getEmail();

        if (email == null || email.isBlank()) {
            throw new UserException("Email cannot be empty.");
        }

        if (userRepo.existsByEmail(email)) {
            // If email is already registered, send OTP for login
            authService.sendEmailOtpForLogin(email);
            return ResponseEntity.ok("OTP sent successfully to your email for login.");
        } else {
            // If email is not registered, send OTP for signup
            authService.sendEmailOtpForSignup(email);
            return ResponseEntity.ok("OTP sent successfully to your email for signup.");
        }

    }

    @PostMapping("/signup/verify-otp-register")
    public ResponseEntity<AuthResponse> verifyOtpAndRegister(@RequestBody UserRegistrationDto req) throws UserException {

        if ((req.getEmail() == null || req.getEmail().isBlank()) && (req.getPhone() == null || req.getPhone().isBlank())) {
            throw new UserException("Email or Phone is required.");
        }

        String token;

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            // 🔹 Email OTP flow
            token = authService.verifyEmailOtpAndRegister(req);
        } else if (req.getPhone() != null && !req.getPhone().isBlank()) {
            // 🔹 Phone OTP flow
            token = authService.verifyPhoneOtpAndRegister(req);
        } else {
            throw new UserException("Invalid input.");
        }

        AuthResponse res = new AuthResponse();
        res.setMessage("register success");
        res.setRoles(List.of(UserRoleEnum.ROLE_CUSTOMER));

        return ResponseEntity.ok(res);

    }

    @PostMapping("/login/email-otp")
    public ResponseEntity<AuthResponse> loginWithEmailOtp(@RequestBody OtpDto req) throws UserException
    {
        if ((req.getEmail() == null || req.getEmail().isBlank()) && (req.getPhone() == null || req.getPhone().isBlank())) {
            throw new UserException("Email or Phone is required.");
        }

        AuthResponse authResponse;

        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            // 🔹 Email OTP Login
            authResponse = authService.loginWithEmailOtp(req);
        } else if (req.getPhone() != null && !req.getPhone().isBlank()) {
            // 🔹 Phone OTP Login
            authResponse = authService.loginWithPhoneOtp(req);
        } else {
            throw new UserException("Invalid input.");
        }
        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/send-otp/phone")
    public ResponseEntity<String> sendOtpPhone(@RequestBody OtpDto req) throws UserException, MessagingException {

        String phone = req.getPhone();

        if (phone == null || phone.isBlank()) {
            throw new UserException("phone cannot be empty.");
        }

        if (userRepo.existsByPhone(phone)) {
            // If phone is already registered, send OTP for login
            authService.sendPhoneOtpForLogin(phone);
            return ResponseEntity.ok("OTP sent successfully to your Phone for login.");
        } else {
            // If phone is not registered, send OTP for signup
            authService.sendPhoneOtpForSignup(phone);
            return ResponseEntity.ok("OTP sent successfully to your Phone for signup.");
        }
    }


}
