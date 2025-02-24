package com.library.service;

import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.repository.BookRepository;
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
public class BookService {

    private BookRepository bookRepository;
    private BookMapper bookMapper;

    @Transactional
    public BookDto addBook(BookDto bookDto) {
        Book book = bookMapper.toEntity(bookDto);
        Book savedBook = bookRepository.save(book);
        log.info("Added new book with ID: {}", savedBook.getBookId());
        return bookMapper.toDto(savedBook);
    }

    public BookDto getBookById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Book with ID: {} not found", bookId);
                    return new NoSuchElementException("Book with ID: " + bookId + " not found");
                });
    }

    public List<BookDto> getAllBooks() {
        log.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookDto updateBook(Integer bookId, BookDto bookDto) {
        if (!bookRepository.existsById(bookId)) {
            log.error("Book with ID: {} not found", bookId);
            throw  new NoSuchElementException("Book with ID: " + bookId + " not found");
        }

        Book updatedBook = bookMapper.toEntity(bookDto);
        updatedBook.setBookId(bookId);

        Book savedBook = bookRepository.save(updatedBook);
        log.info("Updated book with ID: {}", savedBook.getBookId());

        return bookMapper.toDto(savedBook);
    }

    @Transactional
    public void deleteBook(Integer bookId) {
        if (!bookRepository.existsById(bookId)) {
            log.error("Book with ID: {} not found", bookId);
            throw new NoSuchElementException("Book with ID: " + bookId + " not found");
        }
        bookRepository.deleteById(bookId);
        log.info("Deleted book with ID: {}", bookId);
    }

    public List<BookDto> findBooksByTitle(String title) {
        log.info("Searching books by title: {}", title);
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> findBooksByAvailableCopiesRange(Integer min, Integer max) {
        log.info("Searching books with available copies between {} and {}", min, max);
        return bookRepository.findByAvailableCopiesBetween(min, max).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> findBooksByAuthor(String authorName) {
        log.info("Searching books by author: {}", authorName);
        return bookRepository.findByAuthor(authorName).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<BookDto> findBooksByCategory(String categoryName) {
        log.info("Searching books by category: {}", categoryName);
        return bookRepository.findByCategory(categoryName).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }
}
