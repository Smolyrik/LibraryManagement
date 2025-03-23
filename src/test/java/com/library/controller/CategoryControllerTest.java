package com.library.controller;

import com.library.dto.CategoryDto;
import com.library.service.impl.CategoryServiceImpl;
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
class CategoryControllerTest {

    @Mock
    private CategoryServiceImpl categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryDto categoryDto;
    private Integer categoryId;

    @BeforeEach
    void setUp() {
        categoryId = 1;
        categoryDto = CategoryDto.builder()
                .categoryId(categoryId)
                .categoryName("Fiction")
                .description("Books related to fiction genre")
                .build();
    }

    @Test
    void addCategory_ShouldReturnCategory() {
        when(categoryService.addCategory(any(CategoryDto.class))).thenReturn(categoryDto);
        ResponseEntity<CategoryDto> response = categoryController.addCategory(categoryDto);

        assertNotNull(response.getBody());
        assertEquals(categoryDto.getCategoryId(), response.getBody().getCategoryId());
        verify(categoryService, times(1)).addCategory(any(CategoryDto.class));
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        when(categoryService.getCategoryById(categoryId)).thenReturn(categoryDto);
        ResponseEntity<CategoryDto> response = categoryController.getCategoryById(categoryId);

        assertNotNull(response.getBody());
        assertEquals(categoryId, response.getBody().getCategoryId());
        verify(categoryService, times(1)).getCategoryById(categoryId);
    }

    @Test
    void getAllCategories_ShouldReturnCategoryList() {
        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(categoryDto));
        ResponseEntity<List<CategoryDto>> response = categoryController.getAllCategories();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() {
        when(categoryService.updateCategory(eq(categoryId), any(CategoryDto.class))).thenReturn(categoryDto);
        ResponseEntity<CategoryDto> response = categoryController.updateCategory(categoryId, categoryDto);

        assertNotNull(response.getBody());
        assertEquals(categoryId, response.getBody().getCategoryId());
        verify(categoryService, times(1)).updateCategory(eq(categoryId), any(CategoryDto.class));
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() {
        doNothing().when(categoryService).deleteCategory(categoryId);
        ResponseEntity<Void> response = categoryController.deleteCategory(categoryId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}
