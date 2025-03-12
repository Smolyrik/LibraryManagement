package com.library.service;

import com.library.dto.ReservationDto;

import java.util.List;

public interface ReservationService {

    ReservationDto addReservation(ReservationDto reservationDto);

    ReservationDto getReservationById(Integer reservationId);

    List<ReservationDto> getAllReservations();

    ReservationDto updateReservation(Integer reservationId, ReservationDto reservationDto);

    void deleteReservation(Integer reservationId);

    ReservationDto reserveBook(Integer userId, Integer bookId);

    void cancelReservation(Integer reservationId);

    List<ReservationDto> getReservationsByUser(Integer userId);
}
