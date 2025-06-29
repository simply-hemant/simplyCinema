package com.simply.Cinema.service.location_and_venue;

import com.simply.Cinema.core.location_and_venue.dto.CityDto;
import com.simply.Cinema.core.location_and_venue.entity.City;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;

import java.util.List;

public interface CityService {

    CityDto createCity(CityDto cityDto) throws BusinessException;;

    CityDto updateCity(Long cityId, CityDto cityDto) throws ResourceNotFoundException, BusinessException;

    void deleteCity(Long cityId) throws ResourceNotFoundException;

    CityDto getCityById(Long cityId) throws ResourceNotFoundException;

    //for internal DB logic
    public City getCityEntityById(Long cityId) throws ResourceNotFoundException;

    List<CityDto> getAllCities();

    List<CityDto> getActiveCities();

    List<CityDto> findCitiesByState(String state);

    List<CityDto> findCitiesByCountry(String country);

    String getCityTimezone(Long cityId) throws ResourceNotFoundException;;

    // ✅ Search by keyword (e.g., name, state, country)
    List<CityDto> searchCities(String keyword);

}
