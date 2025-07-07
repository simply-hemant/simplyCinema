package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.OperatingHoursDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreRequestDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreResponseDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.location_and_venue.TheatreService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/theatre")
public class TheatreController {

    private final TheatreService theatreService;

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PostMapping("/owner/create")
    public ResponseEntity<TheatreResponseDto> createTheatre(
            @RequestBody TheatreRequestDto requestDto) throws BusinessException {

        TheatreResponseDto created = theatreService.createTheatre(requestDto);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/owner/update/{theatreId}")
    public ResponseEntity<TheatreResponseDto> updateTheatre(
            @PathVariable Long theatreId,
            @RequestBody TheatreRequestDto requestDto) throws ResourceNotFoundException, BusinessException {

        TheatreResponseDto updateTheatre = theatreService.updateTheatre(theatreId, requestDto);
        return ResponseEntity.ok(updateTheatre);

    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @DeleteMapping("/owner/delete/{theatreId}")
    public ResponseEntity<Void> deleteTheatre(
            @PathVariable Long theatreId) throws ResourceNotFoundException {

        theatreService.deleteTheatre(theatreId);
        return ResponseEntity.noContent().build(); //204
    }

    @GetMapping("/{theatreId}")
    public ResponseEntity<TheatreResponseDto> getTheatreById(
            @PathVariable Long theatreId) throws ResourceNotFoundException {

        TheatreResponseDto dto = theatreService.getTheatreById(theatreId);
        return ResponseEntity.ok(dto);

    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<TheatreResponseDto>> getTheatreByCityId(
            @PathVariable Long cityId) throws ResourceNotFoundException {

        List<TheatreResponseDto> dto = theatreService.getTheatreByCity(cityId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<TheatreResponseDto>> getAllTheatre(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<TheatreResponseDto> theatrePage = theatreService.getAllTheatre(pageNo, pageSize);
        return ResponseEntity.ok(theatrePage);

    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @GetMapping("/owner/list/{ownerId}")
    public ResponseEntity<List<TheatreResponseDto>> getTheatresByOwner(
            @PathVariable Long ownerId) throws AccessDeniedException {

        Long currentUserId = SecurityUtil.getCurrentUserId();
        boolean isAdmin = SecurityUtil.hasRole("ADMIN");

        List<TheatreResponseDto> theatres = theatreService.getTheatreByOwner(ownerId, currentUserId, isAdmin);
        return ResponseEntity.ok(theatres);

    }

    @GetMapping("/amenities")
    public ResponseEntity<List<TheatreResponseDto>> getTheatreByAmenities(
            @RequestParam List<String> amenities
    ) {
        List<TheatreResponseDto> result = theatreService.getTheatreByAmenities(amenities);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('THEATRE_OWNER')")
    @PutMapping("/owner/changeTime/{theatreId}")
    public ResponseEntity<TheatreResponseDto> updateTheatreOperatingHours(
            @PathVariable Long theatreId,
            @RequestBody OperatingHoursDto hoursDto
    ) {
        TheatreResponseDto updatedTime = theatreService.updateTheatreOperatingHours(theatreId, hoursDto);
        return ResponseEntity.ok(updatedTime);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TheatreResponseDto>> searchTheatre(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<TheatreResponseDto> theatrePage = theatreService.searchTheatre(keyword, pageNo, pageSize);
        return ResponseEntity.ok(theatrePage);
    }


}

