package com.library.controller;

import com.library.dto.BookAuthorDto;
import com.library.service.impl.BookAuthorServiceImpl;
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
class BookAuthorControllerTest {

    @Mock
    private BookAuthorServiceImpl bookAuthorService;

    @InjectMocks
    private BookAuthorController bookAuthorController;

    private BookAuthorDto bookAuthorDto;

    @BeforeEach
    void setUp() {
        bookAuthorDto = BookAuthorDto.builder()
                .bookId(1)
                .authorId(1)
                .build();
    }

    @Test
    void addBookAuthor_ShouldReturnBookAuthor() {
        when(bookAuthorService.addBookAuthor(any(BookAuthorDto.class))).thenReturn(bookAuthorDto);
        ResponseEntity<BookAuthorDto> response = bookAuthorController.addBookAuthor(bookAuthorDto);

        assertNotNull(response.getBody());
        assertEquals(bookAuthorDto.getBookId(), response.getBody().getBookId());
        verify(bookAuthorService, times(1)).addBookAuthor(any(BookAuthorDto.class));
    }

    @Test
    void getBookAuthorById_ShouldReturnBookAuthor() {
        when(bookAuthorService.getBookAuthorById(1, 1)).thenReturn(bookAuthorDto);
        ResponseEntity<BookAuthorDto> response = bookAuthorController.getBookAuthorById(1, 1);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getBookId());
        assertEquals(1, response.getBody().getAuthorId());
        verify(bookAuthorService, times(1)).getBookAuthorById(1, 1);
    }

    @Test
    void getAllBookAuthors_ShouldReturnBookAuthorList() {
        when(bookAuthorService.getAllBookAuthors()).thenReturn(Collections.singletonList(bookAuthorDto));
        ResponseEntity<List<BookAuthorDto>> response = bookAuthorController.getAllBookAuthors();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(bookAuthorService, times(1)).getAllBookAuthors();
    }

    @Test
    void deleteBookAuthor_ShouldReturnNoContent() {
        doNothing().when(bookAuthorService).deleteBookAuthor(1, 1);
        ResponseEntity<Void> response = bookAuthorController.deleteBookAuthor(1, 1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookAuthorService, times(1)).deleteBookAuthor(1, 1);
    }
}
