package com.library.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    private Integer reviewId;

    @NotNull(message = "User Id must not be null")
    private Integer userId;

    @NotNull(message = "Book Id must not be null")
    private Integer bookId;

    @NotNull(message = "Comment must not be null")
    @Size(min = 1, max = 500, message = "Comment must be between 1 and 500 characters")
    private String comment;

    @NotNull(message = "Rating must not be null")
    @Min(1)
    @Max(10)
    private Short rating;

    @NotNull(message = "Creation date must not be null")
    @PastOrPresent(message = "Creation date cannot be in the future")
    private LocalDateTime createdAt;
}
