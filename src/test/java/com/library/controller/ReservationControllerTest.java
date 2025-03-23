package com.library.controller;

import com.library.dto.ReservationDto;
import com.library.service.impl.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationServiceImpl reservationService;

    @InjectMocks
    private ReservationController reservationController;

    private ReservationDto reservationDto;
    private Integer reservationId;
    private Integer userId;
    private Integer bookId;

    @BeforeEach
    void setUp() {
        reservationId = 1;
        userId = 2;
        bookId = 3;
        reservationDto = ReservationDto.builder()
                .reservationId(reservationId)
                .userId(userId)
                .bookId(bookId)
                .reservationTime(LocalDateTime.now())
                .status("CONFIRMED")
                .build();
    }

    @Test
    void addReservation_ShouldReturnReservation() {
        when(reservationService.addReservation(any(ReservationDto.class))).thenReturn(reservationDto);
        ResponseEntity<ReservationDto> response = reservationController.addReservation(reservationDto);

        assertNotNull(response.getBody());
        assertEquals(reservationDto.getReservationId(), response.getBody().getReservationId());
        verify(reservationService, times(1)).addReservation(any(ReservationDto.class));
    }

    @Test
    void getReservationById_ShouldReturnReservation() {
        when(reservationService.getReservationById(reservationId)).thenReturn(reservationDto);
        ResponseEntity<ReservationDto> response = reservationController.getReservationById(reservationId);

        assertNotNull(response.getBody());
        assertEquals(reservationId, response.getBody().getReservationId());
        verify(reservationService, times(1)).getReservationById(reservationId);
    }

    @Test
    void getAllReservations_ShouldReturnReservationList() {
        when(reservationService.getAllReservations()).thenReturn(Collections.singletonList(reservationDto));
        ResponseEntity<List<ReservationDto>> response = reservationController.getAllReservations();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(reservationService, times(1)).getAllReservations();
    }

    @Test
    void updateReservation_ShouldReturnUpdatedReservation() {
        when(reservationService.updateReservation(eq(reservationId), any(ReservationDto.class))).thenReturn(reservationDto);
        ResponseEntity<ReservationDto> response = reservationController.updateReservation(reservationId, reservationDto);

        assertNotNull(response.getBody());
        assertEquals(reservationId, response.getBody().getReservationId());
        verify(reservationService, times(1)).updateReservation(eq(reservationId), any(ReservationDto.class));
    }

    @Test
    void deleteReservation_ShouldReturnNoContent() {
        doNothing().when(reservationService).deleteReservation(reservationId);
        ResponseEntity<Void> response = reservationController.deleteReservation(reservationId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService, times(1)).deleteReservation(reservationId);
    }

    @Test
    void reserveBook_ShouldReturnReservation() {
        when(reservationService.reserveBook(userId, bookId)).thenReturn(reservationDto);
        ResponseEntity<ReservationDto> response = reservationController.reserveBook(userId, bookId);

        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUserId());
        assertEquals(bookId, response.getBody().getBookId());
        verify(reservationService, times(1)).reserveBook(userId, bookId);
    }

    @Test
    void cancelReservation_ShouldReturnNoContent() {
        doNothing().when(reservationService).cancelReservation(reservationId);
        ResponseEntity<Void> response = reservationController.cancelReservation(reservationId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reservationService, times(1)).cancelReservation(reservationId);
    }

    @Test
    void getReservationsByUser_ShouldReturnReservationList() {
        when(reservationService.getReservationsByUser(userId)).thenReturn(Collections.singletonList(reservationDto));
        ResponseEntity<List<ReservationDto>> response = reservationController.getReservationsByUser(userId);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(reservationService, times(1)).getReservationsByUser(userId);
    }
}
