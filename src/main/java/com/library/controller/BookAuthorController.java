package com.library.controller;

import com.library.dto.BookAuthorDto;
import com.library.service.BookAuthorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-authors")
@AllArgsConstructor
public class BookAuthorController {

    private final BookAuthorService bookAuthorService;

    @PostMapping
    public ResponseEntity<BookAuthorDto> addBookAuthor(@Valid @RequestBody BookAuthorDto bookAuthorDto) {
        BookAuthorDto savedBookAuthor = bookAuthorService.addBookAuthor(bookAuthorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookAuthor);
    }

    @GetMapping("/{bookId}/{authorId}")
    public ResponseEntity<BookAuthorDto> getBookAuthorById(@PathVariable Integer bookId, @PathVariable Integer authorId) {
        BookAuthorDto bookAuthor = bookAuthorService.getBookAuthorById(bookId, authorId);
        return ResponseEntity.ok(bookAuthor);
    }

    @GetMapping
    public ResponseEntity<List<BookAuthorDto>> getAllBookAuthors() {
        List<BookAuthorDto> bookAuthors = bookAuthorService.getAllBookAuthors();
        return ResponseEntity.ok(bookAuthors);
    }

    @DeleteMapping("/{bookId}/{authorId}")
    public ResponseEntity<Void> deleteBookAuthor(@PathVariable Integer bookId, @PathVariable Integer authorId) {
        bookAuthorService.deleteBookAuthor(bookId, authorId);
        return ResponseEntity.noContent().build();
    }
}
