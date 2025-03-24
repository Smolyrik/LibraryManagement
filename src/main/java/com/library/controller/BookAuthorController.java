package com.library.controller;

import com.library.dto.BookAuthorDto;
import com.library.service.impl.BookAuthorServiceImpl;
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
@RequestMapping("/api/book-authors")
@AllArgsConstructor
@Tag(name = "Book-Author Management", description = "Operations related to book-author associations")
public class BookAuthorController {

    private final BookAuthorServiceImpl bookAuthorService;

    @Operation(
            summary = "Add a new book-author association",
            description = "Links a book with an author.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created book-author association",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookAuthorDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<BookAuthorDto> addBookAuthor(@Valid @RequestBody BookAuthorDto bookAuthorDto) {
        BookAuthorDto savedBookAuthor = bookAuthorService.addBookAuthor(bookAuthorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookAuthor);
    }

    @Operation(
            summary = "Get book-author association by IDs",
            description = "Retrieves the association between a book and an author based on their IDs.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved book-author association",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookAuthorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Book-author association not found")
            }
    )
    @GetMapping("/{bookId}/{authorId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<BookAuthorDto> getBookAuthorById(
            @Parameter(description = "ID of the book") @PathVariable Integer bookId,
            @Parameter(description = "ID of the author") @PathVariable Integer authorId) {
        BookAuthorDto bookAuthor = bookAuthorService.getBookAuthorById(bookId, authorId);
        return ResponseEntity.ok(bookAuthor);
    }

    @Operation(
            summary = "Get all book-author associations",
            description = "Retrieves a list of all book-author associations.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of book-author associations",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BookAuthorDto.class))))
            }
    )
    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<BookAuthorDto>> getAllBookAuthors() {
        List<BookAuthorDto> bookAuthors = bookAuthorService.getAllBookAuthors();
        return ResponseEntity.ok(bookAuthors);
    }

    @Operation(
            summary = "Delete book-author association",
            description = "Removes the link between a book and an author using their IDs.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted book-author association"),
                    @ApiResponse(responseCode = "404", description = "Book-author association not found")
            }
    )
    @DeleteMapping("/{bookId}/{authorId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteBookAuthor(
            @Parameter(description = "ID of the book") @PathVariable Integer bookId,
            @Parameter(description = "ID of the author") @PathVariable Integer authorId) {
        bookAuthorService.deleteBookAuthor(bookId, authorId);
        return ResponseEntity.noContent().build();
    }
}
