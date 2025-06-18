package com.simply.Cinema.service.Location_and_venue.CityServiceImpl;

import com.simply.Cinema.core.location_and_venue.entity.City;
import com.simply.Cinema.core.location_and_venue.repository.CityRepo;
import com.simply.Cinema.service.Location_and_venue.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepo cityRepo;

    @Override
    public List<City> getAllCities() {
        return cityRepo.findAll();
    }
}
