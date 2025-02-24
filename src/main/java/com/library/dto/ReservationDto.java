package com.library.dto;


import com.library.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

    private Integer reservationId;

    @NotNull(message = "User Id must not be null")
    private Integer userId;

    @NotNull(message = "Book Id must not be null")
    private Integer bookId;

    @NotNull(message = "Reservation time must not be null")
    @PastOrPresent(message = "Reservation time cannot be in the future")
    private LocalDateTime reservationTime;

    @NotNull(message = "Status must not be null")
    @Pattern(regexp = "PENDING|CONFIRMED|CANCELLED",
            message = "Status must be PENDING, CONFIRMED, or CANCELLED")
    private String status;
}
