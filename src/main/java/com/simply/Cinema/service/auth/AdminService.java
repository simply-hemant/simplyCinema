package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.exception.UserException;

public interface AdminService {

    User createAdmin(UserRegistrationDto req) throws UserException;
    User createTheatreOwner(UserRegistrationDto req) throws UserException;


}
