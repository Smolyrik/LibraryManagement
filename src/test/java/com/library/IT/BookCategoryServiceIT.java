package com.library.IT;

import com.library.dto.BookCategoryDto;
import com.library.dto.BookDto;
import com.library.dto.CategoryDto;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.mapper.BookMapper;
import com.library.mapper.CategoryMapper;
import com.library.repository.BookCategoryRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.service.BookCategoryService;
import com.library.service.BookService;
import com.library.service.CategoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
public class BookCategoryServiceIT {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookCategoryService bookCategoryService;
    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    private CategoryDto createTestCategory() {
        Category category = Category.builder()
                .categoryName("Test Category")
                .description("testDescription")
                .build();

        return categoryService.addCategory(categoryMapper.toDto(category));
    }

    private BookDto createTestBook(String title) {
        Book book = Book.builder()
                .title(title)
                .description("Test Description")
                .availableCopies(5)
                .totalCopies(10)
                .build();
        return bookService.addBook(bookMapper.toDto(book));
    }

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

    @AfterEach
    public void cleanUp() {
        bookCategoryRepository.deleteAll();
        bookCategoryRepository.flush();
        bookRepository.deleteAll();
        bookRepository.flush();
        categoryRepository.deleteAll();
        categoryRepository.flush();
    }

    @Test
    void addBookCategory_ShouldSaveAndReturnBookCategoryDto() {
        BookDto book = createTestBook("Test Book");
        CategoryDto category = createTestCategory();

        BookCategoryDto bookCategoryDto = new BookCategoryDto(book.getBookId(), category.getCategoryId());
        BookCategoryDto savedBookCategory = bookCategoryService.addBookCategory(bookCategoryDto);

        assertNotNull(savedBookCategory);
        assertEquals(book.getBookId(), savedBookCategory.getBookId());
        assertEquals(category.getCategoryId(), savedBookCategory.getCategoryId());
    }

    @Test
    void getBookCategoryById_ShouldReturnBookCategoryDto() {
        BookDto book = createTestBook("Test Book");
        CategoryDto category = createTestCategory();
        BookCategoryDto savedBookCategory = bookCategoryService.addBookCategory(new BookCategoryDto(book.getBookId(), category.getCategoryId()));

        BookCategoryDto foundBookCategory = bookCategoryService.getBookCategoryById(book.getBookId(), category.getCategoryId());

        assertNotNull(foundBookCategory);
        assertEquals(savedBookCategory.getBookId(), foundBookCategory.getBookId());
        assertEquals(savedBookCategory.getCategoryId(), foundBookCategory.getCategoryId());
    }

    @Test
    void getAllBookCategories_ShouldReturnListOfBookCategoryDto() {
        BookDto book1 = createTestBook("Book1");
        CategoryDto category1 = createTestCategory();
        bookCategoryService.addBookCategory(new BookCategoryDto(book1.getBookId(), category1.getCategoryId()));

        BookDto book2 = createTestBook("Book2");
        bookCategoryService.addBookCategory(new BookCategoryDto(book2.getBookId(), category1.getCategoryId()));

        List<BookCategoryDto> bookCategories = bookCategoryService.getAllBookCategories();
        assertTrue(bookCategories.size() >= 2);
    }

    @Test
    void deleteBookCategory_ShouldRemoveBookCategory() {
        BookDto book = createTestBook("Test Book");
        CategoryDto category = createTestCategory();
        bookCategoryService.addBookCategory(new BookCategoryDto(book.getBookId(), category.getCategoryId()));

        bookCategoryService.deleteBookCategory(book.getBookId(), category.getCategoryId());

        assertThrows(NoSuchElementException.class, () -> bookCategoryService.getBookCategoryById(book.getBookId(), category.getCategoryId()));
    }

    @Test
    void updateBookCategory_ShouldUpdateAndReturnBookCategoryDto() {
        BookDto book = createTestBook("Test Book");
        CategoryDto category = createTestCategory();
        bookCategoryService.addBookCategory(new BookCategoryDto(book.getBookId(), category.getCategoryId()));

        CategoryDto newCategory = categoryService.addCategory(new CategoryDto(null, "Updated Category", "Updated Description"));
        BookCategoryDto updatedBookCategory = new BookCategoryDto(book.getBookId(), newCategory.getCategoryId());
        BookCategoryDto result = bookCategoryService.addBookCategory(updatedBookCategory);

        assertNotNull(result);
        assertEquals(book.getBookId(), result.getBookId());
        assertEquals(newCategory.getCategoryId(), result.getCategoryId());
    }
}
