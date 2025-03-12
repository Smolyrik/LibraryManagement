package com.library.controller;

import com.library.dto.BookDto;
import com.library.service.impl.BookServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookServiceImpl bookService;

    @PostMapping
    public ResponseEntity<BookDto> addBook(@Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.addBook(bookDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Integer id, @Valid @RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<BookDto>> findBooksByTitle(@RequestParam String title) {
        return ResponseEntity.ok(bookService.findBooksByTitle(title));
    }

    @GetMapping("/search/available")
    public ResponseEntity<List<BookDto>> findBooksByAvailableCopiesRange(@RequestParam Integer min, @RequestParam Integer max) {
        return ResponseEntity.ok(bookService.findBooksByAvailableCopiesRange(min, max));
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<BookDto>> findBooksByAuthor(@RequestParam String authorName) {
        return ResponseEntity.ok(bookService.findBooksByAuthor(authorName));
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<BookDto>> findBooksByCategory(@RequestParam String categoryName) {
        return ResponseEntity.ok(bookService.findBooksByCategory(categoryName));
    }
}
