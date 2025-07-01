package com.simply.Cinema.service.location_and_venue;

import com.simply.Cinema.core.location_and_venue.dto.OperatingHoursDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreRequestDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreResponseDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;


import java.nio.file.AccessDeniedException;
import java.util.List;

public interface TheatreService {

    TheatreResponseDto createTheatre(TheatreRequestDto requestDto) throws BusinessException;

    TheatreResponseDto updateTheatre(Long theatreId, TheatreRequestDto requestDto) throws ResourceNotFoundException, BusinessException;

    void deleteTheatre(Long theatreId) throws ResourceNotFoundException, AuthorizationException;

    TheatreResponseDto getTheatreById(Long theatreId) throws ResourceNotFoundException;

    List<TheatreResponseDto> getTheatreByCity (Long cityId) throws ResourceNotFoundException;

    Page<TheatreResponseDto> getAllTheatre(int pageNo, int pageSize);

    List<TheatreResponseDto> getTheatreByOwner(Long requestedOwnerId, Long currentUserId, boolean isAdmin) throws ResourceNotFoundException, AccessDeniedException ;

    List<TheatreResponseDto> getTheatreByAmenities(List<String> amenities) throws ResourceNotFoundException;

    TheatreResponseDto updateTheatreOperatingHours(Long theatreId, OperatingHoursDto hoursDto) throws ResourceNotFoundException;

    public Page<TheatreResponseDto> searchTheatre(String keyword, int pageNo, int pageSize);

}
