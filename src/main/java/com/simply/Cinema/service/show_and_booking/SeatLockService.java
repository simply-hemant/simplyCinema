package com.simply.Cinema.service.show_and_booking;

import com.simply.Cinema.exception.*;

import java.util.List;

public interface SeatLockService{

    void lockSeats(Long showId, List<Long> seatIds, Long userId)
            throws ResourceNotFoundException, SeatLockException, ValidationException,
            AuthenticationException, AuthorizationException;

    void releaseLockedSeats(Long showId, List<Long> seatNumbers, Long userId)
            throws ResourceNotFoundException, AuthorizationException, ValidationException;

    boolean checkSeatLockStatus(Long showId, Long seatNumber)
            throws ResourceNotFoundException, ValidationException;

    List<String> getLockedSeats(Long showId) throws ResourceNotFoundException;

}


//
///**
// * Lock the given list of seats for a particular show.
// *
// * @param showId      ID of the show
// * @param seatNumbers List of seat numbers to be locked
// * @param userId      ID of the user requesting the lock
// * @throws ResourceNotFoundException if the show or seat is not found
// * @throws SeatLockException         if any seat is already locked or booked
// * @throws ValidationException       if seat numbers are empty or invalid
// * @throws AuthenticationException   if user is not authenticated
// * @throws AuthorizationException    if user is not allowed to lock seats
// */
//void lockSeats(Long showId, List<String> seatNumbers, Long userId)
//        throws ResourceNotFoundException, SeatLockException, ValidationException,
//        AuthenticationException, AuthorizationException;
//
///**
// * Release previously locked seats (manually or on cancel).
// *
// * @param showId      ID of the show
// * @param seatNumbers List of seat numbers to be released
// * @param userId      ID of the user who locked the seats
// * @throws ResourceNotFoundException if show or seat is not found
// * @throws AuthorizationException    if user is not allowed to release the lock
// * @throws ValidationException       if inputs are invalid
// */
//void releaseLockedSeats(Long showId, List<String> seatNumbers, Long userId)
//        throws ResourceNotFoundException, AuthorizationException, ValidationException;
//
///**
// * Check whether a specific seat is currently locked for a show.
// *
// * @param showId     ID of the show
// * @param seatNumber Seat number to check
// * @return true if the seat is locked and not expired
// * @throws ResourceNotFoundException if seat or show is not found
// * @throws ValidationException       if seat number is invalid
// */
//boolean checkSeatLockStatus(Long showId, String seatNumber)
//        throws ResourceNotFoundException, ValidationException;
//
///**
// * Get a list of all seat numbers currently locked for a show.
// *
// * @param showId ID of the show
// * @return List of locked seat numbers
// * @throws ResourceNotFoundException if the show is not found
// */
//List<String> getLockedSeats(Long showId) throws ResourceNotFoundException;
