package com.simply.Cinema.core.user.controller;

import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.service.CreateUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateUser {

    private final CreateUserService createUserService;

    @PostMapping("/create")
    public ResponseEntity<User> saveUser(
            @RequestBody User user
    ){
        User savedUser = createUserService.save(user);

        return ResponseEntity.ok(savedUser);
    }

}
