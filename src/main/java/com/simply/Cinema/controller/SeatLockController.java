package com.simply.Cinema.controller;

import com.simply.Cinema.service.show_and_booking.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatLockController {

    private final SeatLockService seatLockService;

    @PostMapping("/lock")
    public ResponseEntity<String> lockSeats(@RequestParam Long showId,
                                            @RequestParam Long userId,
                                            @RequestBody List<Long> seatIds) throws Exception {
        seatLockService.lockSeats(showId, seatIds, userId);
        return ResponseEntity.ok("Seats locked successfully");
    }

    @PostMapping("/release")
    public ResponseEntity<String> releaseSeats(@RequestParam Long showId,
                                               @RequestParam Long userId,
                                               @RequestBody List<Long> seatNumbers) throws Exception {
        seatLockService.releaseLockedSeats(showId, seatNumbers, userId);
        return ResponseEntity.ok("Seats released successfully");
    }

    @GetMapping("/isLocked")
    public ResponseEntity<Boolean> isSeatLocked(@RequestParam Long showId,
                                                @RequestParam Long seatNumber) throws Exception {
        boolean isLocked = seatLockService.checkSeatLockStatus(showId, seatNumber);
        return ResponseEntity.ok(isLocked);
    }

}
