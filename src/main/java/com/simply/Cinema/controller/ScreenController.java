package com.simply.Cinema.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.simply.Cinema.core.location_and_venue.dto.ScreenDto;
import com.simply.Cinema.core.location_and_venue.dto.ScreenLayoutDto;
import com.simply.Cinema.core.location_and_venue.dto.ScreenSummaryDto;
import com.simply.Cinema.core.location_and_venue.dto.SeatDto;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.exception.ValidationException;
import com.simply.Cinema.service.location_and_venue.ScreenService;
import com.simply.Cinema.service.location_and_venue.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenDto> createScreen(@RequestBody ScreenDto screenDto)
            throws BusinessException, ValidationException, ResourceNotFoundException {
        ScreenDto created = screenService.createScreen(screenDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update/{screenId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenDto> updateScreen(@PathVariable Long screenId,
                                                  @RequestBody ScreenDto screenDto)
            throws ResourceNotFoundException, ValidationException, AuthorizationException, JsonProcessingException {
        ScreenDto updated = screenService.updateScreen(screenId, screenDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{screenId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deleteScreen(@PathVariable Long screenId)
            throws ResourceNotFoundException, AuthorizationException {
        screenService.deleteScreen(screenId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScreenDto>> getAllScreens() {
        List<ScreenDto> screens = screenService.getAllScreen();
        return ResponseEntity.ok(screens);
    }

    @GetMapping("/theatre/{theatreId}")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<List<ScreenDto>> getScreensByTheatre(@PathVariable Long theatreId)
            throws ResourceNotFoundException, AuthorizationException {
        List<ScreenDto> screens = screenService.getScreenByTheatre(theatreId);
        return ResponseEntity.ok(screens);
    }

    @GetMapping("/{screenId}/summary")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<ScreenSummaryDto> getScreenSummary(@PathVariable Long screenId)
            throws ResourceNotFoundException {
        ScreenSummaryDto summary = screenService.getScreenSummary(screenId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{screenId}")
    public ResponseEntity<ScreenDto> getScreenById(@PathVariable Long screenId)
            throws ResourceNotFoundException {
        ScreenDto screen = screenService.getScreenById(screenId);
        return ResponseEntity.ok(screen);
    }

    @PatchMapping("/owner/{screenId}/deactivate")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> deactivateScreen(@PathVariable Long screenId)
            throws ResourceNotFoundException, AuthorizationException {
        screenService.deactivateScreen(screenId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/owner/{screenId}/activate")
    @PreAuthorize("hasRole('THEATRE_OWNER')")
    public ResponseEntity<Void> activateScreen(@PathVariable Long screenId)
            throws ResourceNotFoundException, AuthorizationException {
        screenService.activateScreen(screenId);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/{screenId}/assign-seats")
//    @PreAuthorize("hasRole('THEATRE_OWNER')")
//    public ResponseEntity<String> assignSeatsToScreen(
//            @PathVariable Long screenId,
//            @RequestBody List<SeatDto> seats
//    ) throws ResourceNotFoundException, ValidationException, BusinessException {
//
//        screenService.assignSeatsToScreen(screenId, seats);
//        return ResponseEntity.status(HttpStatus.CREATED).body("Seats assigned successfully.");
//    }

    @GetMapping("/{screenId}/layout")
    public ResponseEntity<ScreenLayoutDto> getScreenLayout(
            @PathVariable Long screenId
    ) throws ResourceNotFoundException {

        ScreenLayoutDto layout = screenService.getScreenLayout(screenId);
        return ResponseEntity.ok(layout);
    }

}
