package com.simply.Cinema.service.location_and_venue;

import com.simply.Cinema.core.location_and_venue.dto.SeatLayoutDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatTypeDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;

import java.util.List;

public interface SeatService {

    // Layout-related operations
    SeatLayoutDto createSeatLayout(Long screenId, SeatLayoutDto layoutDto) throws ResourceNotFoundException, ValidationException, BusinessException;

    SeatLayoutDto updateSeatLayout(Long layoutId, SeatLayoutDto layoutDto) throws ResourceNotFoundException, ValidationException;

    void deleteSeatLayout(Long layoutId) throws ResourceNotFoundException;

    // The purpose method is to retrieve a complete overview of seat layouts for every screen in the system.
    SeatLayoutDto getSeatLayoutByScreen(Long screenId) throws ResourceNotFoundException;

    List<SeatLayoutDto> getAllSeatLayouts();

    // Seat type-related operations
    List<SeatTypeDto> getSeatTypes();

    SeatTypeDto addSeatType(SeatTypeDto seatTypeDto) throws ValidationException;

    SeatTypeDto updateSeatType(Long seatTypeId, SeatTypeDto seatTypeDto) throws ResourceNotFoundException, ValidationException;

    //void deleteSeatType(Long seatTypeId) throws ResourceNotFoundException;

    public void deleteSeat(Long seatId) throws ResourceNotFoundException, AuthorizationException;

        // Capacity/Count operation
    Integer getSeatCapacityByScreen(Long screenId) throws ResourceNotFoundException;

    boolean isSeatAvailable(Long seatId) throws ResourceNotFoundException;


}
