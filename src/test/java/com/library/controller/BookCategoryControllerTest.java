package com.library.controller;

import com.library.dto.BookCategoryDto;
import com.library.service.impl.BookCategoryServiceImpl;
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
class BookCategoryControllerTest {

    @Mock
    private BookCategoryServiceImpl bookCategoryService;

    @InjectMocks
    private BookCategoryController bookCategoryController;

    private BookCategoryDto bookCategoryDto;
    private Integer bookId;
    private Integer categoryId;

    @BeforeEach
    void setUp() {
        bookId = 1;
        categoryId = 1;
        bookCategoryDto = BookCategoryDto.builder()
                .bookId(bookId)
                .categoryId(categoryId)
                .build();
    }

    @Test
    void addBookCategory_ShouldReturnBookCategory() {
        when(bookCategoryService.addBookCategory(any(BookCategoryDto.class))).thenReturn(bookCategoryDto);
        ResponseEntity<BookCategoryDto> response = bookCategoryController.addBookCategory(bookCategoryDto);

        assertNotNull(response.getBody());
        assertEquals(bookCategoryDto.getBookId(), response.getBody().getBookId());
        verify(bookCategoryService, times(1)).addBookCategory(any(BookCategoryDto.class));
    }

    @Test
    void getBookCategoryById_ShouldReturnBookCategory() {
        when(bookCategoryService.getBookCategoryById(bookId, categoryId)).thenReturn(bookCategoryDto);
        ResponseEntity<BookCategoryDto> response = bookCategoryController.getBookCategoryById(bookId, categoryId);

        assertNotNull(response.getBody());
        assertEquals(bookId, response.getBody().getBookId());
        verify(bookCategoryService, times(1)).getBookCategoryById(bookId, categoryId);
    }

    @Test
    void getAllBookCategories_ShouldReturnBookCategoryList() {
        when(bookCategoryService.getAllBookCategories()).thenReturn(Collections.singletonList(bookCategoryDto));
        ResponseEntity<List<BookCategoryDto>> response = bookCategoryController.getAllBookCategories();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(bookCategoryService, times(1)).getAllBookCategories();
    }

    @Test
    void deleteBookCategory_ShouldReturnNoContent() {
        doNothing().when(bookCategoryService).deleteBookCategory(bookId, categoryId);
        ResponseEntity<Void> response = bookCategoryController.deleteBookCategory(bookId, categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(bookCategoryService, times(1)).deleteBookCategory(bookId, categoryId);
    }
}
