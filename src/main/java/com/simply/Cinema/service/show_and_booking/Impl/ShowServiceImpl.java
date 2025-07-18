package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.location_and_venue.entity.Screen;
import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.location_and_venue.repository.ScreenRepo;
import com.simply.Cinema.core.location_and_venue.repository.SeatRepo;
import com.simply.Cinema.core.location_and_venue.repository.TheatreRepo;
import com.simply.Cinema.core.movieManagement.entity.Movie;
import com.simply.Cinema.core.movieManagement.repository.MovieRepo;
import com.simply.Cinema.core.show_and_booking.Enum.ShowStatus;
import com.simply.Cinema.core.show_and_booking.dto.ShowAvailabilityDto;
import com.simply.Cinema.core.show_and_booking.dto.ShowDto;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.repository.ShowRepo;
import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.show_and_booking.ShowService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class ShowServiceImpl implements ShowService {

    private final ShowRepo showRepo;
    private final UserRepo userRepo;
    private final MovieRepo movieRepo;
    private final ScreenRepo screenRepo;
    private final AuditLogService auditLogService;
    private final TheatreRepo theatreRepo;
    private final SeatRepo seatRepo;

    @Override
    public ShowDto createShow(ShowDto showDto) throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {

        long currentUserId = SecurityUtil.getCurrentUserId();

        User user = userRepo.findById(currentUserId)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        // Validate input DTO
        if (showDto.getMovieId() == null || showDto.getScreenId() == null ||
                showDto.getShowDate() == null || showDto.getShowTime() == null) {
            throw new ValidationException("Missing required show details");
        }

        //. Fetch related entities
        Movie movie = movieRepo.findById(showDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Screen screen = screenRepo.findById(showDto.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));


        // check overlapping show time
        boolean isOverlapping = showRepo.existsByScreen_IdAndShowDateAndShowTime(
                screen.getId(), showDto.getShowDate(), showDto.getShowTime());

        if (isOverlapping) {
            throw new BusinessException("A show already exists at this time on the selected screen");
        }

        if (showDto.getTotalSeats() > screen.getTotalSeats()) {
            throw new BusinessException("Total seats exceed screen capacity");
        }

        // Create Show Entity
        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setShowDate(showDto.getShowDate());
        show.setShowTime(showDto.getShowTime());
        show.setEndTime(showDto.getEndTime());
        show.setBasePrice(showDto.getBasePrice());
        show.setDynamicPriceMultiplier(showDto.getDynamicPriceMultiplier());
        show.setTotalSeats(showDto.getTotalSeats());
        show.setAvailableSeats(showDto.getTotalSeats());
        show.setStatus(ShowStatus.UPCOMING);
        show.setCreatedBy(currentUserId);
        show.setCreatedAt(LocalDateTime.now());
        show.setUpdatedAt(LocalDateTime.now());

        Show savedShow = showRepo.save(show);
        auditLogService.logEvent("show", AuditAction.CREATE, savedShow.getId(), currentUserId);

        // Map to DTO and return
        return convertToDto(savedShow);
    }

    @Override
    public ShowDto updateShow(Long showId, ShowDto showDto) throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("show not found with id: "+showId));

        // Validate date and time fields
        if (showDto.getShowDate() == null || showDto.getShowTime() == null || showDto.getEndTime() == null) {
            throw new ValidationException("Show date, time, and end time must not be null.");
        }

        // Combine date and time to validate time window
        LocalDateTime startDateTime = LocalDateTime.of(showDto.getShowDate(), showDto.getShowTime());
        LocalDateTime endDateTime = LocalDateTime.of(showDto.getShowDate(), showDto.getEndTime());

        // Update basic fields
        show.setShowDate(showDto.getShowDate());
        show.setShowTime(showDto.getShowTime());
        show.setEndTime(showDto.getEndTime());

        if (showDto.getBasePrice() != null)
            show.setBasePrice(showDto.getBasePrice());

        if (showDto.getDynamicPriceMultiplier() != null)
            show.setDynamicPriceMultiplier(showDto.getDynamicPriceMultiplier());

        if (showDto.getStatus() != null)
            show.setStatus(showDto.getStatus());

        // movie or screen can be updated
        if (showDto.getMovieId() != null && !showDto.getMovieId().equals(show.getMovie().getId())) {
            Movie movie = movieRepo.findById(showDto.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showDto.getMovieId()));
            show.setMovie(movie);
        }

        if (showDto.getScreenId() != null && !showDto.getScreenId().equals(show.getScreen().getId())) {
            Screen screen = screenRepo.findById(showDto.getScreenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + showDto.getScreenId()));
            show.setScreen(screen);
        }

        Show updatedShow = showRepo.save(show);

        auditLogService.logEvent("show", AuditAction.UPDATE, showDto.getId(), currentUserId);

        return convertToDto(updatedShow);
    }

    @Override
    public void cancelShow(Long showId) throws AuthenticationException, AuthorizationException, BusinessException, ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        // Authorization: Only the creator (theatre owner) can cancel the show
        if (!show.getCreatedBy().equals(currentUserId)) {
            throw new AuthorizationException("You are not authorized to cancel this show.");
        }

        // Business Rule: Can't cancel a show that is already cancelled or completed
        if (show.getStatus() == ShowStatus.CANCELLED || show.getStatus() == ShowStatus.COMPLETED) {
            throw new BusinessException("Show is already cancelled or completed.");
        }

        show.setStatus(ShowStatus.CANCELLED);
        showRepo.save(show);

        auditLogService.logEvent("show", AuditAction.CANCEL, showId, currentUserId);

    }

    @Override
    public List<ShowDto> getShowsByTheatre(Long theatreId) throws AuthenticationException, AuthorizationException, ResourceNotFoundException {

        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + theatreId));

        //Get all screens for this theatre
        List<Screen> screens = screenRepo.findScreenByTheatreId(theatreId);

        // If no screens, return empty list
        if (screens.isEmpty()) {
            return new ArrayList<>();
        }

        // Collect screen IDs manually
        List<Long> screenIds = new ArrayList<>();
        for (Screen screen : screens) {
            screenIds.add(screen.getId());
        }

        // Get shows based on screen IDs
        List<Show> shows = showRepo.findByScreen_IdIn(screenIds);

        // Convert to ShowDto list
        List<ShowDto> showDtos = new ArrayList<>();
        for (Show show : shows) {
            showDtos.add(convertToDto(show));
        }

        return showDtos;
    }

    @Override
    public ShowDto getShowById(Long id) throws ResourceNotFoundException {
        Show show = showRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));
        return convertToDto(show);
    }

    @Override
    public List<ShowDto> getShowsByMovie(Long movieId) {
        List<Show> shows = showRepo.findByMovie_Id(movieId);
        List<ShowDto> dtos = new ArrayList<>();
        for (Show s : shows) dtos.add(convertToDto(s));
        return dtos;
    }

    @Override
    public void deleteShow(Long id) throws ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Show show = showRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found"));

        if (!show.getCreatedBy().equals(currentUserId)) {
            throw new AuthorizationException("You are not authorized to delete this show");
        }
        auditLogService.logEvent("show", AuditAction.DELETE, id, currentUserId);

        showRepo.delete(show);
    }

//    @Override
//    public ShowAvailabilityDto getShowAvailability(Long showId) throws AuthenticationException, AuthorizationException, ResourceNotFoundException, SeatLockException {
//
//        Show show = showRepo.findById(showId)
//                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));
//
//        Screen screen = show.getScreen();
//        if (screen == null) {
//            throw new ResourceNotFoundException("Screen not associated with this show");
//        }
//
//        List<Seat> allSeats = seatRepo.findByScreenId(screen.getId()); // Assume this method exists
//
//        List<Long> bookedSeatIds = seatBookingRepo.findBookedSeatIdsByShowId(showId); // custom query
//
//        return null;
//    }

    private ShowDto convertToDto(Show show) {
        ShowDto dto = new ShowDto();

        dto.setId(show.getId());
        dto.setMovieId(show.getMovie().getId());
        dto.setScreenId(show.getScreen().getId());
        dto.setShowDate(show.getShowDate());
        dto.setShowTime(show.getShowTime());
        dto.setEndTime(show.getEndTime());
        dto.setBasePrice(show.getBasePrice());
        dto.setDynamicPriceMultiplier(show.getDynamicPriceMultiplier());
        dto.setTotalSeats(show.getTotalSeats());
        dto.setAvailableSeats(show.getAvailableSeats());
        dto.setStatus(show.getStatus());
        dto.setCreatedAt(show.getCreatedAt());

        return dto;
    }


}
