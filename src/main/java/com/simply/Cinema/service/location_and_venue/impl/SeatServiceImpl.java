package com.simply.Cinema.service.location_and_venue.impl;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import com.simply.Cinema.core.location_and_venue.dto.SeatDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatLayoutDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatTypeDto;
import com.simply.Cinema.core.location_and_venue.entity.Screen;
import com.simply.Cinema.core.location_and_venue.entity.Seat;
import com.simply.Cinema.core.location_and_venue.repository.ScreenRepo;
import com.simply.Cinema.core.location_and_venue.repository.SeatRepo;
import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.location_and_venue.ScreenService;
import com.simply.Cinema.service.location_and_venue.SeatService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final ScreenRepo screenRepo;
    private final SeatRepo seatRepo;
    private final AuditLogService auditLogService;

//    @Override
//    public SeatLayoutDto createSeatLayout(Long screenId, SeatLayoutDto layoutDto) throws ResourceNotFoundException, ValidationException, BusinessException {
//
//        Long currentUserId = SecurityUtil.getCurrentUserId();
//
//        Screen screen = screenRepo.findById(screenId)
//                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));
//
//        // Authorization check
//        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
//            throw new AuthorizationException("Access denied.");
//        }
//
//        // Prevent duplicate layout creation
//        List<Seat> existingSeats = seatRepo.findByScreenId(screenId);
//        if (!existingSeats.isEmpty()) {
//            throw new BusinessException("Seat layout already exists for this screen. Please update instead.");
//        }
//
//        // Validate and save each seat
//        //- This creates an empty list to store the actual `Seat` entity objects that will be saved to the database.
//        List<Seat> seatEntities = new ArrayList<>();
//
//        for (SeatDto dto : layoutDto.getSeats()) {
//            if (dto.getRowNumber() == null || dto.getSeatNumber() == null || dto.getSeatType() == null) {
//                throw new ValidationException("Each seat must have rowNumber, seatNumber, and seatType.");
//            }
//
//            Seat seat = new Seat();
//
//            // Links this seat to the screen for which the layout is being created.
//            // This is the foreign key (many-to-one relationship with `Screen`).
//
//            seat.setScreen(screen);
//
//            seat.setRowNumber(dto.getRowNumber());
//            seat.setSeatNumber(dto.getSeatNumber());
//            seat.setSeatType(dto.getSeatType());
//            seat.setIsActive(true);
//
//            seatEntities.add(seat);
//        }
//
//        // Save all seats to DB
//        List<Seat> savedSeats = seatRepo.saveAll(seatEntities);
//
//        // ✅ Update totalSeats in screen
//        screen.setTotalSeats(savedSeats.size());
//        screenRepo.save(screen);
//
//        SeatLayoutDto response = new SeatLayoutDto();
//        response.setScreenId(screenId);
//        response.setLayoutName(layoutDto.getLayoutName());
//        response.setCreatedBy(String.valueOf(currentUserId));
//
//        List<SeatDto> seatDtoList = new ArrayList<>();
//        for (Seat seat : savedSeats) {
//            SeatDto seatDto = new SeatDto();
//            seatDto.setId(seat.getId());
//            seatDto.setScreenId(screenId);
//            seatDto.setRowNumber(seat.getRowNumber());
//            seatDto.setSeatNumber(seat.getSeatNumber());
//            seatDto.setSeatType(seat.getSeatType());
//            seatDto.setIsActive(seat.getIsActive());
//            seatDto.setCreatedAt(seat.getCreatedAt());
//
//            seatDtoList.add(seatDto);
//        }
//
//        response.setSeats(seatDtoList);
//
//        auditLogService.logEvent("seat_layout", AuditAction.CREATE, screenId, currentUserId);
//
//        return response;
//
//    }

    @Override
    public SeatLayoutDto createSeatLayout(Long screenId, SeatLayoutDto layoutDto)
            throws ResourceNotFoundException, ValidationException, BusinessException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        // ✅ Authorization check
        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied.");
        }

        // ✅ Prevent duplicate layout creation
        List<Seat> existingSeats = seatRepo.findByScreenId(screenId);
        if (!existingSeats.isEmpty()) {
            throw new BusinessException("Seat layout already exists for this screen. Please update instead.");
        }

        // ✅ Generate seats using helper method
        List<Seat> generatedSeats = generateSeatsFromLayout(layoutDto, screen);

        // ✅ Save seats to DB
        List<Seat> savedSeats = seatRepo.saveAll(generatedSeats);

        // ✅ Update screen total seat count
        screen.setTotalSeats(savedSeats.size());
        screenRepo.save(screen);

        // ✅ Convert seats to DTOs manually
        List<SeatDto> seatDtoList = new ArrayList<>();
        for (Seat seat : savedSeats) {
            SeatDto seatDto = new SeatDto();

            seatDto.setId(seat.getId());
            seatDto.setScreenId(screenId);
            seatDto.setRowNumber(seat.getRowNumber());
            seatDto.setSeatNumber(seat.getSeatNumber());
            seatDto.setSeatType(seat.getSeatType());
            seatDto.setIsActive(seat.getIsActive());
            seatDto.setCreatedAt(seat.getCreatedAt());

            seatDtoList.add(seatDto);
        }

        SeatLayoutDto response = new SeatLayoutDto();

        response.setScreenId(screenId);
        response.setLayoutName(layoutDto.getLayoutName());
        response.setCreatedBy(String.valueOf(currentUserId));
        response.setSeats(seatDtoList);


        // Include metadata (if autoGenerate was used)
        response.setAutoGenerateSeats(layoutDto.isAutoGenerateSeats());
        response.setSeatsPerRow(layoutDto.getSeatsPerRow());
        response.setVipSeatCount(layoutDto.getVipSeatCount());
        response.setPremiumSeatCount(layoutDto.getPremiumSeatCount());
        response.setRegularSeatCount(layoutDto.getRegularSeatCount());


        // ✅ Audit log
        auditLogService.logEvent("seat_layout", AuditAction.CREATE, screenId, currentUserId);

        return response;
    }

   // @Override
//    public SeatLayoutDto updateSeatLayout(Long layoutId, SeatLayoutDto layoutDto) throws ResourceNotFoundException, ValidationException{
//
//        Long currentUserId = SecurityUtil.getCurrentUserId();
//
//        Screen screen = screenRepo.findById(layoutId)
//                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + layoutId));
//
//        // Authorization check
//        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
//            throw new AuthorizationException("Access denied.");
//        }
//
//        List<Seat> existingSeats = seatRepo.findByScreenId(layoutId);
//        if (existingSeats.isEmpty()) {
//            throw new ResourceNotFoundException("No seat layout found for this screen.");
//        }
//
//        // ✅ Null & empty check for input seats
//        if (layoutDto.getSeats() == null || layoutDto.getSeats().isEmpty()) {
//            throw new ValidationException("Seat list cannot be null or empty for update.");
//        }
//
//        List<Seat> updatedSeats = new ArrayList<>();
//        Map<String, Integer> rowSeatCountMap = new HashMap<>();
//
//        int vipCount = 0;
//        int premiumCount = 0;
//        int regularCount = 0;
//
//        // Loop through the input SeatDto list
//        for (SeatDto dto : layoutDto.getSeats()) {
//
//            if (dto.getId() == null || dto.getRowNumber() == null || dto.getSeatNumber() == null || dto.getSeatType() == null) {
//                throw new ValidationException("Seat ID, rowNumber, seatNumber, and seatType must be provided.");
//            }
//
//
//            // Find matching seat by ID from existingSeats
//
//            Seat matchedSeat = null;
//            for (Seat seat : existingSeats) {
//                if (seat.getId().equals(dto.getId())) {
//                    matchedSeat = seat;
//                    break;
//                }
//            }
//
//            if (matchedSeat == null) {
//                throw new ValidationException("Seat with ID " + dto.getId() + " not found for this screen.");
//            }
//
//            // Update seat details
//            matchedSeat.setRowNumber(dto.getRowNumber());
//            matchedSeat.setSeatNumber(dto.getSeatNumber());
//            matchedSeat.setSeatType(dto.getSeatType());
//
//            if (dto.getIsActive() != null) {
//                matchedSeat.setIsActive(dto.getIsActive());
//            }
//
//            // Count for summary
//            switch (dto.getSeatType()) {
//                case VIP -> vipCount++;
//                case PREMIUM -> premiumCount++;
//                case REGULAR -> regularCount++;
//            }
//
//            updatedSeats.add(matchedSeat);
//        }
//
//        // Save updated seats
//        List<Seat> savedSeats = seatRepo.saveAll(updatedSeats);
//
//        // Convert saved seats to SeatDto
//        List<SeatDto> responseSeatDtos = new ArrayList<>();
//        for (Seat seat : savedSeats) {
//            SeatDto dto = new SeatDto();
//            dto.setId(seat.getId());
//            dto.setScreenId(layoutId);
//            dto.setRowNumber(seat.getRowNumber());
//            dto.setSeatNumber(seat.getSeatNumber());
//            dto.setSeatType(seat.getSeatType());
//            dto.setIsActive(seat.getIsActive());
//            dto.setCreatedAt(seat.getCreatedAt());
//
//            responseSeatDtos.add(dto);
//        }
//
//        // Prepare response layout
//        SeatLayoutDto response = new SeatLayoutDto();
//        response.setScreenId(layoutId);
//        response.setLayoutName(layoutDto.getLayoutName());
//        response.setCreatedBy(layoutDto.getCreatedBy() != null ? layoutDto.getCreatedBy() : String.valueOf(currentUserId));
//        response.setAutoGenerateSeats(layoutDto.isAutoGenerateSeats());
//
//        response.setVipSeatCount(vipCount);
//        response.setPremiumSeatCount(premiumCount);
//        response.setRegularSeatCount(regularCount);
//        response.setSeatsPerRow(rowSeatCountMap.values().stream().max(Integer::compare).orElse(0));
//        response.setSeats(responseSeatDtos);
//
//        auditLogService.logEvent("seat_layout", AuditAction.UPDATE, layoutId, currentUserId);
//
//        return response;
//
//    }

    @Override
    public SeatLayoutDto updateSeatLayout(Long layoutId, SeatLayoutDto layoutDto)
            throws ResourceNotFoundException, ValidationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        // ✅ Check if screen exists
        Screen screen = screenRepo.findById(layoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + layoutId));

        // ✅ Authorization check
        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied. you are not the owner of this screen.");
        }

        // ✅ Fetch existing seats
        List<Seat> existingSeats = seatRepo.findByScreenId(layoutId);
        if (existingSeats.isEmpty()) {
            throw new ResourceNotFoundException("No seat layout found for this screen.");
        }

        // ✅ Validate input
        if (layoutDto.getSeats() == null || layoutDto.getSeats().isEmpty()) {
            throw new ValidationException("Seat list cannot be null or empty for update.");
        }

        List<Seat> updatedSeats = new ArrayList<>();

        // ✅ Update only the provided seat IDs
        for (SeatDto dto : layoutDto.getSeats()) {
            if (dto.getId() == null || dto.getRowNumber() == null || dto.getSeatNumber() == null || dto.getSeatType() == null) {
                throw new ValidationException("Seat ID, rowNumber, seatNumber, and seatType must be provided.");
            }

            Seat matchedSeat = null;
            for (Seat seat : existingSeats) {
                if (seat.getId().equals(dto.getId())) {
                    matchedSeat = seat;
                    break;
                }
            }

            if (matchedSeat == null) {
                throw new ValidationException("Seat with ID " + dto.getId() + " not found for this screen.");
            }

            matchedSeat.setRowNumber(dto.getRowNumber());
            matchedSeat.setSeatNumber(dto.getSeatNumber());
            matchedSeat.setSeatType(dto.getSeatType());

            if (dto.getIsActive() != null) {
                matchedSeat.setIsActive(dto.getIsActive());
            }

            updatedSeats.add(matchedSeat);
        }

        // ✅ Save updated seats
        seatRepo.saveAll(updatedSeats);

        // ✅ Fetch all seats again to calculate accurate counts
        List<Seat> allSeats = seatRepo.findByScreenId(layoutId);

        int vipCount = 0;
        int premiumCount = 0;
        int regularCount = 0;
        int maxSeatsPerRow = 0;

        List<SeatDto> responseSeatDtos = new ArrayList<>();
        Map<String, Integer> rowSeatMap = new HashMap<>();

        for (Seat seat : allSeats) {
            // Count by type
            if (seat.getSeatType().toString().equals("VIP")) {
                vipCount++;
            } else if (seat.getSeatType().toString().equals("PREMIUM")) {
                premiumCount++;
            } else if (seat.getSeatType().toString().equals("REGULAR")) {
                regularCount++;
            }

            // Find max seats per row
            String row = seat.getRowNumber();
            if (!rowSeatMap.containsKey(row)) {
                rowSeatMap.put(row, 1);
            } else {
                rowSeatMap.put(row, rowSeatMap.get(row) + 1);
            }

            // Add to response list
            SeatDto seatDto = new SeatDto();
            seatDto.setId(seat.getId());
            seatDto.setScreenId(layoutId);
            seatDto.setRowNumber(seat.getRowNumber());
            seatDto.setSeatNumber(seat.getSeatNumber());
            seatDto.setSeatType(seat.getSeatType());
            seatDto.setIsActive(seat.getIsActive());
            seatDto.setCreatedAt(seat.getCreatedAt());

            responseSeatDtos.add(seatDto);
        }

        // ✅ Calculate max seats per any row
        for (Integer count : rowSeatMap.values()) {
            if (count > maxSeatsPerRow) {
                maxSeatsPerRow = count;
            }
        }

        // ✅ Build response
        SeatLayoutDto response = new SeatLayoutDto();
        response.setScreenId(layoutId);
        response.setLayoutName(layoutDto.getLayoutName());
        response.setCreatedBy(String.valueOf(currentUserId));
        response.setSeats(responseSeatDtos);
        response.setVipSeatCount(vipCount);
        response.setPremiumSeatCount(premiumCount);
        response.setRegularSeatCount(regularCount);
        response.setSeatsPerRow(maxSeatsPerRow);
        response.setAutoGenerateSeats(false); // Since manual update

        auditLogService.logEvent("seat_layout", AuditAction.UPDATE, layoutId, currentUserId);

        return response;
    }


    @Override
    public void deleteSeatLayout(Long layoutId) throws ResourceNotFoundException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        Screen screen = screenRepo.findById(layoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + layoutId));

        // Authorization check
        if (!screen.getTheatre().getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied.");
        }

        // Delete all seats linked to this screen
        List<Seat> seats = seatRepo.findByScreenId(layoutId);
        if (seats.isEmpty()) {
            throw new ResourceNotFoundException("No seat layout found for screen id: " + layoutId);
        }

        seatRepo.deleteAll(seats);

        auditLogService.logEvent("seat_layout", AuditAction.DELETE, layoutId, currentUserId);

    }

    @Override
    public SeatLayoutDto getSeatLayoutByScreen(Long screenId) throws ResourceNotFoundException {

        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        List<Seat> seats = seatRepo.findByScreenId(screenId);
        if(seats.isEmpty()){
            throw new ResourceNotFoundException("No seat layout found for screed id: "+ screenId);
        }

        List<SeatDto> seatDtoList = new ArrayList<>();
        for(Seat seat : seats){
            SeatDto dto = new SeatDto();

            dto.setId(seat.getId());
            dto.setScreenId(screenId);
            dto.setRowNumber(seat.getRowNumber());
            dto.setSeatNumber(seat.getSeatNumber());
            dto.setSeatType(seat.getSeatType());
            dto.setIsActive(seat.getIsActive());
            dto.setCreatedAt(seat.getCreatedAt());
            seatDtoList.add(dto);
        }

        SeatLayoutDto layoutDto = new SeatLayoutDto();
        layoutDto.setScreenId(screenId);
        layoutDto.setLayoutName("Layout of Screen: "+ screen.getName());
        layoutDto.setCreatedBy("THEATRE_OWNER");
        layoutDto.setSeats(seatDtoList);

        return layoutDto;
    }


    // The purpose method is to retrieve a complete overview of seat layouts for every screen in the system.
//    @Override
//    public List<SeatLayoutDto> getAllSeatLayouts() {
//
//        List<Screen> screens = screenRepo.findAll();
//        List<SeatLayoutDto> layoutList = new ArrayList<>();
//
//        // Loop through each screen, Get all seats associated with this screen
//        for (Screen screen : screens) {
//            List<Seat> seats = seatRepo.findByScreenId(screen.getId());
//
//            if (!seats.isEmpty()) {
//                SeatLayoutDto layoutDto = new SeatLayoutDto();
//                layoutDto.setScreenId(screen.getId());
//                layoutDto.setLayoutName("Layout for " + screen.getName());
//                layoutDto.setCreatedBy("THEATRE_OWNER");
//
//                // Convert seat entities to SeatDto list
//                List<SeatDto> seatDtos = new ArrayList<>();
//                for (Seat seat : seats) {
//                    SeatDto seatDto = new SeatDto();
//                    seatDto.setId(seat.getId());
//                    seatDto.setScreenId(screen.getId());
//                    seatDto.setRowNumber(seat.getRowNumber());
//                    seatDto.setSeatNumber(seat.getSeatNumber());
//                    seatDto.setSeatType(seat.getSeatType());
//                    seatDto.setIsActive(seat.getIsActive());
//                    seatDto.setCreatedAt(seat.getCreatedAt());
//                    seatDtos.add(seatDto);
//                }
//
//                layoutDto.setSeats(seatDtos);
//                layoutList.add(layoutDto);
//            }
//        }
//        return layoutList;
//    }

    @Override
    public List<SeatTypeDto> getSeatTypes() {

        List<SeatTypeDto> result = new ArrayList<>();

        for (SeatType type : SeatType.values()) {
            SeatTypeDto dto = new SeatTypeDto();
            dto.setId(null);
            dto.setSeatType(type);
            dto.setIsActive(true);
            dto.setDescription(type.name() + " seat");

            // ❗ Price will be calculated at runtime by PricingService, so just set 0.0 or null
            dto.setPrice(0.0);

            result.add(dto);
        }

        return result;
    }

    @Override
    public SeatTypeDto addSeatType(SeatTypeDto seatTypeDto) throws ValidationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if(seatTypeDto.getSeatType() == null){
            throw new ValidationException("Seat type cannot be null.");
        }

        // Check if the provided seat type exists in the enum
        boolean valid = false;
        for (SeatType type : SeatType.values()) {
            if (type.name().equalsIgnoreCase(seatTypeDto.getSeatType().name())) {
                valid = true;
                break;
            }
        }

        if(!valid){
            throw new ValidationException("Invalid seat type.");
        }

        SeatTypeDto response = new SeatTypeDto();
        response.setSeatType(seatTypeDto.getSeatType());
        response.setDescription(seatTypeDto.getDescription());
        response.setPrice(seatTypeDto.getPrice());
        response.setIsActive(true);

        return response;
    }

    @Override
    public SeatTypeDto updateSeatType(Long seatTypeId, SeatTypeDto seatTypeDto) throws ResourceNotFoundException, ValidationException {

        // Validate input
        if (seatTypeDto.getSeatType() == null) {
            throw new ValidationException("Seat type cannot be null.");
        }

        // Check if the seatType is valid based on the enum
        boolean isValid = false;
        for (SeatType type : SeatType.values()) {
            if (type.name().equalsIgnoreCase(seatTypeDto.getSeatType().name())) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new ValidationException("Invalid seat type.");
        }

        // Simulate update response (as if it was updated in DB)
        SeatTypeDto updated = new SeatTypeDto();
        updated.setId(seatTypeId);
        updated.setSeatType(seatTypeDto.getSeatType());
        updated.setDescription(seatTypeDto.getDescription());

        // ❗Pricing will be dynamic
        updated.setPrice(0.0);
        if (seatTypeDto.getIsActive() == null) {
            throw new ValidationException("isActive flag must be provided.");
        }
        updated.setIsActive(seatTypeDto.getIsActive());

        return updated;
    }

    @Override
    public void deleteSeat(Long seatId) throws ResourceNotFoundException, AuthorizationException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        // Find the seat
        Seat seat = seatRepo.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id: " + seatId));

        //  Authorization check — only the theatre owner can delete
        if (!seat.getScreen().getTheatre().getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("Access denied. You are not the owner of this seat's theatre.");
        }

        seatRepo.deleteById(seatId);

        auditLogService.logEvent("seat", AuditAction.DELETE, seatId, currentUserId);

    }

//    @Override
//    public void deleteSeatType(Long seatTypeId) throws ResourceNotFoundException {
//
//    }

    @Override
    public Integer getSeatCapacityByScreen(Long screenId) throws ResourceNotFoundException {
        // Validate screen existence
        Screen screen = screenRepo.findById(screenId)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + screenId));

        // Fetch all seats linked to the screen
        List<Seat> seats = seatRepo.findByScreenId(screenId);

        // Return total count
        return seats.size();
    }

    @Override
    public boolean isSeatAvailable(Long seatId) throws ResourceNotFoundException {
        Seat seat = seatRepo.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
        return seat.getIsActive();
    }


    private List<Seat> generateSeatsFromLayout(SeatLayoutDto layoutDto, Screen screen) throws ValidationException {

        if (layoutDto.getSeatsPerRow() == null || layoutDto.getSeatsPerRow() <= 0) {
            throw new ValidationException("seatsPerRow must be provided and > 0.");
        }

        int totalSeats = layoutDto.getVipSeatCount() + layoutDto.getPremiumSeatCount() + layoutDto.getRegularSeatCount();
        int seatsPerRow = layoutDto.getSeatsPerRow();
        int totalRows = (int) Math.ceil((double) totalSeats / seatsPerRow);

        List<Seat> generatedSeats = new ArrayList<>();

        // Helper to convert row index to A, B, ..., Z, AA, AB...
        List<String> rowLabels = generateRowLabels(totalRows);

        int seatCounter = 0;

        for (int i = 0; i < totalRows; i++) {
            String rowLabel = rowLabels.get(i);
            for (int j = 1; j <= seatsPerRow; j++) {
                if (seatCounter >= totalSeats) break;

                Seat seat = new Seat();
                seat.setScreen(screen);
                seat.setRowNumber(rowLabel);
                seat.setSeatNumber(String.valueOf(j));
                seat.setIsActive(true);

                if (seatCounter < layoutDto.getVipSeatCount()) {
                    seat.setSeatType(SeatType.VIP);
                } else if (seatCounter < layoutDto.getVipSeatCount() + layoutDto.getPremiumSeatCount()) {
                    seat.setSeatType(SeatType.PREMIUM);
                } else {
                    seat.setSeatType(SeatType.REGULAR);
                }

                generatedSeats.add(seat);
                seatCounter++;
            }
        }

        return generatedSeats;
    }

    private List<String> generateRowLabels(int count) {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            StringBuilder label = new StringBuilder();
            int num = i;
            do {
                label.insert(0, (char) ('A' + (num % 26)));
                num = num / 26 - 1;
            } while (num >= 0);
            labels.add(label.toString());
        }
        return labels;
    }




}
