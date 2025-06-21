package com.simply.Cinema.controller;

import com.simply.Cinema.core.user.dto.UserLoginDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.emun.UserRoleEnum;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.core.user.repository.UserRoleRepo;
import com.simply.Cinema.exception.UserException;
import com.simply.Cinema.response.ApiResponse;
import com.simply.Cinema.service.auth.AuthService;
import com.simply.Cinema.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepo userRepo;
    private final UserRoleRepo userRoleRepo;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegistrationDto req) throws UserException{

        String jwt = authService.createUser(req);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setMessage("register success");
        res.setRole(UserRoleEnum.CUSTOMER);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(
            @RequestBody UserLoginDto req) throws UserException{

        AuthResponse authResponse = authService.loginUser(req);

        return ResponseEntity.ok(authResponse);

    }


}
