package com.simply.Cinema.service.show_and_booking;

import com.simply.Cinema.core.show_and_booking.dto.ShowAvailabilityDto;
import com.simply.Cinema.core.show_and_booking.dto.ShowDto;
import com.simply.Cinema.exception.*;

import java.util.List;

public interface ShowService {

    ShowDto createShow(ShowDto showDto)
            throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException;

    ShowDto updateShow(Long showId, ShowDto showDto)
            throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException;

    void cancelShow(Long showId)
            throws AuthenticationException, AuthorizationException, BusinessException, ResourceNotFoundException;

    List<ShowDto> getShowsByTheatre(Long theatreId)
            throws AuthenticationException, AuthorizationException, ResourceNotFoundException;

    ShowDto getShowById(Long id) throws ResourceNotFoundException;

    List<ShowDto> getShowsByMovie(Long movieId);

    void deleteShow(Long id) throws ResourceNotFoundException;



    ShowAvailabilityDto getShowAvailability(Long showId)
            throws AuthenticationException, AuthorizationException, ResourceNotFoundException, SeatLockException;

}
