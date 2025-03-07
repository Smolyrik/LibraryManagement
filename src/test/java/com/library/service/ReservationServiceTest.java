package com.library.service;

import com.library.dto.ReservationDto;
import com.library.entity.Reservation;
import com.library.mapper.ReservationMapper;
import com.library.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    private Reservation reservation;
    private ReservationDto reservationDto;

    @BeforeEach
    void setUp() {
        reservation = new Reservation(1, null, null, null, null);
        reservationDto = new ReservationDto(1, 1, 1, null, "CONFIRMED");
    }

    @Test
    void addReservation_ShouldReturnSavedReservationDto() {
        when(reservationMapper.toEntity(reservationDto)).thenReturn(reservation);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toDto(reservation)).thenReturn(reservationDto);

        ReservationDto savedReservation = reservationService.addReservation(reservationDto);

        assertNotNull(savedReservation);
        assertEquals(reservationDto.getReservationId(), savedReservation.getReservationId());
        verify(reservationRepository, times(1)).save(reservation);
    }

    @Test
    void getReservationById_WhenReservationExists_ShouldReturnReservationDto() {
        when(reservationRepository.findById(1)).thenReturn(Optional.of(reservation));
        when(reservationMapper.toDto(reservation)).thenReturn(reservationDto);

        ReservationDto foundReservation = reservationService.getReservationById(1);

        assertNotNull(foundReservation);
        assertEquals(reservationDto.getReservationId(), foundReservation.getReservationId());
    }

    @Test
    void getReservationById_WhenReservationDoesNotExist_ShouldThrowException() {
        when(reservationRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reservationService.getReservationById(1));
    }

    @Test
    void getAllReservations_ShouldReturnListOfReservationDtos() {
        List<Reservation> reservations = Collections.singletonList(reservation);

        when(reservationRepository.findAll()).thenReturn(reservations);
        when(reservationMapper.toDto(reservation)).thenReturn(reservationDto);

        List<ReservationDto> foundReservations = reservationService.getAllReservations();

        assertEquals(1, foundReservations.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void updateReservation_WhenReservationExists_ShouldReturnUpdatedReservationDto() {
        when(reservationRepository.existsById(1)).thenReturn(true);
        when(reservationMapper.toEntity(reservationDto)).thenReturn(reservation);
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toDto(reservation)).thenReturn(reservationDto);

        ReservationDto updatedReservation = reservationService.updateReservation(1, reservationDto);

        assertNotNull(updatedReservation);
        assertEquals(reservationDto.getReservationId(), updatedReservation.getReservationId());
    }

    @Test
    void updateReservation_WhenReservationDoesNotExist_ShouldThrowException() {
        when(reservationRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reservationService.updateReservation(1, reservationDto));
    }

    @Test
    void deleteReservation_WhenReservationExists_ShouldDeleteReservation() {
        when(reservationRepository.existsById(1)).thenReturn(true);
        doNothing().when(reservationRepository).deleteById(1);

        assertDoesNotThrow(() -> reservationService.deleteReservation(1));
        verify(reservationRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteReservation_WhenReservationDoesNotExist_ShouldThrowException() {
        when(reservationRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reservationService.deleteReservation(1));
    }
}
