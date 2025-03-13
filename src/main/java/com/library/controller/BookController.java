package com.library.controller;

import com.library.dto.BookDto;
import com.library.service.impl.BookServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Operations related to books management")
public class BookController {

    private final BookServiceImpl bookService;

    @Operation(
            summary = "Add a new book",
            description = "Creates a new book record.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created book",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.addBook(bookDto));
    }

    @Operation(
            summary = "Get book by ID",
            description = "Retrieves a book by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved book",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDto.class))),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @Operation(
            summary = "Get all books",
            description = "Retrieves a list of all books.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BookDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @Operation(
            summary = "Update book",
            description = "Updates an existing book record.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated book",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BookDto.class))),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Integer id, @Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }

    @Operation(
            summary = "Delete book",
            description = "Removes a book record by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted book"),
                    @ApiResponse(responseCode = "404", description = "Book not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Search books by title",
            description = "Finds books matching the given title.",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved books")}
    )
    @GetMapping("/search/title")
    public ResponseEntity<List<BookDto>> findBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findBooksByTitle(title));
    }

    @Operation(
            summary = "Search books by available copies range",
            description = "Finds books with available copies within a specified range.",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved books")}
    )
    @GetMapping("/search/available")
    public ResponseEntity<List<BookDto>> findBooksByAvailableCopiesRange(@RequestParam Integer min, @RequestParam Integer max) {
        return ResponseEntity.ok(bookService.findBooksByAvailableCopiesRange(min, max));
    }

    @Operation(
            summary = "Search books by author",
            description = "Finds books written by a specific author.",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved books")}
    )
    @GetMapping("/search/author")
    public ResponseEntity<List<BookDto>> findBooksByAuthor(@RequestParam String authorName) {
        return ResponseEntity.ok(bookService.findBooksByAuthor(authorName));
    }

    @Operation(
            summary = "Search books by category",
            description = "Finds books belonging to a specific category.",
            responses = {@ApiResponse(responseCode = "200", description = "Successfully retrieved books")}
    )
    @GetMapping("/search/category")
    public ResponseEntity<List<BookDto>> findBooksByCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(bookService.findBooksByCategory(categoryName));
    }
}
