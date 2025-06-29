package com.simply.Cinema.controller;

import com.simply.Cinema.core.location_and_venue.dto.CityDto;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.location_and_venue.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CityDto> createCity (
            @RequestBody CityDto cityDto)throws BusinessException {

        CityDto createdCity = cityService.createCity(cityDto);
        return ResponseEntity.ok(createdCity);
    }

    @PutMapping("/update/{cityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public  ResponseEntity<CityDto> updateCity(
            @PathVariable Long cityId,
            @RequestBody CityDto cityDto) throws BusinessException {

        CityDto updatedCity = cityService.updateCity(cityId, cityDto);
        return ResponseEntity.ok(updatedCity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{cityId}")
    public ResponseEntity<String> deleteCity(@PathVariable Long cityId) {

        try {
            cityService.deleteCity(cityId);
            return ResponseEntity.noContent().build(); // 204
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("City not found with ID: " + cityId); // 404 – City not found        }
        }
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<CityDto> getCityById(@PathVariable Long cityId){

        CityDto city = cityService.getCityById(cityId);
        return ResponseEntity.ok(city);

    }

    @GetMapping("/all")
    public ResponseEntity<List<CityDto>> getAllCities(){

        return ResponseEntity.ok(cityService.getAllCities());

    }

    @GetMapping("/active")
    public ResponseEntity<List<CityDto>> getActiveCities(){
        List<CityDto> cities = cityService.getActiveCities();
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/state")
    public ResponseEntity<List<CityDto>> findCitiesByState(
            @RequestParam String state) {

        List<CityDto> cities = cityService.findCitiesByState(state);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/country")
    public ResponseEntity<List<CityDto>> findCitiesByCountry(
            @RequestParam String country) {

        List<CityDto> cities = cityService.findCitiesByCountry(country);
        return ResponseEntity.ok(cities);
    }

    @GetMapping("/{cityId}/timezone")
    public ResponseEntity<String> getCityTimezone(@PathVariable Long cityId) {
        try {
            String timezone = cityService.getCityTimezone(cityId);
            return ResponseEntity.ok(timezone); // Return the timezone in the response
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("City not found with ID: " + cityId); // Return a 404 if city not found
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CityDto>> searchCities(@RequestParam String keyword) {
        List<CityDto> cityList = cityService.searchCities(keyword);
        return ResponseEntity.ok(cityList);
    }

}

