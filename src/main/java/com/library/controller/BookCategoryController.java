package com.library.controller;

import com.library.dto.BookCategoryDto;
import com.library.service.impl.BookCategoryServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-categories")
@AllArgsConstructor
@Tag(name = "Book-Category Management", description = "Operations related to book-category associations")
public class BookCategoryController {

    private final BookCategoryServiceImpl bookCategoryService;

    @Operation(
            summary = "Add a new book-category association",
            description = "Links a book with a category.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created book-category association",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookCategoryDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<BookCategoryDto> addBookCategory(@Valid @RequestBody BookCategoryDto bookCategoryDto) {
        BookCategoryDto savedBookCategory = bookCategoryService.addBookCategory(bookCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookCategory);
    }

    @Operation(
            summary = "Get book-category association by IDs",
            description = "Retrieves the association between a book and a category based on their IDs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved book-category association",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookCategoryDto.class))),
                    @ApiResponse(responseCode = "404", description = "Book-category association not found")
            }
    )
    @GetMapping("/{bookId}/{categoryId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<BookCategoryDto> getBookCategoryById(
            @Parameter(description = "ID of the book") @PathVariable Integer bookId,
            @Parameter(description = "ID of the category") @PathVariable Integer categoryId) {
        BookCategoryDto bookCategory = bookCategoryService.getBookCategoryById(bookId, categoryId);
        return ResponseEntity.ok(bookCategory);
    }

    @Operation(
            summary = "Get all book-category associations",
            description = "Retrieves a list of all book-category associations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of book-category associations",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BookCategoryDto.class))))
            }
    )
    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<BookCategoryDto>> getAllBookCategories() {
        List<BookCategoryDto> bookCategories = bookCategoryService.getAllBookCategories();
        return ResponseEntity.ok(bookCategories);
    }

    @Operation(
            summary = "Delete book-category association",
            description = "Removes the link between a book and a category using their IDs.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted book-category association"),
                    @ApiResponse(responseCode = "404", description = "Book-category association not found")
            }
    )
    @DeleteMapping("/{bookId}/{categoryId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteBookCategory(
            @Parameter(description = "ID of the book") @PathVariable Integer bookId,
            @Parameter(description = "ID of the category") @PathVariable Integer categoryId) {
        bookCategoryService.deleteBookCategory(bookId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
