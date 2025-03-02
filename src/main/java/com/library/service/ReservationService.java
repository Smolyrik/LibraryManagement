package com.library.service;

import com.library.dto.ReservationDto;
import com.library.entity.Book;
import com.library.entity.Reservation;
import com.library.entity.ReservationStatus;
import com.library.entity.User;
import com.library.mapper.ReservationMapper;
import com.library.repository.BookRepository;
import com.library.repository.ReservationRepository;
import com.library.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationDto addReservation(ReservationDto reservationDto) {
        Reservation reservation = reservationMapper.toEntity(reservationDto);
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Added new reservation with ID: {}", savedReservation.getReservationId());
        return reservationMapper.toDto(savedReservation);
    }

    public ReservationDto getReservationById(Integer reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservationMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Reservation with ID: {} not found", reservationId);
                    return new NoSuchElementException("Reservation with ID: " + reservationId + " not found");
                });
    }

    public List<ReservationDto> getAllReservations() {
        log.info("Fetching all reservations");
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationDto updateReservation(Integer reservationId, ReservationDto reservationDto) {
        if (!reservationRepository.existsById(reservationId)) {
            log.error("Reservation with ID: {} not found", reservationId);
            throw new NoSuchElementException("Reservation with ID: " + reservationId + " not found");
        }

        Reservation updatedReservation = reservationMapper.toEntity(reservationDto);
        updatedReservation.setReservationId(reservationId);

        Reservation savedReservation = reservationRepository.save(updatedReservation);
        log.info("Updated reservation with ID: {}", savedReservation.getReservationId());

        return reservationMapper.toDto(savedReservation);
    }

    @Transactional
    public void deleteReservation(Integer reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            log.error("Reservation with ID: {} not found", reservationId);
            throw new NoSuchElementException("Reservation with ID: " + reservationId + " not found");
        }
        reservationRepository.deleteById(reservationId);
        log.info("Deleted reservation with ID: {}", reservationId);
    }

    @Transactional
    public ReservationDto reserveBook(Integer userId, Integer bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with ID: " + userId + " not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NoSuchElementException("Book with ID: " + bookId + " not found"));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setBook(book);
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reserved book with ID: {} for user ID: {}", bookId, userId);
        return reservationMapper.toDto(savedReservation);
    }

    @Transactional
    public void cancelReservation(Integer reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            log.error("Reservation with ID: {} not found", reservationId);
            throw new NoSuchElementException("Reservation with ID: " + reservationId + " not found");
        }
        reservationRepository.deleteById(reservationId);
        log.info("Cancelled reservation with ID: {}", reservationId);
    }

    public List<ReservationDto> getReservationsByUser(Integer userId) {
        log.info("Fetching reservations for user ID: {}", userId);
        return reservationRepository.findByUserUserId(userId).stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }
}
