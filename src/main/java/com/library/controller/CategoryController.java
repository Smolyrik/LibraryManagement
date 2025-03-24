package com.library.controller;

import com.library.dto.CategoryDto;
import com.library.service.impl.CategoryServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management", description = "Operations related to book categories")
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    @Operation(
            summary = "Add a new category",
            description = "Creates a new category record.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created category",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.addCategory(categoryDto));
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a category by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved category",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @Operation(
            summary = "Get all categories",
            description = "Retrieves a list of all categories.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of categories",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Operation(
            summary = "Update category",
            description = "Updates an existing category record.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated category",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDto.class))),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    @Operation(
            summary = "Delete category",
            description = "Removes a category record by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted category"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            }
    )
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
