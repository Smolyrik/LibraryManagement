package com.library.dto;


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
public class CategoryDto {

    private Integer categoryId;

    @NotNull(message = "Category name must not be null")
    @Size(min = 1, max = 50, message = "Category name must be between 1 and 50 characters")
    private String categoryName;

    @NotNull(message = "Description must not be null")
    @Size(min = 1, max = 50, message = "Description name must be between 1 and 50 characters")
    private String description;

}
