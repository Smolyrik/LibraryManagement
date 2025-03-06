package com.library.IT;

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
import com.library.service.AuthorService;
import com.library.service.BookAuthorService;
import com.library.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
public class BookAuthorServiceIT {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private AuthorMapper authorMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookAuthorService bookAuthorService;
    @Autowired
    private BookAuthorRepository bookAuthorRepository;

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
        bookRepository.deleteAll();
        authorRepository.deleteAll();
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
