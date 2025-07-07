package com.simply.Cinema.service.auth.impl;

import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.core.user.dto.OtpDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.response.AuthResponse;
import com.simply.Cinema.security.jwt.JwtProvider;
import com.simply.Cinema.service.UserDetailsServiceImpl;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.validation.otp.OtpService;
import com.simply.Cinema.validation.otp.OtpVerificationCode;
import com.simply.Cinema.validation.otp.OtpVerificationRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepo userRoleRepo;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final OtpVerificationRepo otpVerificationRepo;
    private final OtpService otpService;
    private final AuditLogService auditLogService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public String createUser(UserRegistrationDto req) throws UserException {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new UserException("Email is already registered.");
        }

        if (userRepo.existsByPhone(req.getPhone())) {
            throw new UserException("Phone number is already registered.");
        }

        User user = new User();

        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setGender(req.getGender());
        user.setPreferredLanguage(req.getPreferredLanguage());
        user.setPreferredCityId(req.getPreferredCityId());

        if (req.getDateOfBirth() != null) {
            user.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        }

        User savedUser = userRepo.save(user);
        System.out.println("User created successfully with ID: " + savedUser.getId());

        // Manual Audit Logging
        auditLogService.logEvent("User", AuditAction.CREATE, savedUser.getId(), savedUser.getId());

//        System.out.println("User ID from AuditContext: " + AuditContext.getUserId());

        UserRole customerRole = new UserRole();

        customerRole.setRole(UserRoleEnum.ROLE_CUSTOMER);
        customerRole.setAssignedAt(LocalDateTime.now());
        customerRole.setIsActive(true);
        customerRole.setUser(savedUser);

        userRoleRepo.save(customerRole);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(UserRoleEnum.ROLE_CUSTOMER.toString()));

        String token = jwtProvider.generateToken(savedUser);

        return token;
    }

    @Override
    public AuthResponse loginUser(UserLoginDto req) throws UserException {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // Get UserDetails from authentication
            User user = userRepo.findByEmail(req.getEmail())
                    .orElseThrow(() -> new UserException("User not found"));

            String token = jwtProvider.generateToken(user);

            // Extract roles
            List<UserRoleEnum> roles = new ArrayList<>();
            for (UserRole userRole : user.getRoles()) {
                roles.add(userRole.getRole());
            }

            AuthResponse authResponse = new AuthResponse();

            authResponse.setMessage("login success.");
            authResponse.setRoles(roles);
            authResponse.setJwt(token);

            return authResponse;

        } catch (Exception e) {
            throw new UserException("Invalid email or password.");
        }

    }


    @Override
    public void sendEmailOtpForSignup(String email) throws UserException, MessagingException {

        if (userRepo.existsByEmail(email)) {
            throw new UserException("Email is already registered.");
        }

        otpService.sendOtp(email);

    }

    @Override
    public String verifyEmailOtpAndRegister(UserRegistrationDto req) throws UserException {

        OtpVerificationCode otpVerificationcode = otpVerificationRepo.findByEmail(req.getEmail());

        if (otpVerificationcode == null)
            throw new UserException("Wrong Otp.");

        if (otpVerificationcode.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpVerificationRepo.delete(otpVerificationcode);
            throw new UserException("OTP has expired.");
        }

        if (otpVerificationcode.getAttempts() >= 5) {
            otpVerificationRepo.delete(otpVerificationcode);
            throw new UserException("Maximum OTP attempts exceeded.");
        }

        if (!otpVerificationcode.getOtp().equals(req.getOtp())) {
            otpVerificationcode.setAttempts(otpVerificationcode.getAttempts() + 1);
            otpVerificationRepo.save(otpVerificationcode);
            throw new UserException("Invalid OTP.");
        }

        User createdUser = new User();
        createdUser.setFirstName(req.getFirstName());
        createdUser.setLastName(req.getLastName());
        createdUser.setEmail(req.getEmail());

        if (req.getDateOfBirth() != null) {
            createdUser.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        }

        User savedUser = userRepo.save(createdUser);

        UserRole userRole = new UserRole();
        userRole.setRole(UserRoleEnum.ROLE_CUSTOMER);
        userRole.setAssignedAt(LocalDateTime.now());
        userRole.setIsActive(true);

        userRole.setUser(savedUser);

        userRoleRepo.save(userRole);

        otpVerificationRepo.delete(otpVerificationcode);

        String token = jwtProvider.generateTokenDirect(
                savedUser.getEmail(),
                List.of(UserRoleEnum.ROLE_CUSTOMER)
        );

        return token;

    }

    @Override
    public void sendPhoneOtpForSignup(String phone) throws UserException, MessagingException {

        if (userRepo.existsByPhone(phone)) {
            throw new UserException("Email is already registered.");
        }

        otpService.sendOtp(phone);
    }

    @Override
    public String verifyPhoneOtpAndRegister(UserRegistrationDto req) throws UserException {

        OtpVerificationCode otpVerificationcode = otpVerificationRepo.findByPhone(req.getPhone());

        if (otpVerificationcode == null)
            throw new UserException("Wrong Otp.");

        if (otpVerificationcode.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpVerificationRepo.delete(otpVerificationcode);
            throw new UserException("OTP has expired.");
        }

        if (otpVerificationcode.getAttempts() >= 5) {
            otpVerificationRepo.delete(otpVerificationcode);
            throw new UserException("Maximum OTP attempts exceeded.");
        }

        if (!otpVerificationcode.getOtp().equals(req.getOtp())) {
            otpVerificationcode.setAttempts(otpVerificationcode.getAttempts() + 1);
            otpVerificationRepo.save(otpVerificationcode);
            throw new UserException("Invalid OTP.");
        }

        User createdUser = new User();
        createdUser.setFirstName(req.getFirstName());
        createdUser.setLastName(req.getLastName());
        createdUser.setPhone(req.getPhone());

        if (req.getDateOfBirth() != null) {
            createdUser.setDateOfBirth(LocalDate.parse(req.getDateOfBirth()));
        }

        User savedUser = userRepo.save(createdUser);

        UserRole userRole = new UserRole();
        userRole.setRole(UserRoleEnum.ROLE_CUSTOMER);
        userRole.setAssignedAt(LocalDateTime.now());
        userRole.setIsActive(true);

        userRole.setUser(savedUser);

        userRoleRepo.save(userRole);

        otpVerificationRepo.delete(otpVerificationcode);

        String token = jwtProvider.generateTokenDirect(
                savedUser.getEmail(),
                List.of(UserRoleEnum.ROLE_CUSTOMER)
        );
        return token;

    }

    @Override
    public void sendEmailOtpForLogin(String email) throws UserException, MessagingException {


        if (!userRepo.existsByEmail(email)) {
            throw new UserException("Email is not registered.");
        }

        otpService.sendOtp(email);

    }

    @Override
    public AuthResponse loginWithEmailOtp(OtpDto req) throws UserException {

        OtpVerificationCode otpVerificationcode = otpVerificationRepo.findByEmail(req.getEmail());

        if (otpVerificationcode == null)
            throw new UserException("No otp found.");

        if (otpVerificationcode.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpVerificationRepo.delete(otpVerificationcode);
            throw new UserException("OTP has expired.");
        }

        if (otpVerificationcode.getAttempts() >= 5) {
            otpVerificationRepo.delete(otpVerificationcode);
            throw new UserException("Maximum OTP attempts exceeded.");
        }

        if (!otpVerificationcode.getOtp().equals(req.getOtp())) {
            otpVerificationcode.setAttempts(otpVerificationcode.getAttempts() + 1);
            otpVerificationRepo.save(otpVerificationcode);
            throw new UserException("Invalid OTP.");
        }

        // ✅ OTP is valid, delete it
        otpVerificationRepo.delete(otpVerificationcode);

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserException("User not found."));

        //Last Login Update
        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        //Extract roles
        List<UserRoleEnum> roles = new ArrayList<>();
        for (UserRole r : user.getRoles()) {
            if (r.getIsActive() != null && r.getIsActive()) {
                roles.add(r.getRole());
            }
        }

        if (roles.isEmpty()) {
            throw new UserException("User has no active roles.");
        }

        // ✅ Generate JWT token
        String token = jwtProvider.generateTokenDirect(user.getEmail(), roles);

        // ✅ Prepare Auth Response
        AuthResponse res = new AuthResponse();
        res.setJwt(token);
        res.setMessage("Login successful");
        res.setRoles(roles);

        return res;

    }

    @Override
    public void sendPhoneOtpForLogin(String phone) throws UserException, MessagingException {

        if (!userRepo.existsByPhone(phone)) {
            throw new UserException("Phone number is not registered.");
        }

        otpService.sendOtp(phone);
    }

    @Override
    public AuthResponse loginWithPhoneOtp(OtpDto req) throws UserException {


        OtpVerificationCode otpVerificationCode = otpVerificationRepo.findByPhone(req.getPhone());

        if (otpVerificationCode == null)
            throw new UserException("No OTP found.");

        if (otpVerificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpVerificationRepo.delete(otpVerificationCode);
            throw new UserException("OTP has expired.");
        }

        if (otpVerificationCode.getAttempts() >= 5) {
            otpVerificationRepo.delete(otpVerificationCode);
            throw new UserException("Maximum OTP attempts exceeded.");
        }

        if (!otpVerificationCode.getOtp().equals(req.getOtp())) {
            otpVerificationCode.setAttempts(otpVerificationCode.getAttempts() + 1);
            otpVerificationRepo.save(otpVerificationCode);
            throw new UserException("Invalid OTP.");
        }

        otpVerificationRepo.delete(otpVerificationCode); // ✅ OTP used

        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new UserException("User not found."));

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepo.save(user);

        //Extract roles
        List<UserRoleEnum> roles = new ArrayList<>();
        for (UserRole r : user.getRoles()) {
            if (r.getIsActive() != null && r.getIsActive()) {
                roles.add(r.getRole());
            }
        }

        if (roles.isEmpty()) {
            throw new UserException("User has no active roles.");
        }

        // ✅ Generate JWT token
        String token = jwtProvider.generateTokenDirect(user.getPhone(), roles);

        // ✅ Prepare Auth Response
        AuthResponse res = new AuthResponse();
        res.setJwt(token);
        res.setMessage("Login successful");
        res.setRoles(roles);

        return res;

    }

    private List<String> extractRoleNamesFromDB(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getRoles().stream()
                .filter(UserRole::getIsActive)
                .map(r -> r.getRole().name())
                .distinct()
                .toList();
    }


}
