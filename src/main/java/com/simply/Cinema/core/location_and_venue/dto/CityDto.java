package com.simply.Cinema.core.location_and_venue.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CityDto {

    private Long id;
    private String name;
    private String state;
    private String country;
    private String timezone;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
