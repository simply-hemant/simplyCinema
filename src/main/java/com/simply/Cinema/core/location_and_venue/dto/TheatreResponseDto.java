package com.simply.Cinema.core.location_and_venue.dto;

import com.simply.Cinema.core.movieManagement.dto.MovieDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class TheatreResponseDto {

    private Long id;
    private String name;
    private Long cityId;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private List<String> amenities;
    private Boolean isActive;
    private Boolean foodBeverageAvailable;
    private LocalTime openingHour;
    private LocalTime closingHour;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
