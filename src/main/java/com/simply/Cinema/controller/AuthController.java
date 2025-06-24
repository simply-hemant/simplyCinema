package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.EmailOtpDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.emun.UserRoleEnum;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.response.AuthResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;

    @PostMapping("/signup/pass")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegistrationDto req) throws UserException{

        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
        res.setRole(UserRoleEnum.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/login/pass")
    public ResponseEntity<AuthResponse> loginUser(
            @RequestBody UserLoginDto req) throws UserException{

        AuthResponse authResponse = authService.loginUser(req);

        return ResponseEntity.ok(authResponse);

    }

    @PostMapping("/send-otp/email")
    public ResponseEntity<String> sendOtpEmail(@RequestBody EmailOtpDto req) throws UserException, MessagingException {

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

        String token = authService.verifyEmailOtpAndRegister(req);

        AuthResponse res = new AuthResponse();
        res.setMessage("register success");
        res.setRole(UserRoleEnum.ROLE_CUSTOMER);

        return ResponseEntity.ok(res);

    }

    @PostMapping("/login/email-otp")
    public ResponseEntity<AuthResponse> loginWithEmailOtp(@RequestBody EmailOtpDto req) throws UserException, MessagingException
    {
        AuthResponse authResponse = authService.loginWithEmailOtp(req);

        return ResponseEntity.ok(authResponse);

    }


}
