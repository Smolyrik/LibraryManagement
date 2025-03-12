package com.library.service.impl;

import com.library.dto.BookAuthorDto;
import com.library.entity.Book;
import com.library.entity.BookAuthor;
import com.library.entity.BookAuthorId;
import com.library.entity.Author;
import com.library.mapper.BookAuthorMapper;
import com.library.repository.BookAuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.AuthorRepository;
import com.library.service.BookAuthorService;
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
public class BookAuthorServiceImpl implements BookAuthorService {

    private final BookAuthorRepository bookAuthorRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookAuthorMapper bookAuthorMapper;

    @Transactional
    public BookAuthorDto addBookAuthor(BookAuthorDto bookAuthorDto) {
        Book book = bookRepository.findById(bookAuthorDto.getBookId())
                .orElseThrow(() -> new NoSuchElementException("Book not found with ID: " + bookAuthorDto.getBookId()));

        Author author = authorRepository.findById(bookAuthorDto.getAuthorId())
                .orElseThrow(() -> new NoSuchElementException("Author not found with ID: " + bookAuthorDto.getAuthorId()));

        BookAuthor bookAuthor = BookAuthor.builder()
                .id(new BookAuthorId(book.getBookId(), author.getAuthorId()))
                .book(book)
                .author(author)
                .build();

        BookAuthor savedBookAuthor = bookAuthorRepository.save(bookAuthor);
        log.info("Added book-author relation: Book ID = {}, Author ID = {}", book.getBookId(), author.getAuthorId());

        return bookAuthorMapper.toDto(savedBookAuthor);
    }

    public BookAuthorDto getBookAuthorById(Integer bookId, Integer authorId) {
        BookAuthorId id = new BookAuthorId(bookId, authorId);
        return bookAuthorRepository.findById(id)
                .map(bookAuthorMapper::toDto)
                .orElseThrow(() -> {
                    log.error("BookAuthor relation not found: Book ID = {}, Author ID = {}", bookId, authorId);
                    return new NoSuchElementException("BookAuthor relation not found");
                });
    }

    public List<BookAuthorDto> getAllBookAuthors() {
        log.info("Fetching all book-author relations");
        return bookAuthorRepository.findAll().stream()
                .map(bookAuthorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBookAuthor(Integer bookId, Integer authorId) {
        BookAuthorId id = new BookAuthorId(bookId, authorId);
        if (!bookAuthorRepository.existsById(id)) {
            log.error("BookAuthor relation not found: Book ID = {}, Author ID = {}", bookId, authorId);
            throw new NoSuchElementException("BookAuthor relation not found");
        }
        bookAuthorRepository.deleteById(id);
        log.info("Deleted book-author relation: Book ID = {}, Author ID = {}", bookId, authorId);
    }
}
