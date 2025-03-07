package com.library.service;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
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
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorService authorService;

    private Author author;
    private AuthorDto authorDto;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setAuthorId(1);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("biography");

        authorDto = new AuthorDto();
        authorDto.setAuthorId(1);
        author.setFirstName("John");
        author.setLastName("Doe");
        author.setBiography("biography");
    }

    @Test
    void testAddAuthor() {
        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorDto result = authorService.addAuthor(authorDto);

        assertNotNull(result);
        assertEquals(authorDto.getAuthorId(), result.getAuthorId());
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testGetAuthorById() {
        when(authorRepository.findById(1)).thenReturn(Optional.of(author));
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorDto result = authorService.getAuthorById(1);

        assertNotNull(result);
        assertEquals(authorDto.getAuthorId(), result.getAuthorId());
        verify(authorRepository, times(1)).findById(1);
    }

    @Test
    void testGetAuthorById_NotFound() {
        when(authorRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authorService.getAuthorById(1));
        verify(authorRepository, times(1)).findById(1);
    }

    @Test
    void testGetAllAuthors() {
        List<Author> authors = Collections.singletonList(author);

        when(authorRepository.findAll()).thenReturn(authors);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        List<AuthorDto> result = authorService.getAllAuthors();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAuthor() {
        when(authorRepository.existsById(1)).thenReturn(true);
        when(authorMapper.toEntity(authorDto)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);
        when(authorMapper.toDto(author)).thenReturn(authorDto);

        AuthorDto result = authorService.updateAuthor(1, authorDto);

        assertNotNull(result);
        assertEquals(authorDto.getAuthorId(), result.getAuthorId());
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void testUpdateAuthor_NotFound() {
        when(authorRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> authorService.updateAuthor(1, authorDto));
        verify(authorRepository, times(1)).existsById(1);
    }

    @Test
    void testDeleteAuthor() {
        when(authorRepository.existsById(1)).thenReturn(true);

        authorService.deleteAuthor(1);

        verify(authorRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteAuthor_NotFound() {
        when(authorRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> authorService.deleteAuthor(1));
        verify(authorRepository, times(1)).existsById(1);
    }
}
