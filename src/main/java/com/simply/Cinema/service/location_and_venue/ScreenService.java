package com.simply.Cinema.service.location_and_venue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.simply.Cinema.core.location_and_venue.dto.ScreenDto;
import com.simply.Cinema.core.location_and_venue.dto.ScreenLayoutDto;
import com.simply.Cinema.core.location_and_venue.dto.ScreenSummaryDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatDto;
import com.simply.Cinema.exception.*;

import java.util.List;

public interface ScreenService {

    ScreenDto createScreen(ScreenDto screenDto) throws BusinessException, ValidationException, ResourceNotFoundException;

    ScreenDto updateScreen(Long screenId, ScreenDto screenDto) throws ResourceNotFoundException, ValidationException, AuthorizationException, JsonProcessingException;

    void deleteScreen(Long screenId) throws ResourceNotFoundException, AuthorizationException;

    ScreenDto getScreenById(Long screenId) throws ResourceNotFoundException;

    List<ScreenDto> getAllScreen();

    List<ScreenDto> getScreenByTheatre(Long theatreId) throws ResourceNotFoundException, AuthorizationException;

    ScreenSummaryDto getScreenSummary(Long screenId) throws ResourceNotFoundException;

    void assignSeatsToScreen(Long screenId, List<SeatDto> seats) throws ResourceNotFoundException, ValidationException, BusinessException;

    ScreenLayoutDto getScreenLayout(Long screenId) throws ResourceNotFoundException, ExternalServiceException;

    void deactivateScreen(Long screenId) throws ResourceNotFoundException, AuthorizationException;

    void activateScreen(Long screenId) throws ResourceNotFoundException, AuthorizationException;

}
