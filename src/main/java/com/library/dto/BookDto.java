package com.library.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Integer bookId;

    @NotNull(message = "Title must not be null")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @NotNull(message = "Description must not be null")
    @Size(min = 1, max = 1000, message = "Description must be between 1 and 1000 characters")
    private String description;

    @NotNull(message = "Available copies must not be null")
    @Min(value = 0, message = "Available copies must be 0 or greater")
    private Integer availableCopies;

    @NotNull(message = "Total copies must not be null")
    @Min(value = 0, message = "Total copies must be 0 or greater")
    private Integer totalCopies;

}
