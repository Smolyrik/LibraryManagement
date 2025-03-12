package com.library.service.impl;

import com.library.dto.BookCategoryDto;
import com.library.entity.Book;
import com.library.entity.BookCategory;
import com.library.entity.BookCategoryId;
import com.library.entity.Category;
import com.library.mapper.BookCategoryMapper;
import com.library.repository.BookCategoryRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.service.BookCategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCategoryMapper bookCategoryMapper;

    @Transactional
    public BookCategoryDto addBookCategory(BookCategoryDto bookCategoryDto) {
        Book book = bookRepository.findById(bookCategoryDto.getBookId())
                .orElseThrow(() -> new NoSuchElementException("Book not found with ID: " + bookCategoryDto.getBookId()));

        Category category = categoryRepository.findById(bookCategoryDto.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("Category not found with ID: " + bookCategoryDto.getCategoryId()));

        BookCategory bookCategory = BookCategory.builder()
                .id(new BookCategoryId(book.getBookId(), category.getCategoryId()))
                .book(book)
                .category(category)
                .build();

        BookCategory savedBookCategory = bookCategoryRepository.save(bookCategory);
        log.info("Added book-category relation: Book ID = {}, Category ID = {}", book.getBookId(), category.getCategoryId());

        return bookCategoryMapper.toDto(savedBookCategory);
    }

    public BookCategoryDto getBookCategoryById(Integer bookId, Integer categoryId) {
        BookCategoryId id = new BookCategoryId(bookId, categoryId);
        return bookCategoryRepository.findById(id)
                .map(bookCategoryMapper::toDto)
                .orElseThrow(() -> {
                    log.error("BookCategory relation not found: Book ID = {}, Category ID = {}", bookId, categoryId);
                    return new NoSuchElementException("BookCategory relation not found");
                });
    }

    public List<BookCategoryDto> getAllBookCategories() {
        log.info("Fetching all book-category relations");
        return bookCategoryRepository.findAll().stream()
                .map(bookCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBookCategory(Integer bookId, Integer categoryId) {
        BookCategoryId id = new BookCategoryId(bookId, categoryId);
        if (!bookCategoryRepository.existsById(id)) {
            log.error("BookCategory relation not found: Book ID = {}, Category ID = {}", bookId, categoryId);
            throw new NoSuchElementException("BookCategory relation not found");
        }
        bookCategoryRepository.deleteById(id);
        log.info("Deleted book-category relation: Book ID = {}, Category ID = {}", bookId, categoryId);
    }
}
