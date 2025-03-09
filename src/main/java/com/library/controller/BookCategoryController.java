package com.library.controller;

import com.library.dto.BookCategoryDto;
import com.library.service.BookCategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-categories")
@AllArgsConstructor
public class BookCategoryController {

    private final BookCategoryService bookCategoryService;

    @PostMapping
    public ResponseEntity<BookCategoryDto> addBookCategory(@Valid @RequestBody BookCategoryDto bookCategoryDto) {
        BookCategoryDto savedBookCategory = bookCategoryService.addBookCategory(bookCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookCategory);
    }

    @GetMapping("/{bookId}/{categoryId}")
    public ResponseEntity<BookCategoryDto> getBookCategoryById(@PathVariable Integer bookId, @PathVariable Integer categoryId) {
        BookCategoryDto bookCategory = bookCategoryService.getBookCategoryById(bookId, categoryId);
        return ResponseEntity.ok(bookCategory);
    }

    @GetMapping
    public ResponseEntity<List<BookCategoryDto>> getAllBookCategories() {
        List<BookCategoryDto> bookCategories = bookCategoryService.getAllBookCategories();
        return ResponseEntity.ok(bookCategories);
    }

    @DeleteMapping("/{bookId}/{categoryId}")
    public ResponseEntity<Void> deleteBookCategory(@PathVariable Integer bookId, @PathVariable Integer categoryId) {
        bookCategoryService.deleteBookCategory(bookId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
