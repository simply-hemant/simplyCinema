package com.simply.Cinema.service.auth;

import com.simply.Cinema.core.user.dto.UserProfileDto;
import com.simply.Cinema.core.user.dto.UserRegistrationDto;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.UserException;

import java.util.List;

public interface AdminService {


  //  SystemAnalyticsDto getSystemAnalytics();

    UserProfileDto manageUser(Long userId) throws ResourceNotFoundException;

    List<UserProfileDto> getAllUsers();

  //  ModerationResponseDto moderateContent(Long contentId);


}
