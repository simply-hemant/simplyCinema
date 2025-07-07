package com.simply.Cinema.service.location_and_venue.impl;

import com.simply.Cinema.core.location_and_venue.dto.OperatingHoursDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreRequestDto;
import com.simply.Cinema.core.location_and_venue.dto.TheatreResponseDto;
import com.simply.Cinema.core.location_and_venue.entity.Theatre;
import com.simply.Cinema.core.location_and_venue.repository.TheatreRepo;
import com.simply.Cinema.core.systemConfig.Enums.AuditAction;
import com.simply.Cinema.exception.AuthorizationException;
import com.simply.Cinema.exception.BusinessException;
import com.simply.Cinema.exception.ResourceNotFoundException;
import com.simply.Cinema.service.location_and_venue.TheatreService;
import com.simply.Cinema.service.systemConfig.impl.AuditLogService;
import com.simply.Cinema.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TheatreServiceImpl implements TheatreService {

    private final TheatreRepo theatreRepo;
    private final AuditLogService auditLogService;

    @Override
    public TheatreResponseDto createTheatre(TheatreRequestDto requestDto) throws BusinessException {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        boolean theatreExist = theatreRepo.existsByNameIgnoreCaseAndCityId(requestDto.getName(), requestDto.getCityId());
        if (theatreExist) {
            throw new BusinessException("Theatre with the same name and city already exists. ");
        }

        Theatre theatre = new Theatre();

        theatre.setName(requestDto.getName());
        theatre.setOwnerId(currentUserId);
        theatre.setCityId(requestDto.getCityId());
        theatre.setLatitude(requestDto.getLatitude());
        theatre.setLongitude(requestDto.getLongitude());
        theatre.setPhone(requestDto.getPhone());
        theatre.setEmail(requestDto.getEmail());
        theatre.setAmenities(requestDto.getAmenities());
        theatre.setFoodBeverageAvailable(requestDto.getFoodBeverageAvailable());
        theatre.setAddress(requestDto.getAddress());

        theatre.setOpeningHour(requestDto.getOpeningHour());
        theatre.setClosingHour(requestDto.getClosingHour());

        theatre.setIsActive(true);

        Theatre savedTheatre = theatreRepo.save(theatre);

        // 🔹 Convert back to DTO
        TheatreResponseDto responseDto = new TheatreResponseDto();

        responseDto.setName(savedTheatre.getName());
        responseDto.setId(savedTheatre.getId());
        responseDto.setCityId(savedTheatre.getCityId());
        responseDto.setAddress(savedTheatre.getAddress());
        responseDto.setLatitude(savedTheatre.getLatitude());
        responseDto.setLongitude(savedTheatre.getLongitude());
        responseDto.setPhone(savedTheatre.getPhone());

        responseDto.setAmenities(savedTheatre.getAmenities());

        responseDto.setEmail(savedTheatre.getEmail());
        responseDto.setIsActive(true);
        responseDto.setFoodBeverageAvailable(savedTheatre.getFoodBeverageAvailable());
        responseDto.setOpeningHour(savedTheatre.getOpeningHour());
        responseDto.setClosingHour(savedTheatre.getClosingHour());

        auditLogService.logEvent("Theatre", AuditAction.CREATE, savedTheatre.getId(), savedTheatre.getOwnerId());

        return responseDto;
    }

    @Override
    public TheatreResponseDto updateTheatre(Long theatreId, TheatreRequestDto requestDto)
            throws ResourceNotFoundException, BusinessException {

        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + theatreId));

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (!theatre.getOwnerId().equals(currentUserId)) {
            throw new BusinessException("Access denied. You are not the owner of this theatre.");
        }

        // 🔍 Check for duplicate only if name or city changed and request value is not null
        boolean isNameChanged = requestDto.getName() != null && !requestDto.getName().equalsIgnoreCase(theatre.getName());
        boolean isCityChanged = requestDto.getCityId() != null && !requestDto.getCityId().equals(theatre.getCityId());

        if (isNameChanged || isCityChanged) {
            String newName = requestDto.getName() != null ? requestDto.getName() : theatre.getName();
            Long newCityId = requestDto.getCityId() != null ? requestDto.getCityId() : theatre.getCityId();

            boolean duplicateExists = theatreRepo.existsByNameIgnoreCaseAndCityId(newName, newCityId);
            if (duplicateExists) {
                throw new BusinessException("Another theatre with the same name in this city already exists.");
            }
        }

        // ✅ Update only if user has sent value (i.e. not null)
        if (requestDto.getName() != null) theatre.setName(requestDto.getName());
        if (requestDto.getCityId() != null) theatre.setCityId(requestDto.getCityId());
        if (requestDto.getAddress() != null) theatre.setAddress(requestDto.getAddress());
        if (requestDto.getLatitude() != null) theatre.setLatitude(requestDto.getLatitude());
        if (requestDto.getLongitude() != null) theatre.setLongitude(requestDto.getLongitude());
        if (requestDto.getPhone() != null) theatre.setPhone(requestDto.getPhone());
        if (requestDto.getEmail() != null) theatre.setEmail(requestDto.getEmail());
        if (requestDto.getAmenities() != null) theatre.setAmenities(requestDto.getAmenities());
        if (requestDto.getFoodBeverageAvailable() != null) theatre.setFoodBeverageAvailable(requestDto.getFoodBeverageAvailable());
        if (requestDto.getOpeningHour() != null) theatre.setOpeningHour(requestDto.getOpeningHour());
        if (requestDto.getClosingHour() != null) theatre.setClosingHour(requestDto.getClosingHour());
        if (requestDto.getOwnerId() != null) theatre.setOwnerId(requestDto.getOwnerId()); // optional

        Theatre updated = theatreRepo.save(theatre);

        auditLogService.logEvent("Theatre", AuditAction.UPDATE, theatreId, currentUserId);

        // Build and return response
        TheatreResponseDto responseDto = new TheatreResponseDto();
        responseDto.setId(updated.getId());
        responseDto.setName(updated.getName());
        responseDto.setCityId(updated.getCityId());
        responseDto.setAddress(updated.getAddress());
        responseDto.setLatitude(updated.getLatitude());
        responseDto.setLongitude(updated.getLongitude());
        responseDto.setPhone(updated.getPhone());
        responseDto.setEmail(updated.getEmail());
        responseDto.setAmenities(updated.getAmenities());
        responseDto.setFoodBeverageAvailable(updated.getFoodBeverageAvailable());
        responseDto.setOpeningHour(updated.getOpeningHour());
        responseDto.setClosingHour(updated.getClosingHour());
        responseDto.setIsActive(updated.getIsActive());

        return responseDto;
    }

    @Override
    public void deleteTheatre(Long theatreId) throws ResourceNotFoundException, AuthorizationException {

        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + theatreId));

        Long currentUserId = SecurityUtil.getCurrentUserId();// Extract from JWT

        if (!theatre.getOwnerId().equals(currentUserId)) {
            throw new AuthorizationException("You are not Authorized to delete this theatre.");
        }

        auditLogService.logEvent("Theatre", AuditAction.DELETE, theatreId, currentUserId);


        theatreRepo.delete(theatre);
    }

    @Override
    public TheatreResponseDto getTheatreById(Long theatreId) throws ResourceNotFoundException {

        Theatre existingTheatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with id: " + theatreId));

        TheatreResponseDto responseDto = new TheatreResponseDto();

        responseDto.setId(existingTheatre.getId());
        responseDto.setName(existingTheatre.getName());
        responseDto.setCityId(existingTheatre.getCityId());
        responseDto.setAddress(existingTheatre.getAddress());
        responseDto.setLatitude(existingTheatre.getLatitude());
        responseDto.setLongitude(existingTheatre.getLongitude());
        responseDto.setPhone(existingTheatre.getPhone());
        responseDto.setEmail(existingTheatre.getEmail());
        responseDto.setAmenities(existingTheatre.getAmenities());
        responseDto.setIsActive(existingTheatre.getIsActive());
        responseDto.setFoodBeverageAvailable(existingTheatre.getFoodBeverageAvailable());
        responseDto.setOpeningHour(existingTheatre.getOpeningHour());
        responseDto.setClosingHour(existingTheatre.getClosingHour());

        return responseDto;

    }

    @Override
    public List<TheatreResponseDto> getTheatreByCity(Long cityId) throws ResourceNotFoundException {

        List<Theatre> theatres = theatreRepo.findByCityId(cityId);

        if (theatres.isEmpty()) {
            throw new ResourceNotFoundException("No theatres found in city with ID: " + cityId);
        }

        // Convert each Theatre entity to TheatreResponseDto
        List<TheatreResponseDto> responseList = new ArrayList<>();

        for (Theatre theatre : theatres) {
            TheatreResponseDto dto = new TheatreResponseDto();

            dto.setId(theatre.getId());
            dto.setName(theatre.getName());
            dto.setCityId(theatre.getCityId());
            dto.setAddress(theatre.getAddress());
            dto.setLatitude(theatre.getLatitude());
            dto.setLongitude(theatre.getLongitude());
            dto.setPhone(theatre.getPhone());
            dto.setEmail(theatre.getEmail());
            dto.setAmenities(theatre.getAmenities());
            dto.setIsActive(theatre.getIsActive());
            dto.setFoodBeverageAvailable(theatre.getFoodBeverageAvailable());
            dto.setOpeningHour(theatre.getOpeningHour());
            dto.setClosingHour(theatre.getClosingHour());

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public Page<TheatreResponseDto> getAllTheatre(int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Theatre> theatrePage = theatreRepo.findAll(pageable);

        if (theatrePage.getContent().isEmpty()) {
            throw new ResourceNotFoundException("No theatres found.");
        }

        List<TheatreResponseDto> dtoList = new ArrayList<>();

        for (Theatre theatre : theatrePage.getContent()) {
            TheatreResponseDto dto = new TheatreResponseDto();
            dto.setId(theatre.getId());
            dto.setName(theatre.getName());
            dto.setCityId(theatre.getCityId());
            dto.setAddress(theatre.getAddress());
            dto.setLatitude(theatre.getLatitude());
            dto.setLongitude(theatre.getLongitude());
            dto.setPhone(theatre.getPhone());
            dto.setEmail(theatre.getEmail());
            dto.setAmenities(theatre.getAmenities());
            dto.setIsActive(theatre.getIsActive());
            dto.setFoodBeverageAvailable(theatre.getFoodBeverageAvailable());
            dto.setOpeningHour(theatre.getOpeningHour());
            dto.setClosingHour(theatre.getClosingHour());

            dtoList.add(dto);
        }

        // PageImpl - It wraps your custom DTO list into a paginated response:
        return new PageImpl<>(dtoList, pageable, theatrePage.getTotalElements());
    }

    @Override
    public List<TheatreResponseDto> getTheatreByOwner(Long requestedOwnerId
            , Long currentUserId
            , boolean isAdmin) throws ResourceNotFoundException, AccessDeniedException {

        if (!isAdmin && !requestedOwnerId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to access another owner's theatres.");
        }

        List<Theatre> theatres = theatreRepo.findTheatreByOwnerId(requestedOwnerId);

        if (theatres.isEmpty()) {
            throw new ResourceNotFoundException("No theatres found for owner with ID: " + requestedOwnerId);
        }

        List<TheatreResponseDto> responseList = new ArrayList<>();

        for (Theatre theatre : theatres) {
            TheatreResponseDto dto = new TheatreResponseDto();
            dto.setId(theatre.getId());
            dto.setName(theatre.getName());
            dto.setCityId(theatre.getCityId());
            dto.setAddress(theatre.getAddress());
            dto.setLatitude(theatre.getLatitude());
            dto.setLongitude(theatre.getLongitude());
            dto.setPhone(theatre.getPhone());
            dto.setEmail(theatre.getEmail());
            dto.setAmenities(theatre.getAmenities());
            dto.setIsActive(theatre.getIsActive());
            dto.setFoodBeverageAvailable(theatre.getFoodBeverageAvailable());
            dto.setOpeningHour(theatre.getOpeningHour());
            dto.setClosingHour(theatre.getClosingHour());

            responseList.add(dto);
        }

        return responseList;

    }

    @Override
    public List<TheatreResponseDto> getTheatreByAmenities(List<String> amenities) throws ResourceNotFoundException {

        if (amenities == null || amenities.isEmpty()) {
            throw new IllegalArgumentException("Amenities list cannot be empty.");
        }

        List<Theatre> theatres = theatreRepo.findByAllAmenities(amenities, amenities.size());

        if (theatres.isEmpty()) {
            throw new ResourceNotFoundException("No theatres found with all specified amenities.");
        }

        List<TheatreResponseDto> responseList = new ArrayList<>();

        for (Theatre theatre : theatres) {
            TheatreResponseDto dto = new TheatreResponseDto();
            dto.setId(theatre.getId());
            dto.setName(theatre.getName());
            dto.setCityId(theatre.getCityId());
            dto.setAddress(theatre.getAddress());
            dto.setLatitude(theatre.getLatitude());
            dto.setLongitude(theatre.getLongitude());
            dto.setPhone(theatre.getPhone());
            dto.setEmail(theatre.getEmail());
            dto.setAmenities(theatre.getAmenities());
            dto.setIsActive(theatre.getIsActive());
            dto.setFoodBeverageAvailable(theatre.getFoodBeverageAvailable());
            dto.setOpeningHour(theatre.getOpeningHour());
            dto.setClosingHour(theatre.getClosingHour());

            responseList.add(dto);
        }

        return responseList;
    }

    @Override
    public TheatreResponseDto updateTheatreOperatingHours(Long theatreId, OperatingHoursDto hoursDto) throws ResourceNotFoundException {

        Theatre theatre = theatreRepo.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre with this id not found: " + theatreId));

        // ✅ Get currently logged-in user ID
        Long currentUserId = SecurityUtil.getCurrentUserId();

        // ✅ Only the owner who created it can update
        if (!theatre.getOwnerId().equals(currentUserId)) {
            throw new BusinessException("Access denied. You are not the owner of this theatre.");
        }

        // Set updated times
        theatre.setOpeningHour(hoursDto.getOpeningTime());
        theatre.setClosingHour(hoursDto.getClosingTime());

        Theatre updated = theatreRepo.save(theatre);

        //  Build response DTO (only essential fields shown here)
        TheatreResponseDto dto = new TheatreResponseDto();
        dto.setId(updated.getId());
        dto.setName(updated.getName());
        dto.setOpeningHour(updated.getOpeningHour());
        dto.setClosingHour(updated.getClosingHour());
        return dto;

    }

    @Override
    public Page<TheatreResponseDto> searchTheatre(String keyword, int pageNo, int pageSize) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Theatre> theatrePage = theatreRepo.findByNameContainingIgnoreCase(keyword, pageable);

        if (theatrePage.getContent().isEmpty()) {
            throw new ResourceNotFoundException("No theatres found matching: " + keyword);
        }

        List<TheatreResponseDto> dtoList = new ArrayList<>();

        for (Theatre theatre : theatrePage.getContent()) {
            TheatreResponseDto dto = new TheatreResponseDto();
            dto.setId(theatre.getId());
            dto.setName(theatre.getName());
            dto.setCityId(theatre.getCityId());
            dto.setAddress(theatre.getAddress());
            dto.setLatitude(theatre.getLatitude());
            dto.setLongitude(theatre.getLongitude());
            dto.setPhone(theatre.getPhone());
            dto.setEmail(theatre.getEmail());
            dto.setAmenities(theatre.getAmenities());
            dto.setIsActive(theatre.getIsActive());
            dto.setFoodBeverageAvailable(theatre.getFoodBeverageAvailable());
            dto.setOpeningHour(theatre.getOpeningHour());
            dto.setClosingHour(theatre.getClosingHour());

            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, pageable, theatrePage.getTotalElements());
    }

}
