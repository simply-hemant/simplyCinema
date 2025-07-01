package com.simply.Cinema.core.location_and_venue.dto;

import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
public class TheatreRequestDto {

    private String name;
    private Long ownerId;
    private Long cityId;
    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private List<String> amenities;
    private Boolean foodBeverageAvailable;
    private LocalTime openingHour;
    private LocalTime closingHour;


}
