package com.library.service.impl;

import com.library.dto.CategoryDto;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.repository.CategoryRepository;
import com.library.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        log.info("Added new category with ID: {}", savedCategory.getCategoryId());
        return categoryMapper.toDto(savedCategory);
    }

    public CategoryDto getCategoryById(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Category with ID: {} not found", categoryId);
                    return new NoSuchElementException("Category with ID: " + categoryId + " not found");
                });
    }

    public List<CategoryDto> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto updateCategory(Integer categoryId, CategoryDto categoryDto) {
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Category with ID: {} not found", categoryId);
            throw new NoSuchElementException("Category with ID: " + categoryId + " not found");
        }

        Category updatedCategory = categoryMapper.toEntity(categoryDto);
        updatedCategory.setCategoryId(categoryId);

        Category savedCategory = categoryRepository.save(updatedCategory);
        log.info("Updated category with ID: {}", savedCategory.getCategoryId());

        return categoryMapper.toDto(savedCategory);
    }

    @Transactional
    public void deleteCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            log.error("Category with ID: {} not found", categoryId);
            throw new NoSuchElementException("Category with ID: " + categoryId + " not found");
        }
        categoryRepository.deleteById(categoryId);
        log.info("Deleted category with ID: {}", categoryId);
    }
}
