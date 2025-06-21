package com.simply.Cinema.service.auth.impl;

import com.simply.Cinema.core.user.dto.EmailOtpLoginDto;
import com.simply.Cinema.core.user.dto.PhoneOtpLoginDto;
import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.emun.UserRoleEnum;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.entity.UserRole;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.response.AuthResponse;
import com.simply.Cinema.security.CustomUserDetailsService;
import com.simply.Cinema.security.jwt.JwtProvider;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.validation.otp.OtpService;
import com.simply.Cinema.validation.otp.OtpVerification;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepo userRoleRepo;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public String createUser(UserRegistrationDto req) throws UserException {

        if (userRepo.existsByEmail(req.getEmail())){
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

        UserRole customerRole = new UserRole();

        customerRole.setRole(UserRoleEnum.CUSTOMER);
        customerRole.setAssignedAt(LocalDateTime.now());
        customerRole.setIsActive(true);
        customerRole.setUser(savedUser);

        userRoleRepo.save(customerRole);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(UserRoleEnum.CUSTOMER.toString()));

//        // 🔐 Authenticate and generate JWT directly
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
//        );

 //       String token = jwtProvider.generateToken(authentication);
        String token = jwtProvider.generateTokenDirect(savedUser.getEmail(), List.of("ROLE_CUSTOMER"));

        return token;
    }

    @Override
    public AuthResponse loginUser(UserLoginDto req) throws UserException {

        try {
             //🔐 Authenticate the user using Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            // ✅ Generate JWT Token using JwtProvider
            String token = jwtProvider.generateToken(authentication);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(token);
            authResponse.setMessage("Login success");

            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String roleName=authorities.isEmpty()?null:authorities.iterator().next().getAuthority();

          //  authResponse.setRole(UserRoleEnum.valueOf(roleName));

            if (roleName != null && roleName.startsWith("ROLE_")) {
                roleName = roleName.substring(5); // remove "ROLE_" prefix
            }

            // ✅ Set role in response
            if (roleName != null) {
                authResponse.setRole(UserRoleEnum.valueOf(roleName)); // Convert string to enum and set
            }

            return authResponse;

        } catch (Exception ex) {
            throw new UserException("Invalid email or password.");
        }
    }

    @Override
    public void sendEmailOtpForSignup(String email) throws UserException, MessagingException {

    }

    @Override
    public String verifyEmailOtpAndRegister(OtpVerification req) throws UserException {
        return "";
    }

    @Override
    public void sendPhoneOtpForSignup(String phone, UserRoleEnum role) throws UserException {

    }

    @Override
    public String verifyPhoneOtpAndRegister(OtpVerification req) throws UserException {
        return "";
    }

    @Override
    public void sendEmailOtpForLogin(String email) throws UserException, MessagingException {

    }

    @Override
    public String loginWithEmailOtp(EmailOtpLoginDto req) throws UserException {
        return "";
    }

    @Override
    public void sendPhoneOtpForLogin(String phone) throws UserException, MessagingException {

    }

    @Override
    public String loginWithPhoneOtp(PhoneOtpLoginDto req) throws UserException {
        return "";
    }

    private Authentication customAuthenticate(String email, String rawPassword) throws UserException {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new UserException("User not found.");
        }

        // ✅ Validate password (since you are handling normal login here)
        if (!passwordEncoder.matches(rawPassword, userDetails.getPassword())) {
            throw new UserException("Invalid password.");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

}
