package com.library.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookAuthorDto {

    @NotNull(message = "Book Id must not be null")
    private Integer bookId;

    @NotNull(message = "Author Id must not be null")
    private Integer authorId;

}
