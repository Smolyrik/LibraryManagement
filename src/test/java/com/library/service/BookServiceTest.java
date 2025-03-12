package com.library.service;

import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.mapper.BookMapper;
import com.library.repository.BookRepository;
import com.library.service.impl.BookServiceImpl;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        book = new Book(1, "Test Book", "Test Description", 5, 10);
        bookDto = new BookDto(1, "Test Book", "Test Description", 5, 10);
    }

    @Test
    void addBook_ShouldReturnSavedBookDto() {
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBook = bookService.addBook(bookDto);

        assertNotNull(savedBook);
        assertEquals(bookDto.getBookId(), savedBook.getBookId());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBookDto() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto foundBook = bookService.getBookById(1);

        assertNotNull(foundBook);
        assertEquals(bookDto.getBookId(), foundBook.getBookId());
    }

    @Test
    void getBookById_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookService.getBookById(1));
    }

    @Test
    void getAllBooks_ShouldReturnListOfBookDtos() {
        List<Book> books = Collections.singletonList(book);

        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        List<BookDto> foundBooks = bookService.getAllBooks();

        assertEquals(1, foundBooks.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBookDto() {
        when(bookRepository.existsById(1)).thenReturn(true);
        when(bookMapper.toEntity(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto updatedBook = bookService.updateBook(1, bookDto);

        assertNotNull(updatedBook);
        assertEquals(bookDto.getBookId(), updatedBook.getBookId());
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> bookService.updateBook(1, bookDto));
    }

    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        when(bookRepository.existsById(1)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1);

        assertDoesNotThrow(() -> bookService.deleteBook(1));
        verify(bookRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> bookService.deleteBook(1));
    }
}
