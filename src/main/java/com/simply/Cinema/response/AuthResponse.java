package com.simply.Cinema.response;

import com.simply.Cinema.core.user.emun.UserRoleEnum;
import lombok.Data;

@Data
public class AuthResponse {

    private String jwt;
    private String message;
    private UserRoleEnum role;

}
