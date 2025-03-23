package com.library.controller;

import com.library.dto.BookDto;
import com.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookServiceImpl bookService;

    @InjectMocks
    private BookController bookController;

    private BookDto bookDto;
    private Integer bookId;

    @BeforeEach
    void setUp() {
        bookId = 1;
        bookDto = BookDto.builder()
                .bookId(bookId)
                .title("Test Book")
                .description("This is a test book")
                .availableCopies(5)
                .totalCopies(10)
                .build();
    }

    @Test
    void addBook_ShouldReturnBook() {
        when(bookService.addBook(any(BookDto.class))).thenReturn(bookDto);
        ResponseEntity<BookDto> response = bookController.addBook(bookDto);

        assertNotNull(response.getBody());
        assertEquals(bookDto.getBookId(), response.getBody().getBookId());
        verify(bookService, times(1)).addBook(any(BookDto.class));
    }

    @Test
    void getBookById_ShouldReturnBook() {
        when(bookService.getBookById(bookId)).thenReturn(bookDto);
        ResponseEntity<BookDto> response = bookController.getBookById(bookId);

        assertNotNull(response.getBody());
        assertEquals(bookId, response.getBody().getBookId());
        verify(bookService, times(1)).getBookById(bookId);
    }

    @Test
    void getAllBooks_ShouldReturnBookList() {
        when(bookService.getAllBooks()).thenReturn(Collections.singletonList(bookDto));
        ResponseEntity<List<BookDto>> response = bookController.getAllBooks();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void updateBook_ShouldReturnUpdatedBook() {
        when(bookService.updateBook(eq(bookId), any(BookDto.class))).thenReturn(bookDto);
        ResponseEntity<BookDto> response = bookController.updateBook(bookId, bookDto);

        assertNotNull(response.getBody());
        assertEquals(bookId, response.getBody().getBookId());
        verify(bookService, times(1)).updateBook(eq(bookId), any(BookDto.class));
    }

    @Test
    void deleteBook_ShouldReturnNoContent() {
        doNothing().when(bookService).deleteBook(bookId);
        ResponseEntity<Void> response = bookController.deleteBook(bookId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookService, times(1)).deleteBook(bookId);
    }

    @Test
    void findBooksByTitle_ShouldReturnMatchingBooks() {
        String title = "Test Book";
        when(bookService.findBooksByTitle(title)).thenReturn(Collections.singletonList(bookDto));
        ResponseEntity<List<BookDto>> response = bookController.findBooksByTitle(title);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(title, response.getBody().get(0).getTitle());
        verify(bookService, times(1)).findBooksByTitle(title);
    }

    @Test
    void findBooksByAvailableCopiesRange_ShouldReturnMatchingBooks() {
        int min = 2, max = 6;
        when(bookService.findBooksByAvailableCopiesRange(min, max)).thenReturn(Collections.singletonList(bookDto));
        ResponseEntity<List<BookDto>> response = bookController.findBooksByAvailableCopiesRange(min, max);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertTrue(response.getBody().get(0).getAvailableCopies() >= min
                && response.getBody().get(0).getAvailableCopies() <= max);
        verify(bookService, times(1)).findBooksByAvailableCopiesRange(min, max);
    }

    @Test
    void findBooksByAuthor_ShouldReturnMatchingBooks() {
        String authorName = "Joshua Bloch";
        when(bookService.findBooksByAuthor(authorName)).thenReturn(Collections.singletonList(bookDto));
        ResponseEntity<List<BookDto>> response = bookController.findBooksByAuthor(authorName);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(bookService, times(1)).findBooksByAuthor(authorName);
    }

    @Test
    void findBooksByCategory_ShouldReturnMatchingBooks() {
        String categoryName = "Programming";
        when(bookService.findBooksByCategory(categoryName)).thenReturn(Collections.singletonList(bookDto));
        ResponseEntity<List<BookDto>> response = bookController.findBooksByCategory(categoryName);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(bookService, times(1)).findBooksByCategory(categoryName);
    }
}
