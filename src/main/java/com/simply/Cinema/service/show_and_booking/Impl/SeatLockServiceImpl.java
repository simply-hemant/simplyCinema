package com.simply.Cinema.service.show_and_booking.Impl;

import com.simply.Cinema.core.show_and_booking.dto.SeatLockInfo;
import com.simply.Cinema.exception.*;
import com.simply.Cinema.service.RedisService;
import com.simply.Cinema.service.show_and_booking.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SeatLockServiceImpl implements SeatLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final long LOCK_DURATION = 300; // 5 mins in seconds


//    @Override
//    public void lockSeats(Long showId, List<Long> seatIds, Long userId)
//            throws ResourceNotFoundException, SeatLockException, ValidationException, AuthenticationException, AuthorizationException {
//
//        for (Long seatId : seatIds) {
//            String key = generateKey(showId, seatId); // e.g., lock:show_123_seat_101
//
//            if (redisTemplate.hasKey(key)) {
//                throw new SeatLockException("Seat with ID " + seatId + " is already locked.");
//            }
//
//            SeatLockInfo lockInfo = new SeatLockInfo(userId, showId, seatId, LocalDateTime.now());
//
//            redisTemplate.opsForValue().set(key, lockInfo, Duration.ofSeconds(LOCK_DURATION));
//        }
//    }

    @Autowired
    private RedisService redisService;

    @Override
    public void lockSeats(Long showId, List<Long> seatIds, Long userId)
            throws SeatLockException {

        for (Long seatId : seatIds) {
            String key = generateKey(showId, seatId);
            if (redisService.hasKey(key)) {
                throw new SeatLockException("Seat with ID " + seatId + " is already locked.");
            }

            SeatLockInfo lockInfo = new SeatLockInfo(userId, showId, seatId, LocalDateTime.now());
            redisService.set(key, lockInfo, LOCK_DURATION);
        }
    }



    @Override
    public void releaseLockedSeats(Long showId, List<Long> seatIds, Long userId) {
        for (Long seatId : seatIds) {
            String key = generateKey(showId, seatId);
            try {
                SeatLockInfo lockInfo = redisService.get(key, SeatLockInfo.class);
                if (lockInfo != null && lockInfo.getUserId().equals(userId)) {
                    redisService.delete(key);
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean checkSeatLockStatus(Long showId, Long seatId) {
        String key = generateKey(showId, seatId);
        return redisService.hasKey(key);
    }

    @Override
    public List<String> getLockedSeats(Long showId) {
        String pattern = "seat_lock:" + showId + ":*";
        Set<String> keys = redisService.getKeys(pattern);
        List<String> lockedSeats = new ArrayList<>();

        if (keys != null) {
            for (String key : keys) {
                String[] parts = key.split(":");
                if (parts.length == 3) {
                    lockedSeats.add(parts[2]); // seat ID
                }
            }
        }

        return lockedSeats;
    }

    private String generateKey(Long showId, Long seatId) {
        return "seat_lock:" + showId + ":" + seatId;
    }

}
