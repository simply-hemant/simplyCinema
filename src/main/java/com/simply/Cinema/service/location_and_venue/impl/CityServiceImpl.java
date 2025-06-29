package com.simply.Cinema.service.location_and_venue.impl;

import com.simply.Cinema.core.location_and_venue.dto.CityDto;
import com.simply.Cinema.core.location_and_venue.entity.City;
import com.simply.Cinema.core.location_and_venue.repository.CityRepo;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.location_and_venue.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepo cityRepo;

    @Override
    public CityDto createCity(CityDto cityDto) throws BusinessException {

        boolean cityExists = cityRepo.existsByNameIgnoreCaseAndStateIgnoreCase(cityDto.getName(), cityDto.getState());
        if(cityExists){
            throw new BusinessException("City with the same name and state already exists.");
        }

        //CityDto ➝ City (for saving)
        //City ➝ CityDto (for sending response)

        //convert Dto to entity
        City city = new City();
        city.setName(cityDto.getName());
        city.setState(cityDto.getState());
        city.setCountry(cityDto.getCountry());
        city.setTimezone(cityDto.getTimezone());

        // ✅ Set `isActive` based on input:
        // - If user provides a value (true/false), use it.
        // - If user leaves it null, default to true (active by default).
        city.setIsActive(cityDto.getIsActive() != null ? cityDto.getIsActive() : true);

        City savedCity = cityRepo.save(city);

        // 🔹 Convert back to DTO
        CityDto responseDto = new CityDto();

        responseDto.setId(savedCity.getId());
        responseDto.setName(savedCity.getName());
        responseDto.setState(savedCity.getState());
        responseDto.setCountry(savedCity.getCountry());
        responseDto.setTimezone(savedCity.getTimezone());
        responseDto.setIsActive(savedCity.getIsActive());

        return responseDto;
    }

    @Override
    public CityDto updateCity(Long cityId, CityDto cityDto) throws ResourceNotFoundException, BusinessException {

        City city = cityRepo.findById(cityId)
                .orElseThrow(() -> new BusinessException("City not found with id "+cityId));


        // 📝 Update fields only if new values are provided
        if (cityDto.getName() != null) city.setName(cityDto.getName());
        if (cityDto.getState() != null) city.setState(cityDto.getState());
        if (cityDto.getCountry() != null) city.setCountry(cityDto.getCountry());
        if (cityDto.getTimezone() != null) city.setTimezone(cityDto.getTimezone());

        // ✅ Optional: handle isActive explicitly
        if (cityDto.getIsActive() != null) {
            city.setIsActive(cityDto.getIsActive());
        }

        // 💾 Save updated city entity
        City updatedCity = cityRepo.save(city);

        // 🔁 Convert back to DTO and return
        CityDto responseDto = new CityDto();
        responseDto.setId(updatedCity.getId());
        responseDto.setName(updatedCity.getName());
        responseDto.setState(updatedCity.getState());
        responseDto.setCountry(updatedCity.getCountry());
        responseDto.setTimezone(updatedCity.getTimezone());
        responseDto.setIsActive(updatedCity.getIsActive());

        return responseDto;
    }

    @Override
    public void deleteCity(Long cityId) throws ResourceNotFoundException {

        City city = getCityEntityById(cityId);
        cityRepo.delete(city);

    }

    @Override
    public CityDto getCityById(Long cityId) throws ResourceNotFoundException {

        City city = cityRepo.findById(cityId)
                .orElseThrow(()-> new ResourceNotFoundException("City not found with id " + cityId));

        return convertToDto(city);

    }

    @Override
    public City getCityEntityById(Long cityId) throws ResourceNotFoundException {
        return cityRepo.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id " + cityId));    }

    @Override
    public List<CityDto> getAllCities() {

        List<City> cities = cityRepo.findAll();

        return cities.stream()
                .map(this::convertToDto) // Convert each entity to DTO
                .toList();               // Collect into List<CityDto>

    }

    @Override
    public List<CityDto> getActiveCities() {

        List<City> cities = cityRepo.findByIsActiveTrue();
        List<CityDto> cityDtos = new ArrayList<>();

        for (City city : cities) {
            cityDtos.add(convertToDto(city));  // Convert each to DTO
        }

        return cityDtos;
    }

    @Override
    public List<CityDto> findCitiesByState(String state) {

        List<City> cities = cityRepo.findByStateIgnoreCase(state);
        List<CityDto> cityDtos = new ArrayList<>();

        for (City city : cities) {
            cityDtos.add(convertToDto(city));  // Convert each to DTO
        }

        return cityDtos;

    }

    @Override
    public List<CityDto> findCitiesByCountry(String country) {

        List<City> cities = cityRepo.findByCountryIgnoreCase(country);
        List<CityDto> cityDtos = new ArrayList<>();

        for (City city : cities) {
            cityDtos.add(convertToDto(city));  // Convert each to DTO
        }

        return cityDtos;
    }

    @Override
    public String getCityTimezone(Long cityId) throws ResourceNotFoundException {

        City city = cityRepo.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City not found with id "+cityId));

        return city.getTimezone();   // Return the timezone of the city
    }

    @Override
    public List<CityDto> searchCities(String keyword) {

        List<City> cities = cityRepo.searchCities(keyword);
        List<CityDto> cityDtos = new ArrayList<>();

        for (City city : cities) {
            cityDtos.add(convertToDto(city));  // Convert each to DTO
        }

        return cityDtos;

    }


    private CityDto convertToDto(City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setState(city.getState());
        dto.setCountry(city.getCountry());
        dto.setTimezone(city.getTimezone());
        dto.setIsActive(city.getIsActive());
        return dto;
    }


}
