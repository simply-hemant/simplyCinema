package com.simply.Cinema.core.show_and_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatLockInfo {

    private Long userId;
    private Long showId;
    private Long seatNumber;
    private LocalDateTime lockedAt;
}
