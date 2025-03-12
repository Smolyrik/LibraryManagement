package com.library.service;

import com.library.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(CategoryDto categoryDto);

    CategoryDto getCategoryById(Integer categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto updateCategory(Integer categoryId, CategoryDto categoryDto);

    void deleteCategory(Integer categoryId);
}
