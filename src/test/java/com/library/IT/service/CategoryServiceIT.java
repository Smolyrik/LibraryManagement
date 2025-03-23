package com.library.IT.service;

import com.library.dto.CategoryDto;
import com.library.entity.Category;
import com.library.mapper.CategoryMapper;
import com.library.repository.CategoryRepository;
import com.library.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryServiceIT {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @AfterAll
    static void teardown() {
        postgres.stop();
    }

    @AfterEach
    public void cleanUp() {
        categoryRepository.deleteAll();
        categoryRepository.flush();
    }

    @Test
    void addCategory_ShouldSaveAndReturnCategoryDto() {
        Category testCategory = Category.builder()
                .categoryName("Fiction")
                .description("Fictional books")
                .build();

        CategoryDto savedCategory = categoryService.addCategory(categoryMapper.toDto(testCategory));

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getCategoryId());
        assertEquals(savedCategory.getCategoryName(), testCategory.getCategoryName());
        assertEquals(savedCategory.getDescription(), testCategory.getDescription());
    }

    @Test
    void getCategoryById_ShouldReturnCategoryDto() {
        Category testCategory = Category.builder()
                .categoryName("Fiction")
                .description("Fictional books")
                .build();

        CategoryDto savedCategory = categoryService.addCategory(categoryMapper.toDto(testCategory));
        CategoryDto foundCategory = categoryService.getCategoryById(savedCategory.getCategoryId());

        assertNotNull(foundCategory);
        assertEquals(foundCategory.getCategoryId(), savedCategory.getCategoryId());
        assertEquals(foundCategory.getCategoryName(), savedCategory.getCategoryName());
        assertEquals(foundCategory.getDescription(), savedCategory.getDescription());
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategoryDto() {
        Category testCategory1 = Category.builder()
                .categoryName("Fiction")
                .description("Fictional books")
                .build();
        categoryService.addCategory(categoryMapper.toDto(testCategory1));

        Category testCategory2 = Category.builder()
                .categoryName("Non-Fiction")
                .description("Non-fictional books")
                .build();
        categoryService.addCategory(categoryMapper.toDto(testCategory2));

        List<CategoryDto> foundCategories = categoryService.getAllCategories();

        assertEquals(2, foundCategories.size());
    }

    @Test
    void updateCategory_ShouldUpdateAndReturnCategoryDto() {
        Category testCategory = Category.builder()
                .categoryName("Science")
                .description("Scientific books")
                .build();
        CategoryDto savedCategory = categoryService.addCategory(categoryMapper.toDto(testCategory));
        savedCategory.setCategoryName("Physics");
        savedCategory.setDescription("Physics books");

        CategoryDto updatedCategory = categoryService.updateCategory(savedCategory.getCategoryId(), savedCategory);

        assertNotNull(updatedCategory);
        assertEquals(updatedCategory.getCategoryName(), savedCategory.getCategoryName());
        assertEquals(updatedCategory.getDescription(), savedCategory.getDescription());
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() {
        Category testCategory = Category.builder()
                .categoryName("History")
                .description("Historical books")
                .build();
        CategoryDto savedCategory = categoryService.addCategory(categoryMapper.toDto(testCategory));
        categoryService.deleteCategory(savedCategory.getCategoryId());

        assertThrows(NoSuchElementException.class, () -> categoryService.getCategoryById(savedCategory.getCategoryId()));
    }
}
