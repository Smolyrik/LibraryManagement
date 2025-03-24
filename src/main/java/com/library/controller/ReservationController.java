package com.library.controller;

import com.library.dto.ReservationDto;
import com.library.service.impl.ReservationServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation Management", description = "Operations related to book reservations")
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @Operation(
            summary = "Add a new reservation",
            description = "Creates a reservation for a book.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created reservation",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ReservationDto> addReservation(@Valid @RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.addReservation(reservationDto));
    }

    @Operation(
            summary = "Get reservation by ID",
            description = "Retrieves a reservation by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservation",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationDto.class))),
                    @ApiResponse(responseCode = "404", description = "Reservation not found")
            }
    )
    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ReservationDto> getReservationById(@PathVariable Integer id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @Operation(
            summary = "Get all reservations",
            description = "Retrieves a list of all reservations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reservations",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationDto.class))))
            }
    )
    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<ReservationDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @Operation(
            summary = "Update reservation",
            description = "Updates an existing reservation.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated reservation",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationDto.class))),
                    @ApiResponse(responseCode = "404", description = "Reservation not found")
            }
    )
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Integer id, @Valid @RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservationDto));
    }

    @Operation(
            summary = "Delete reservation",
            description = "Deletes a reservation by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted reservation"),
                    @ApiResponse(responseCode = "404", description = "Reservation not found")
            }
    )
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reserve a book",
            description = "Creates a reservation for a book by a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully reserved book",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationDto.class)))
            }
    )
    @PostMapping("/reserve")
    public ResponseEntity<ReservationDto> reserveBook(@RequestParam Integer userId, @RequestParam Integer bookId) {
        return ResponseEntity.ok(reservationService.reserveBook(userId, bookId));
    }

    @Operation(
            summary = "Cancel reservation",
            description = "Cancels an existing reservation.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully canceled reservation"),
                    @ApiResponse(responseCode = "404", description = "Reservation not found")
            }
    )
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Integer id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get reservations by user ID",
            description = "Retrieves a list of reservations for a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved reservations",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReservationDto.class))))
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDto>> getReservationsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUser(userId));
    }
}
