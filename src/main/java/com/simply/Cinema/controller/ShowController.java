package com.simply.Cinema.controller;

import com.simply.Cinema.core.show_and_booking.dto.ShowAvailabilityDto;
import com.simply.Cinema.core.show_and_booking.dto.ShowDto;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.show_and_booking.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/show")
public class ShowController {

    private final ShowService showService;

    @GetMapping("/{id}")
    public ResponseEntity<ShowDto> getShowById(@PathVariable Long id) throws ResourceNotFoundException {
        ShowDto show = showService.getShowById(id);
        return ResponseEntity.ok(show);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PostMapping("/create")
    public ResponseEntity<ShowDto> createShow(@RequestBody @Valid ShowDto dto) {
        ShowDto createdShow = showService.createShow(dto);
        return new ResponseEntity<>(createdShow, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ShowDto> updateShow(@PathVariable Long id, @RequestBody @Valid ShowDto dto)
            throws ResourceNotFoundException {
        ShowDto updatedShow = showService.updateShow(id, dto);
        return ResponseEntity.ok(updatedShow);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShow(@PathVariable Long id) throws ResourceNotFoundException {
        showService.deleteShow(id);
        return ResponseEntity.ok("Show with ID " + id + " deleted successfully.");
    }

//    @GetMapping("/shows/{id}/seats")
//    public ShowAvailabilityDto getShowAvailability(@PathVariable Long id)
//            throws ResourceNotFoundException {
//        return showService.getShowAvailability(id);
//    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<List<ShowDto>> getShowsByMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(showService.getShowsByMovie(movieId));
    }

    @GetMapping("/theatres/{theatreId}")
    public ResponseEntity<List<ShowDto>> getShowsByTheatre(@PathVariable Long theatreId) {
        return ResponseEntity.ok(showService.getShowsByTheatre(theatreId));
    }

}
