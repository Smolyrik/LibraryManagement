package com.library.service;

import com.library.dto.BookAuthorDto;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.BookAuthor;
import com.library.entity.BookAuthorId;
import com.library.mapper.BookAuthorMapper;
import com.library.repository.AuthorRepository;
import com.library.repository.BookAuthorRepository;
import com.library.repository.BookRepository;
import com.library.service.impl.BookAuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookAuthorServiceTest {

    @Mock
    private BookAuthorRepository bookAuthorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookAuthorMapper bookAuthorMapper;

    @InjectMocks
    private BookAuthorServiceImpl bookAuthorService;

    private Book book;
    private Author author;
    private BookAuthor bookAuthor;
    private BookAuthorDto bookAuthorDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setBookId(1);

        author = new Author();
        author.setAuthorId(1);

        bookAuthor = new BookAuthor(new BookAuthorId(1, 1), book, author);
        bookAuthorDto = new BookAuthorDto(1, 1);
    }

    @Test
    void testAddBookAuthor() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        when(bookAuthorRepository.save(any(BookAuthor.class))).thenReturn(bookAuthor);
        when(bookAuthorMapper.toDto(bookAuthor)).thenReturn(bookAuthorDto);

        BookAuthorDto result = bookAuthorService.addBookAuthor(bookAuthorDto);

        assertNotNull(result);
        assertEquals(1, result.getBookId());
        assertEquals(1, result.getAuthorId());
        verify(bookAuthorRepository, times(1)).save(any(BookAuthor.class));
    }

    @Test
    void testAddBookAuthor_BookNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookAuthorService.addBookAuthor(bookAuthorDto));
    }

    @Test
    void testAddBookAuthor_AuthorNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookAuthorService.addBookAuthor(bookAuthorDto));
    }

    @Test
    void testGetBookAuthorById() {
        when(bookAuthorRepository.findById(any(BookAuthorId.class))).thenReturn(Optional.of(bookAuthor));
        when(bookAuthorMapper.toDto(bookAuthor)).thenReturn(bookAuthorDto);

        BookAuthorDto result = bookAuthorService.getBookAuthorById(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getBookId());
        assertEquals(1, result.getAuthorId());
    }

    @Test
    void testGetBookAuthorById_NotFound() {
        when(bookAuthorRepository.findById(any(BookAuthorId.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookAuthorService.getBookAuthorById(1, 1));
    }

    @Test
    void testGetAllBookAuthors() {
        List<BookAuthor> bookAuthors = Collections.singletonList(bookAuthor);

        when(bookAuthorRepository.findAll()).thenReturn(bookAuthors);
        when(bookAuthorMapper.toDto(bookAuthor)).thenReturn(bookAuthorDto);

        List<BookAuthorDto> result = bookAuthorService.getAllBookAuthors();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteBookAuthor() {
        when(bookAuthorRepository.existsById(any(BookAuthorId.class))).thenReturn(true);

        bookAuthorService.deleteBookAuthor(1, 1);

        verify(bookAuthorRepository, times(1)).deleteById(any(BookAuthorId.class));
    }

    @Test
    void testDeleteBookAuthor_NotFound() {
        when(bookAuthorRepository.existsById(any(BookAuthorId.class))).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> bookAuthorService.deleteBookAuthor(1, 1));
    }
}
