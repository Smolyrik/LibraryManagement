package com.library.IT;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
import com.library.service.AuthorService;
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
public class AuthorServiceIT {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    @AfterEach
    public void cleanUp() {
        authorRepository.deleteAll();
    }

    @Test
    void addAuthor_ShouldSaveAndReturnAuthorDto() {
        Author testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .biography("Famous author of fiction books.")
                .build();

        AuthorDto savedAuthor = authorService.addAuthor(authorMapper.toDto(testAuthor));

        assertNotNull(savedAuthor);
        assertNotNull(savedAuthor.getAuthorId());
        assertEquals(savedAuthor.getFirstName(), testAuthor.getFirstName());
        assertEquals(savedAuthor.getLastName(), testAuthor.getLastName());
        assertEquals(savedAuthor.getBiography(), testAuthor.getBiography());
    }

    @Test
    void getAuthorById_ShouldReturnAuthorDto() {
        Author testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .biography("Famous author of fiction books.")
                .build();

        AuthorDto savedAuthor = authorService.addAuthor(authorMapper.toDto(testAuthor));
        AuthorDto foundAuthor = authorService.getAuthorById(savedAuthor.getAuthorId());

        assertNotNull(foundAuthor);
        assertEquals(foundAuthor.getAuthorId(), savedAuthor.getAuthorId());
        assertEquals(foundAuthor.getFirstName(), savedAuthor.getFirstName());
        assertEquals(foundAuthor.getLastName(), savedAuthor.getLastName());
        assertEquals(foundAuthor.getBiography(), savedAuthor.getBiography());
    }

    @Test
    void getAllAuthors_ShouldReturnListOfAuthorDto() {
        Author testAuthor1 = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .biography("Fiction writer.")
                .build();
        authorService.addAuthor(authorMapper.toDto(testAuthor1));

        Author testAuthor2 = Author.builder()
                .firstName("Jane")
                .lastName("Doe")
                .biography("Non-fiction writer.")
                .build();
        authorService.addAuthor(authorMapper.toDto(testAuthor2));

        List<AuthorDto> foundAuthors = authorService.getAllAuthors();

        assertEquals(2, foundAuthors.size());
    }

    @Test
    void updateAuthor_ShouldUpdateAndReturnAuthorDto() {
        Author testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .biography("Old biography.")
                .build();
        AuthorDto savedAuthor = authorService.addAuthor(authorMapper.toDto(testAuthor));
        savedAuthor.setBiography("Updated biography.");

        AuthorDto updatedAuthor = authorService.updateAuthor(savedAuthor.getAuthorId(), savedAuthor);

        assertNotNull(updatedAuthor);
        assertEquals(updatedAuthor.getBiography(), savedAuthor.getBiography());
    }

    @Test
    void deleteAuthor_ShouldDeleteAuthor() {
        Author testAuthor = Author.builder()
                .firstName("John")
                .lastName("Doe")
                .biography("Some biography.")
                .build();
        AuthorDto savedAuthor = authorService.addAuthor(authorMapper.toDto(testAuthor));
        authorService.deleteAuthor(savedAuthor.getAuthorId());

        assertThrows(NoSuchElementException.class, () -> authorService.getAuthorById(savedAuthor.getAuthorId()));
    }
}
