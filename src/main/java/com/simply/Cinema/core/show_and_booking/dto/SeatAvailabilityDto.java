package com.simply.Cinema.core.show_and_booking.dto;

import com.simply.Cinema.core.location_and_venue.Enum.SeatType;
import com.simply.Cinema.core.show_and_booking.Enum.ShowSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityDto {
    private Long seatId;
    private String seatNumber;
    private String seatRow;
    private SeatType seatType;

    private Double price;
    private ShowSeatStatus status;
}

