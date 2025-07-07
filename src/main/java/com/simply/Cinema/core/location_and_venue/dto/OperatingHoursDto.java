package com.simply.Cinema.core.location_and_venue.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class OperatingHoursDto {
    private LocalTime openingTime;
    private LocalTime closingTime;
}
