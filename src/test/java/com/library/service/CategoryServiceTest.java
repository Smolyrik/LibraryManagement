package com.library.service;

import com.library.dto.CategoryDto;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.repository.CategoryRepository;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        category = new Category(1, "Fiction", "Fictional books");
        categoryDto = new CategoryDto(1, "Fiction", "Fictional books");
    }

    @Test
    void addCategory_ShouldReturnSavedCategoryDto() {
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto savedCategory = categoryService.addCategory(categoryDto);

        assertNotNull(savedCategory);
        assertEquals(categoryDto.getCategoryId(), savedCategory.getCategoryId());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategoryDto() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto foundCategory = categoryService.getCategoryById(1);

        assertNotNull(foundCategory);
        assertEquals(categoryDto.getCategoryId(), foundCategory.getCategoryId());
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> categoryService.getCategoryById(1));
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategoryDtos() {
        List<Category> categories = Collections.singletonList(category);

        when(categoryRepository.findAll()).thenReturn(categories);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        List<CategoryDto> foundCategories = categoryService.getAllCategories();

        assertEquals(1, foundCategories.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldReturnUpdatedCategoryDto() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto updatedCategory = categoryService.updateCategory(1, categoryDto);

        assertNotNull(updatedCategory);
        assertEquals(categoryDto.getCategoryId(), updatedCategory.getCategoryId());
    }

    @Test
    void updateCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> categoryService.updateCategory(1, categoryDto));
    }

    @Test
    void deleteCategory_WhenCategoryExists_ShouldDeleteCategory() {
        when(categoryRepository.existsById(1)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1);

        assertDoesNotThrow(() -> categoryService.deleteCategory(1));
        verify(categoryRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteCategory_WhenCategoryDoesNotExist_ShouldThrowException() {
        when(categoryRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> categoryService.deleteCategory(1));
    }
}
