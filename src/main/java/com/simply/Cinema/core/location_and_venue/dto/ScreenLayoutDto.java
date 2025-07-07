package com.simply.Cinema.core.location_and_venue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenLayoutDto {

    private Long screenId;
    private String screenName;
    private Map<String, List<SeatDto>> layout; // Row-wise grouping

}
