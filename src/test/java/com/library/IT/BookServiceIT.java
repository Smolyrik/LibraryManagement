package com.library.IT;

import com.library.dto.*;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.mapper.AuthorMapper;
import com.library.mapper.BookMapper;
import com.library.mapper.CategoryMapper;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.service.*;
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
public class BookServiceIT {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookAuthorService bookAuthorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BookCategoryService bookCategoryService;

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
        bookRepository.deleteAll();
        bookRepository.flush();
        categoryRepository.deleteAll();
        categoryRepository.flush();
    }

    private CategoryDto addCategory() {
        Category category = Category.builder()
                .categoryName("Test Category")
                .description("testDescription")
                .build();

        return categoryService.addCategory(categoryMapper.toDto(category));
    }

    private AuthorDto addAuthor() {
        Author author = Author.builder()
                .firstName("Test Author")
                .lastName("test")
                .biography("testBiography")
                .build();
        return authorService.addAuthor(authorMapper.toDto(author));
    }

    private Book createBook(String title) {
        return Book.builder()
                .title(title)
                .description("Test Description")
                .availableCopies(5)
                .totalCopies(10)
                .build();
    }

    @Test
    void addBook_ShouldSaveAndReturnBookDto() {
        Book testBook = createBook("Test Book");

        BookDto savedBook = bookService.addBook(bookMapper.toDto(testBook));

        assertNotNull(savedBook);
        assertNotNull(savedBook.getBookId());
        assertEquals(savedBook.getTitle(), testBook.getTitle());
        assertEquals(savedBook.getDescription(), testBook.getDescription());
        assertEquals(savedBook.getAvailableCopies(), testBook.getAvailableCopies());
        assertEquals(savedBook.getTotalCopies(), testBook.getTotalCopies());
    }

    @Test
    void getBookById_ShouldReturnBookDto() {
        Book testBook = createBook("Test Book");

        BookDto savedBook = bookService.addBook(bookMapper.toDto(testBook));
        BookDto foundBook = bookService.getBookById(savedBook.getBookId());

        assertNotNull(foundBook);
        assertEquals(foundBook.getBookId(), savedBook.getBookId());
        assertEquals(foundBook.getTitle(), savedBook.getTitle());
        assertEquals(foundBook.getDescription(), savedBook.getDescription());
    }

    @Test
    void getAllBooks_ShouldReturnListOfBookDto() {
        Book testBook1 = createBook("Test Book 1");
        bookService.addBook(bookMapper.toDto(testBook1));

        Book testBook2 = createBook("Test Book 2");
        bookService.addBook(bookMapper.toDto(testBook2));

        List<BookDto> foundBooks = bookService.getAllBooks();

        assertEquals(2, foundBooks.size());
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBookDto() {
        Book testBook = createBook("Test Book");
        BookDto savedBook = bookService.addBook(bookMapper.toDto(testBook));
        savedBook.setDescription("Updated Description");

        BookDto updatedBook = bookService.updateBook(savedBook.getBookId(), savedBook);

        assertNotNull(updatedBook);
        assertEquals(updatedBook.getDescription(), savedBook.getDescription());
    }

    @Test
    void deleteBook_ShouldDeleteBook() {
        Book testBook = createBook("Test Book");
        BookDto savedBook = bookService.addBook(bookMapper.toDto(testBook));
        bookService.deleteBook(savedBook.getBookId());

        assertThrows(NoSuchElementException.class, () -> bookService.getBookById(savedBook.getBookId()));
    }

    @Test
    void findBooksByAvailableCopiesRange_ShouldReturnBooksInGivenRange() {
        Book book1 = Book.builder().title("Book 1").description("Desc 1").availableCopies(5).totalCopies(10).build();
        Book book2 = Book.builder().title("Book 2").description("Desc 2").availableCopies(3).totalCopies(10).build();
        Book book3 = Book.builder().title("Book 3").description("Desc 3").availableCopies(12).totalCopies(12).build();
        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);

        List<BookDto> books = bookService.findBooksByAvailableCopiesRange(5, 15);
        assertFalse(books.isEmpty());
        assertEquals(2, books.size());
    }

    @Test
    void findBooksByAuthor_ShouldReturnBooksByAuthor() {
        Book book1 = createBook("Test Book 1");
        Book book2 = createBook("Test Book 2");
        BookDto savedBook1 = bookService.addBook(bookMapper.toDto(book1));
        BookDto savedBook2 = bookService.addBook(bookMapper.toDto(book2));

        AuthorDto testAuthor = addAuthor();

        BookAuthorDto testBookAuthor1 = new BookAuthorDto(savedBook1.getBookId(), testAuthor.getAuthorId());
        bookAuthorService.addBookAuthor(testBookAuthor1);

        List<BookDto> books = bookService.findBooksByAuthor("Test Author");
        assertEquals(1, books.size());

        BookAuthorDto testBookAuthor2 = new BookAuthorDto(savedBook2.getBookId(), testAuthor.getAuthorId());
        bookAuthorService.addBookAuthor(testBookAuthor2);

        books = bookService.findBooksByAuthor("Test Author");
        assertEquals(2, books.size());
    }

    @Test
    void findBooksByCategory_ShouldReturnBooksByCategory() {
        Book book1 = createBook("Test Book 1");
        Book book2 = createBook("Test Book 2");
        BookDto savedBook1 = bookService.addBook(bookMapper.toDto(book1));
        BookDto savedBook2 = bookService.addBook(bookMapper.toDto(book2));

        CategoryDto testCategory = addCategory();

        BookCategoryDto testBookCategory1 = new BookCategoryDto(savedBook1.getBookId(), testCategory.getCategoryId());
        bookCategoryService.addBookCategory(testBookCategory1);

        List<BookDto> books = bookService.findBooksByCategory("Test Category");
        assertEquals(1, books.size());

        BookCategoryDto testBookCategory2 = new BookCategoryDto(savedBook2.getBookId(), testCategory.getCategoryId());
        bookCategoryService.addBookCategory(testBookCategory2);

        books = bookService.findBooksByCategory("Test Category");
        assertEquals(2, books.size());
    }
}
