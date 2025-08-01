package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import com.simply.Cinema.core.location_and_venue.entity.Screen;
import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.location_and_venue.repository.ScreenRepo;
import com.simply.Cinema.core.location_and_venue.repository.SeatRepo;
import com.simply.Cinema.core.location_and_venue.repository.TheatreRepo;
import com.simply.Cinema.core.movieManagement.entity.Movie;
import com.simply.Cinema.core.movieManagement.repository.MovieRepo;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import com.simply.Cinema.core.show_and_booking.Enum.ShowStatus;
import com.simply.Cinema.core.show_and_booking.dto.ShowAvailabilityDto;
import com.simply.Cinema.core.show_and_booking.dto.ShowDto;
import com.simply.Cinema.core.show_and_booking.entity.Show;
import com.simply.Cinema.core.show_and_booking.entity.ShowSeat;
import com.simply.Cinema.core.show_and_booking.repository.ShowRepo;
import com.simply.Cinema.core.show_and_booking.repository.ShowSeatRepo;
import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.core.user.entity.User;
import com.simply.Cinema.core.user.repository.UserRepo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.show_and_booking.ShowSeatService;
import com.simply.Cinema.service.show_and_booking.ShowService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

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
    private final ShowSeatRepo showSeatRepo;

//    @Override
//    public ShowDto createShow(ShowDto showDto) throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {
//
//        long currentUserId = SecurityUtil.getCurrentUserId();
//
//        User user = userRepo.findById(currentUserId)
//                .orElseThrow(() -> new AuthenticationException("User not found"));
//
//        // Validate input DTO
//        if (showDto.getMovieId() == null || showDto.getScreenId() == null ||
//                showDto.getShowDate() == null || showDto.getShowTime() == null) {
//            throw new ValidationException("Missing required show details");
//        }
//
//        //. Fetch related entities
//        Movie movie = movieRepo.findById(showDto.getMovieId())
//                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
//
//        Screen screen = screenRepo.findById(showDto.getScreenId())
//                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));
//
//        // check overlapping show time
//        boolean isOverlapping = showRepo.existsByScreen_IdAndShowDateAndShowTime(
//                screen.getId(), showDto.getShowDate(), showDto.getShowTime());
//
//        if (isOverlapping) {
//            throw new BusinessException("A show already exists at this time on the selected screen");
//        }
//
//        // Create Show Entity
//        Show show = new Show();
//        show.setMovie(movie);
//        show.setScreen(screen);
//        show.setShowDate(showDto.getShowDate());
//        show.setShowTime(showDto.getShowTime());
//        show.setEndTime(showDto.getEndTime());
//        show.setTotalSeats(screen.getTotalSeats());
//
//        // ✅ Set base price from request
//        double basePrice = showDto.getBasePrice();
//        show.setBasePrice(basePrice);
//
//        // Set status if provided, otherwise default to UPCOMING
//        show.setStatus(showDto.getStatus() != null ? showDto.getStatus() : ShowStatus.UPCOMING);
//
//        show.setCreatedBy(currentUserId);
//        show.setCreatedAt(LocalDateTime.now());
//        show.setUpdatedAt(LocalDateTime.now());
//
//        // ✅ Calculate dynamic multiplier based on day
//        DayOfWeek day = showDto.getShowDate().getDayOfWeek();
//        double multiplier = (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) ? 1.5 : 1.2;
//        show.setDynamicPriceMultiplier(multiplier);
//
//        Show savedShow = showRepo.save(show);
//        auditLogService.logEvent("show", AuditAction.CREATE, savedShow.getId(), currentUserId);
//
//        List<Seat> seats = seatRepo.findByScreenId(show.getScreen().getId());
//        List<ShowSeat> showSeats = new ArrayList<>();
//
//        for (Seat seat : seats) {
//            double seatPrice = switch (seat.getSeatType()) {
//                case REGULAR -> basePrice * multiplier;
//                case PREMIUM -> basePrice * multiplier * 1.5;
//                case VIP -> basePrice * multiplier * 2.0;
//            };
//
//            ShowSeat showSeat = new ShowSeat();
//            showSeat.setShow(savedShow);
//            showSeat.setSeat(seat);
//            showSeat.setPrice(seatPrice);
//            showSeat.setStatus(ShowSeatStatus.AVAILABLE);
//            showSeat.setLockedUntil(null);
//            showSeat.setLockedByUserId(null);
//
//            showSeats.add(showSeat);
//        }
//        showSeatRepo.saveAll(showSeats);
//
//        // Set showDto values to return
//        ShowDto responseDto = new ShowDto();
//
//        responseDto.setId(savedShow.getId());
//        responseDto.setMovieId(movie.getId());
//        responseDto.setScreenId(screen.getId());
//        responseDto.setShowDate(show.getShowDate());
//        responseDto.setShowTime(show.getShowTime());
//        responseDto.setEndTime(show.getEndTime());
//        responseDto.setTotalSeats(show.getTotalSeats());
//        responseDto.setStatus(show.getStatus());
//        responseDto.setSeatPrices(showDto.getSeatPrices());
//
//        return responseDto;
//    }

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

        if (showDto.getSeatPrices() == null || showDto.getSeatPrices().isEmpty()) {
            throw new ValidationException("Manual seat prices must be provided");
        }

        // Fetch related entities
        Movie movie = movieRepo.findById(showDto.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Screen screen = screenRepo.findById(showDto.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        // Check overlapping show time
        boolean isOverlapping = showRepo.existsByScreen_IdAndShowDateAndShowTime(
                screen.getId(), showDto.getShowDate(), showDto.getShowTime());

        if (isOverlapping) {
            throw new BusinessException("A show already exists at this time on the selected screen");
        }

        // Create Show Entity
        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setShowDate(showDto.getShowDate());
        show.setShowTime(showDto.getShowTime());
        show.setEndTime(showDto.getEndTime());
        show.setTotalSeats(screen.getTotalSeats());
        show.setBasePrice(showDto.getBasePrice()); // optional, just for record

        show.setStatus(showDto.getStatus() != null ? showDto.getStatus() : ShowStatus.UPCOMING);
        show.setCreatedBy(currentUserId);
        show.setCreatedAt(LocalDateTime.now());
        show.setUpdatedAt(LocalDateTime.now());

        // Do NOT set dynamic price multiplier (manual only)

        Show savedShow = showRepo.save(show);
        auditLogService.logEvent("show", AuditAction.CREATE, savedShow.getId(), currentUserId);

        // Manual seat pricing logic only
        Map<SeatType, Double> manualSeatPrices = showDto.getSeatPrices();
        List<Seat> seats = seatRepo.findByScreenId(screen.getId());
        List<ShowSeat> showSeats = new ArrayList<>();

        for (Seat seat : seats) {
            Double price = manualSeatPrices.get(seat.getSeatType());
            if (price == null) {
                throw new ValidationException("Missing price for seat type: " + seat.getSeatType());
            }

            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(savedShow);
            showSeat.setSeat(seat);
            showSeat.setPrice(price);
            showSeat.setStatus(ShowSeatStatus.AVAILABLE);
            showSeat.setLockedUntil(null);
            showSeat.setLockedByUserId(null);
            showSeats.add(showSeat);
        }

        showSeatRepo.saveAll(showSeats);

        // Response DTO
        ShowDto responseDto = new ShowDto();
        responseDto.setId(savedShow.getId());
        responseDto.setMovieId(movie.getId());
        responseDto.setScreenId(screen.getId());
        responseDto.setShowDate(show.getShowDate());
        responseDto.setShowTime(show.getShowTime());
        responseDto.setEndTime(show.getEndTime());
        responseDto.setTotalSeats(show.getTotalSeats());
        responseDto.setStatus(show.getStatus());
        responseDto.setSeatPrices(manualSeatPrices);
        responseDto.setCreatedAt(show.getCreatedAt());
        responseDto.setBasePrice(show.getBasePrice());

        return responseDto;
    }

//    @Override
//    public ShowDto updateShow(Long showId, ShowDto showDto) throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {
//
//        Long currentUserId = SecurityUtil.getCurrentUserId();
//
//        Show show = showRepo.findById(showId)
//                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));
//
//        // Validate required fields
//        if (showDto.getShowDate() == null || showDto.getShowTime() == null || showDto.getEndTime() == null) {
//            throw new ValidationException("Show date, time, and end time must not be null.");
//        }
//
//        // Validate show time window
//        LocalDateTime startDateTime = LocalDateTime.of(showDto.getShowDate(), showDto.getShowTime());
//        LocalDateTime endDateTime = LocalDateTime.of(showDto.getShowDate(), showDto.getEndTime());
//        if (!endDateTime.isAfter(startDateTime)) {
//            throw new ValidationException("End time must be after start time.");
//        }
//
//        // Update basic fields
//        show.setShowDate(showDto.getShowDate());
//        show.setShowTime(showDto.getShowTime());
//        show.setEndTime(showDto.getEndTime());
//
//        if (showDto.getBasePrice() != null)
//            show.setBasePrice(showDto.getBasePrice());
//
//        if (showDto.getDynamicPriceMultiplier() != null)
//            show.setDynamicPriceMultiplier(showDto.getDynamicPriceMultiplier());
//
//        if (showDto.getStatus() != null)
//            show.setStatus(showDto.getStatus());
//
//        // Update movie
//        if (showDto.getMovieId() != null && !showDto.getMovieId().equals(show.getMovie().getId())) {
//            Movie movie = movieRepo.findById(showDto.getMovieId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showDto.getMovieId()));
//            show.setMovie(movie);
//        }
//
//        // Update screen
//        boolean screenChanged = false;
//        if (showDto.getScreenId() != null && !showDto.getScreenId().equals(show.getScreen().getId())) {
//            Screen screen = screenRepo.findById(showDto.getScreenId())
//                    .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + showDto.getScreenId()));
//            show.setScreen(screen);
//            screenChanged = true;
//        }
//
//        // Save updated show
//        Show updatedShow = showRepo.save(show);
//
//        // Delete old show seats if screen or pricing changed
//        if (screenChanged || showDto.getBasePrice() != null || showDto.getDynamicPriceMultiplier() != null) {
//            showSeatRepo.deleteById(showId);
//
//            List<Seat> seats = seatRepo.findByScreenId(show.getScreen().getId());
//
//            List<ShowSeat> newShowSeats = new ArrayList<>();
//            double basePrice = show.getBasePrice();
//            double multiplier = show.getDynamicPriceMultiplier();
//
//            for (Seat seat : seats) {
//                double seatPrice = switch (seat.getSeatType()) {
//                    case REGULAR -> basePrice * multiplier;
//                    case PREMIUM -> basePrice * multiplier * 1.5;
//                    case VIP -> basePrice * multiplier * 2.0;
//                };
//
//                ShowSeat showSeat = new ShowSeat();
//                showSeat.setShow(updatedShow);
//                showSeat.setSeat(seat);
//                showSeat.setPrice(seatPrice);
//                showSeat.setStatus(ShowSeatStatus.AVAILABLE);
//                showSeat.setLockedByUserId(null);
//                showSeat.setLockedUntil(null);
//
//                newShowSeats.add(showSeat);
//            }
//
//            showSeatRepo.saveAll(newShowSeats);
//        }
//
//        auditLogService.logEvent("show", AuditAction.UPDATE, updatedShow.getId(), currentUserId);
//
//        return convertToDto(updatedShow);
//    }

    @Override
    public ShowDto updateShow(Long showId, ShowDto showDto) throws AuthenticationException, AuthorizationException, ValidationException, BusinessException, ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Show show = showRepo.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + showId));

        boolean screenChanged = false;

        // Optional: Update show date/time/endTime only if all are provided
        if (showDto.getShowDate() != null && showDto.getShowTime() != null && showDto.getEndTime() != null) {
            LocalDateTime startDateTime = LocalDateTime.of(showDto.getShowDate(), showDto.getShowTime());
            LocalDateTime endDateTime = LocalDateTime.of(showDto.getShowDate(), showDto.getEndTime());

            if (!endDateTime.isAfter(startDateTime)) {
                throw new ValidationException("End time must be after start time.");
            }

            show.setShowDate(showDto.getShowDate());
            show.setShowTime(showDto.getShowTime());
            show.setEndTime(showDto.getEndTime());
        }

        // Optional: Update base price
        if (showDto.getBasePrice() != null) {
            show.setBasePrice(showDto.getBasePrice());
        }

        // Optional: Update status
        if (showDto.getStatus() != null) {
            show.setStatus(showDto.getStatus());
        }

        // Optional: Update movie
        if (showDto.getMovieId() != null && !showDto.getMovieId().equals(show.getMovie().getId())) {
            Movie movie = movieRepo.findById(showDto.getMovieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + showDto.getMovieId()));
            show.setMovie(movie);
        }

        // Optional: Update screen
        if (showDto.getScreenId() != null && !showDto.getScreenId().equals(show.getScreen().getId())) {
            Screen screen = screenRepo.findById(showDto.getScreenId())
                    .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + showDto.getScreenId()));
            show.setScreen(screen);
            screenChanged = true;
        }

        // Save show before updating show seats
        Show updatedShow = showRepo.save(show);

        // Update seats if screen changed or seatPrices are provided
        if (screenChanged || showDto.getSeatPrices() != null) {

            if (showDto.getSeatPrices() == null || showDto.getSeatPrices().isEmpty()) {
                throw new ValidationException("Seat prices must be provided for manual pricing.");
            }

            // Delete existing show seats
            showSeatRepo.deleteByShowId(showId);

            // Fetch seats for the updated screen
            List<Seat> seats = seatRepo.findByScreenId(show.getScreen().getId());

            List<ShowSeat> newShowSeats = new ArrayList<>();

            for (Seat seat : seats) {
                SeatType type = seat.getSeatType();
                Double manualPrice = showDto.getSeatPrices().get(type);
                if (manualPrice == null) {
                    throw new ValidationException("Missing price for seat type: " + type);
                }

                ShowSeat showSeat = new ShowSeat();
                showSeat.setShow(updatedShow);
                showSeat.setSeat(seat);
                showSeat.setPrice(manualPrice);
                showSeat.setStatus(ShowSeatStatus.AVAILABLE);
                showSeat.setLockedByUserId(null);
                showSeat.setLockedUntil(null);

                newShowSeats.add(showSeat);
            }

            showSeatRepo.saveAll(newShowSeats);
        }

        auditLogService.logEvent("show", AuditAction.UPDATE, updatedShow.getId(), currentUserId);

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
    public List<ShowDto> getShowsByMovie(Long movieId) throws ResourceNotFoundException {
        List<Show> shows = showRepo.findByMovie_Id(movieId);

        if (shows.isEmpty()) {
            throw new ResourceNotFoundException("No shows found for movie ID: " + movieId);
        }

        List<ShowDto> dtos = new ArrayList<>();
        for (Show s : shows) {
            dtos.add(convertToDto(s));
        }

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

        // ✅ Delete associated show seats first to avoid FK constraint violation
        showSeatRepo.deleteByShowId(id);  // This must exist in your ShowSeatRepository

        showRepo.delete(show);

        auditLogService.logEvent("show", AuditAction.DELETE, id, currentUserId);
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

        ShowSeat showSeat = new ShowSeat();

        ShowDto dto = new ShowDto();

        dto.setId(show.getId());
        dto.setMovieId(show.getMovie().getId());
        dto.setScreenId(show.getScreen().getId());
        dto.setShowDate(show.getShowDate());
        dto.setShowTime(show.getShowTime());
        dto.setEndTime(show.getEndTime());
        dto.setTotalSeats(show.getTotalSeats());
        dto.setAvailableSeats(show.getAvailableSeats());
        dto.setStatus(show.getStatus());
        dto.setCreatedAt(show.getCreatedAt());

        // Set seatPrices using simple logic
        Map<SeatType, Double> seatPrices = new HashMap<>();

        List<ShowSeat> showSeats = showSeatRepo.findByShow_Id(show.getId()); // Get show seats

        for (ShowSeat seat : showSeats) {
            String seatType = seat.getSeat().getSeatType().name(); // e.g., PREMIUM, REGULAR
            Double price = seat.getPrice();

            if (!seatPrices.containsKey(seatType)) {
                seatPrices.put(SeatType.valueOf(seatType), price); // Add only once per type
            }
        }
        dto.setSeatPrices(seatPrices);

        return dto;
    }



}
