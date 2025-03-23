package com.library.IT.service;

import com.library.dto.AuthorDto;
import com.library.dto.BookAuthorDto;
import com.library.dto.BookDto;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.mapper.AuthorMapper;
import com.library.mapper.BookMapper;
import com.library.repository.BookAuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.AuthorRepository;
import com.library.service.impl.AuthorServiceImpl;
import com.library.service.impl.BookAuthorServiceImpl;
import com.library.service.impl.BookServiceImpl;
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
public class BookAuthorServiceIT {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private AuthorServiceImpl authorService;
    @Autowired
    private AuthorMapper authorMapper;
    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookAuthorServiceImpl bookAuthorService;
    @Autowired
    private BookAuthorRepository bookAuthorRepository;

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

    private AuthorDto createTestAuthor() {
        Author author = Author.builder()
                .firstName("Test Author")
                .lastName("test")
                .biography("testBiography")
                .build();
        return authorService.addAuthor(authorMapper.toDto(author));
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

    @AfterEach
    public void cleanUp() {
        bookAuthorRepository.deleteAll();
        bookAuthorRepository.flush();
        bookRepository.deleteAll();
        bookRepository.flush();
        authorRepository.deleteAll();
        authorRepository.flush();
    }

    @Test
    void addBookAuthor_ShouldSaveAndReturnBookAuthorDto() {
        BookDto book = createTestBook("Test Book");
        AuthorDto author = createTestAuthor();

        BookAuthorDto bookAuthorDto = new BookAuthorDto(book.getBookId(), author.getAuthorId());
        BookAuthorDto savedBookAuthor = bookAuthorService.addBookAuthor(bookAuthorDto);

        assertNotNull(savedBookAuthor);
        assertEquals(book.getBookId(), savedBookAuthor.getBookId());
        assertEquals(author.getAuthorId(), savedBookAuthor.getAuthorId());
    }

    @Test
    void getBookAuthorById_ShouldReturnBookAuthorDto() {
        BookDto book = createTestBook("Test Book");
        AuthorDto author = createTestAuthor();
        BookAuthorDto savedBookAuthor = bookAuthorService.addBookAuthor(new BookAuthorDto(book.getBookId(), author.getAuthorId()));

        BookAuthorDto foundBookAuthor = bookAuthorService.getBookAuthorById(book.getBookId(), author.getAuthorId());

        assertNotNull(foundBookAuthor);
        assertEquals(savedBookAuthor.getBookId(), foundBookAuthor.getBookId());
        assertEquals(savedBookAuthor.getAuthorId(), foundBookAuthor.getAuthorId());
    }

    @Test
    void getAllBookAuthors_ShouldReturnListOfBookAuthorDto() {
        BookDto book1 = createTestBook("Book1");
        AuthorDto author1 = createTestAuthor();
        bookAuthorService.addBookAuthor(new BookAuthorDto(book1.getBookId(), author1.getAuthorId()));

        BookDto book2 = createTestBook("Book2");
        bookAuthorService.addBookAuthor(new BookAuthorDto(book2.getBookId(), author1.getAuthorId()));

        List<BookAuthorDto> bookAuthors = bookAuthorService.getAllBookAuthors();
        assertTrue(bookAuthors.size() >= 2);
    }

    @Test
    void deleteBookAuthor_ShouldRemoveBookAuthor() {
        BookDto book = createTestBook("Test Book");
        AuthorDto author = createTestAuthor();
        bookAuthorService.addBookAuthor(new BookAuthorDto(book.getBookId(), author.getAuthorId()));

        bookAuthorService.deleteBookAuthor(book.getBookId(), author.getAuthorId());

        assertThrows(NoSuchElementException.class, () -> bookAuthorService.getBookAuthorById(book.getBookId(), author.getAuthorId()));
    }

    @Test
    void updateBookAuthor_ShouldUpdateAndReturnBookAuthorDto() {
        BookDto book = createTestBook("Test Book");
        AuthorDto author = createTestAuthor();
        bookAuthorService.addBookAuthor(new BookAuthorDto(book.getBookId(), author.getAuthorId()));

        AuthorDto newAuthor = authorService.addAuthor(new AuthorDto(null, "Updated Author", "Updated LastName", "Updated Biography"));
        BookAuthorDto updatedBookAuthor = new BookAuthorDto(book.getBookId(), newAuthor.getAuthorId());
        BookAuthorDto result = bookAuthorService.addBookAuthor(updatedBookAuthor);

        assertNotNull(result);
        assertEquals(book.getBookId(), result.getBookId());
        assertEquals(newAuthor.getAuthorId(), result.getAuthorId());
    }
}
