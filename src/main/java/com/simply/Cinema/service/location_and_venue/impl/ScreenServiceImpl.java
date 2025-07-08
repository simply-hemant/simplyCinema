package com.simply.Cinema.service.location_and_venue.impl;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import com.simply.Cinema.core.location_and_venue.dto.*;
import com.simply.Cinema.core.location_and_venue.entity.Screen;
import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.location_and_venue.repository.ScreenRepo;
import com.simply.Cinema.core.location_and_venue.repository.SeatRepo;
import com.simply.Cinema.core.location_and_venue.repository.TheatreRepo;
import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.location_and_venue.ScreenService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepo screenRepo;
    private final TheatreRepo theatreRepo;
    private final AuditLogService auditLogService;
    private final SeatRepo seatRepo;

    @Override
    public ScreenDto createScreen(ScreenDto screenDto) throws BusinessException, ValidationException, ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        //Validate input
        if (screenDto.getName() == null || screenDto.getName().trim().isEmpty()) {
            throw new ValidationException("Screen name cannot be empty.");
        }

        if (screenDto.getTheatreId() == null) {
            throw new ValidationException("Theatre ID is required.");
        }

        //Find theatre
        Theatre theatre = theatreRepo.findById(screenDto.getTheatreId())
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with ID: " + screenDto.getTheatreId()));

        // ✅ Check if the current user is the owner of the theatre
        if (!theatre.getOwnerId().equals(currentUserId)) {
            throw new ValidationException("Access denied: You are not the owner of this theatre.");
        }

        boolean screenExist = screenRepo.existsByNameIgnoreCaseAndTheatreId(screenDto.getName(), screenDto.getTheatreId());
        if(screenExist){
            throw new BusinessException("A screen with the same name already exists int he theatre.");
        }

        Screen screen = Screen.builder()
                .name(screenDto.getName())
                .screenType(screenDto.getScreenType())
                .totalSeats(0)
              //  .layoutConfig(layoutConfigJson) // store as JSON string
                .isActive(true)
                .theatre(theatre)
                .build();

        Screen saved = screenRepo.save(screen);

        ScreenDto responseDto = new ScreenDto();

        responseDto.setId(saved.getId());
        responseDto.setName(saved.getName());
        responseDto.setTheatreId(saved.getTheatre().getId());
        responseDto.setScreenType(saved.getScreenType());
        responseDto.setTotalSeats(saved.getTotalSeats());
        responseDto.setIsActive(saved.getIsActive());
        responseDto.setCreatedAt(saved.getCreatedAt());

        auditLogService.logEvent("screen", AuditAction.CREATE, responseDto.getId(), currentUserId);

        return responseDto;
    }

    @Override
    public ScreenDto updateScreen(Long screenId, ScreenDto screenDto) throws ResourceNotFoundException, ValidationException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: "+ screenId));

        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
            throw new ValidationException("Access denied. You are not the owner of this theatre.");
        }

        if (screenDto.getName() != null) screen.setName(screenDto.getName());
        if (screenDto.getScreenType() != null) screen.setScreenType(screenDto.getScreenType());
        if (screenDto.getTotalSeats() != null) screen.setTotalSeats(screenDto.getTotalSeats());
        if (screenDto.getIsActive() != null) screen.setIsActive(screenDto.getIsActive());

        Screen updatedScreen = screenRepo.save(screen);

        ScreenDto responseDto = new ScreenDto();
        responseDto.setId(updatedScreen.getId());
        responseDto.setName(updatedScreen.getName());
        responseDto.setTheatreId(updatedScreen.getTheatre().getId());
        responseDto.setScreenType(updatedScreen.getScreenType());
        responseDto.setTotalSeats(updatedScreen.getTotalSeats());
        responseDto.setIsActive(updatedScreen.getIsActive());
        responseDto.setCreatedAt(updatedScreen.getCreatedAt());

        auditLogService.logEvent("screen", AuditAction.UPDATE, screenId, currentUserId);

        return responseDto;
    }

    @Override
    public void deleteScreen(Long screenId) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: "+ screenId));

        if(!screen.getTheatre().getOwnerId().equals(currentUserId)){
            throw new ValidationException("Access denied. You are not the owner of this theatre.");
        }

        screenRepo.delete(screen);

        auditLogService.logEvent("screen", AuditAction.DELETE, screenId, currentUserId);
    }

    @Override
    public ScreenDto getScreenById(Long screenId) throws ResourceNotFoundException {

        Screen Existingscreen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: "+screenId));

        ScreenDto responseDto = new ScreenDto();

        responseDto.setId(Existingscreen.getId());
        responseDto.setName(Existingscreen.getName());
        responseDto.setTheatreId(Existingscreen.getTheatre().getId());
        responseDto.setScreenType(Existingscreen.getScreenType());
        responseDto.setTotalSeats(Existingscreen.getTotalSeats());
        responseDto.setIsActive(Existingscreen.getIsActive());
        responseDto.setCreatedAt(Existingscreen.getCreatedAt());
        responseDto.setTheatreName(Existingscreen.getTheatre().getName());

        return responseDto;
    }

    @Override
    public List<ScreenDto> getAllScreen() {

        List<Screen> screens = screenRepo.findAll();
        List<ScreenDto> screenDtoList = new ArrayList<>();

        for (Screen screen : screens) {
            ScreenDto dto = new ScreenDto();
            dto.setId(screen.getId());
            dto.setName(screen.getName());
            dto.setTheatreId(screen.getTheatre().getId());
            dto.setScreenType(screen.getScreenType());
            dto.setTotalSeats(screen.getTotalSeats());

            dto.setIsActive(screen.getIsActive());
            dto.setCreatedAt(screen.getCreatedAt());

            screenDtoList.add(dto);
        }

        return screenDtoList;
    }

    @Override
    public List<ScreenDto> getScreenByTheatre(Long theatreId) throws ResourceNotFoundException, AuthorizationException {

        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + theatreId));

        Long currentUserId = SecurityUtil.getCurrentUserId();
        if (!theatre.getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied. You are not the owner of this theatre.");
        }

        List<Screen> screens = screenRepo.findScreenByTheatreId(theatreId);
        List<ScreenDto> screenDtoList = new ArrayList<>();

        for(Screen screen : screens){
            ScreenDto dto = new ScreenDto();
            dto.setId(screen.getId());
            dto.setName(screen.getName());
            dto.setTheatreId(screen.getTheatre().getId());
            dto.setScreenType(screen.getScreenType());
            dto.setTotalSeats(screen.getTotalSeats());

            dto.setIsActive(screen.getIsActive());
            dto.setCreatedAt(screen.getCreatedAt());

            screenDtoList.add(dto);
        }

        return screenDtoList;
    }

    @Override
    public ScreenSummaryDto getScreenSummary(Long screenId) throws ResourceNotFoundException {

        Screen existingScreen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: "+screenId));

        List<Seat> seats = seatRepo.findByScreenId(screenId);

        int activeSeats = 0;
        Map<SeatType, Integer> seatTypeCounts = new HashMap<>();

        for (Seat seat : seats) {
            // Count active seats
            if (seat.getIsActive() != null && seat.getIsActive()) {
                activeSeats++;
            }

            // Count seat types
            SeatType type = seat.getSeatType();
            if (type != null) {
                if (seatTypeCounts.containsKey(type)) {
                    seatTypeCounts.put(type, seatTypeCounts.get(type) + 1);
                } else {
                    seatTypeCounts.put(type, 1);
                }
            }
        }

        ScreenSummaryDto dto = new ScreenSummaryDto();

        dto.setScreenId(existingScreen.getId());
        dto.setScreenName(existingScreen.getName());
        dto.setTotalSeats(existingScreen.getTotalSeats());
        dto.setActiveSeats(activeSeats);
        dto.setSeatTypeCounts(seatTypeCounts);

        dto.setScreenType(String.valueOf(existingScreen.getScreenType()));
        dto.setIsActive(existingScreen.getIsActive());
        dto.setTheatreId(existingScreen.getTheatre().getId());
        dto.setTheatreName(existingScreen.getTheatre().getName());

        return dto;
    }

//    @Override
//    public void assignSeatsToScreen(Long screenId, List<SeatDto> seats)
//            throws ResourceNotFoundException, ValidationException, BusinessException {
//
//        Screen screen = screenRepo.findById(screenId)
//                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));
//
//        if (seats == null || seats.isEmpty()) {
//            throw new ValidationException("Seat list cannot be empty.");
//        }
//
//        //check for existing seats to avoid duplicates
//        List<Seat> existingSeats = seatRepo.findByScreenId(screenId);
//        Set<String> existingSeatNumbers = new HashSet<>();
//
//        for (Seat seat : existingSeats) {
//            String seatKey = seat.getRowNumber() + "-" + seat.getSeatNumber();
//            existingSeatNumbers.add(seatKey);
//        }
//
//
//        List<Seat> seatEntities = new ArrayList<>();
//
//        for (SeatDto dto : seats) {
//
//            // Validate seat data
//            if (dto.getRowNumber() == null || dto.getSeatNumber() == null || dto.getSeatType() == null) {
//                throw new ValidationException("Invalid seat details provided.");
//            }
//
//            String seatKey = dto.getRowNumber() + "-" + dto.getSeatNumber();
//            if (existingSeatNumbers.contains(seatKey)) {
//                throw new BusinessException("Seat already exists: " + seatKey);
//            }
//
//            Seat seat = new Seat();
//            seat.setScreen(screen);
//            seat.setRowNumber(dto.getRowNumber());
//            seat.setSeatNumber(dto.getSeatNumber());
//            seat.setSeatType(dto.getSeatType());
//            seat.setIsActive(true);
//            seatEntities.add(seat);
//        }
//
//        seatRepo.saveAll(seatEntities);
//
//        // Optional: update totalSeats in screen
//        screen.setTotalSeats(existingSeats.size() + seatEntities.size());
//        screenRepo.save(screen);
//    }

    @Override
    public ScreenLayoutDto getScreenLayout(Long screenId) throws ResourceNotFoundException {

        // 1. Find the screen
        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        // 2. Fetch all seats for the screen
        List<Seat> seats = seatRepo.findByScreenId(screenId);

        // 3. Create layout: Map<rowNumber, List<SeatDto>>
        Map<String, List<SeatDto>> layoutByRow = new HashMap<>();

        for (Seat seat : seats) {
            // Convert entity to DTO
            SeatDto dto = new SeatDto();
            dto.setId(seat.getId());
            dto.setRowNumber(seat.getRowNumber());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setSeatType(seat.getSeatType());
            dto.setIsActive(seat.getIsActive());

            // Add to the layout map
            String row = seat.getRowNumber();

            if (!layoutByRow.containsKey(row)) {
                layoutByRow.put(row, new ArrayList<>());
            }

            layoutByRow.get(row).add(dto);
        }

        // 4. Prepare response DTO
        ScreenLayoutDto layoutDto = new ScreenLayoutDto();
        layoutDto.setScreenId(screen.getId());
        layoutDto.setScreenName(screen.getName());
        layoutDto.setLayout(layoutByRow);

        return layoutDto;
    }

    @Override
    public void deactivateScreen(Long screenId) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied! You are not the owner of this theatre");
        }

        screen.setIsActive(false);
        screenRepo.save(screen);
        auditLogService.logEvent("screen", AuditAction.DEACTIVATE, screenId, currentUserId);
    }

    @Override
    public void activateScreen(Long screenId) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied! You are not the owner of this theatre");
        }

        screen.setIsActive(true);
        screenRepo.save(screen);
        auditLogService.logEvent("screen", AuditAction.ACTIVATE, screenId, currentUserId);
    }

}
