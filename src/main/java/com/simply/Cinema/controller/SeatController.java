package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.SeatLayoutDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatTypeDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.location_and_venue.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats/")
public class SeatController{

    private final SeatService seatService;

    @PostMapping("/{screenId}/create")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatLayoutDto> createSeatLayout(
            @RequestBody SeatLayoutDto seatLayoutDto,
            @PathVariable Long screenId) throws ResourceNotFoundException, ValidationException, BusinessException{

        SeatLayoutDto createdLayout = seatService.createSeatLayout(screenId, seatLayoutDto);
        return ResponseEntity.ok(createdLayout);
    }

    @PutMapping("/{layoutId}/update")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatLayoutDto> updateSeatLayout(
            @PathVariable Long layoutId,
            @RequestBody SeatLayoutDto seatLayoutDto
    ) throws ResourceNotFoundException, ValidationException{

        SeatLayoutDto updateLayout = seatService.updateSeatLayout(layoutId, seatLayoutDto);
        return ResponseEntity.ok(updateLayout);
    }

    @GetMapping("/{screenId}")
    public ResponseEntity<SeatLayoutDto> getSeatLayoutByScreen(@PathVariable Long screenId){

        SeatLayoutDto seatLayoutDto = seatService.getSeatLayoutByScreen(screenId);
        return ResponseEntity.ok(seatLayoutDto);
    }

    @DeleteMapping("/{seatId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<String> deleteSeat(@PathVariable Long seatId)
            throws ResourceNotFoundException, AuthorizationException {

        seatService.deleteSeat(seatId);
        return ResponseEntity.ok("Seat deleted successfully.");
    }

    @DeleteMapping("/layout/{layoutId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deleteSeatLayout(@PathVariable Long layoutId) throws ResourceNotFoundException {
        seatService.deleteSeatLayout(layoutId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{seatId}/availability")
    public ResponseEntity<Boolean> isSeatAvailable(@PathVariable Long seatId) throws ResourceNotFoundException {
        // You must have isSeatAvailable() method in SeatService
        boolean available = seatService.isSeatAvailable(seatId);
        return ResponseEntity.ok(available);
    }


//    @GetMapping("/layout/all")
//    public ResponseEntity<List<SeatLayoutDto>> getAllLayouts() {
//        return ResponseEntity.ok(seatService.getAllSeatLayouts());
//    }

    @GetMapping("/types")
    public ResponseEntity<List<SeatTypeDto>> getSeatTypes() {
        return ResponseEntity.ok(seatService.getSeatTypes());
    }

    @PostMapping("/types")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatTypeDto> addSeatType(@RequestBody @Validated SeatTypeDto seatTypeDto) throws ValidationException {
        return ResponseEntity.status(HttpStatus.CREATED).body(seatService.addSeatType(seatTypeDto));
    }

    @PutMapping("/types/{id}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<SeatTypeDto> updateSeatType(
            @PathVariable Long id,
            @RequestBody @Validated SeatTypeDto seatTypeDto) throws ResourceNotFoundException, ValidationException {
        return ResponseEntity.ok(seatService.updateSeatType(id, seatTypeDto));
    }

    @GetMapping("/capacity/{screenId}")
    public ResponseEntity<Integer> getSeatCapacity(@PathVariable Long screenId) throws ResourceNotFoundException {
        return ResponseEntity.ok(seatService.getSeatCapacityByScreen(screenId));
    }
}
