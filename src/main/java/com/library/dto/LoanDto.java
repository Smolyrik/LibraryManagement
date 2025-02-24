package com.library.dto;


import com.library.entity.LoanStatus;
import jakarta.validation.constraints.FutureOrPresent;
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
public class LoanDto {

    private Integer loanId;

    @NotNull(message = "Loan status must not be null")
    @Pattern(regexp = "ACTIVE|RETURNED|OVERDUE",
            message = "Status must be ACTIVE, PENDING, or OVERDUE")
    private String status;

    @NotNull(message = "Loan date must not be null")
    @PastOrPresent(message = "Loan date cannot be in the future")
    private LocalDateTime loanDate;

    @NotNull(message = "Return date must not be null")
    @FutureOrPresent(message = "Return date cannot be in the past")
    private LocalDateTime returnDate;

    @NotNull(message = "User Id must not be null")
    private Integer userId;

    @NotNull(message = "Book Id must not be null")
    private Integer bookId;
}
