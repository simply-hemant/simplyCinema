package com.simply.Cinema.core.location_and_venue.dto;

import com.simply.Cinema.core.location_and_venue.Enum.ScreenType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScreenDto {

    private Long id;
    private String name;
    private Long theatreId;
    private ScreenType screenType;
    private Integer totalSeats;
  //  private LayoutConfig layoutConfig; // JSON or string layout
    private Boolean isActive;
    private LocalDateTime createdAt;

    private String theatreName;

}
