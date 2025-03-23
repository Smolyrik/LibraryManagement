package com.library.controller;

import com.library.dto.AuthorDto;
import com.library.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorServiceImpl authorService;

    @InjectMocks
    private AuthorController authorController;

    private AuthorDto authorDto;
    private Integer authorId;

    @BeforeEach
    void setUp() {
        authorId = 1;
        authorDto = AuthorDto.builder()
                .authorId(authorId)
                .firstName("John")
                .lastName("Doe")
                .biography("Famous author")
                .build();
    }

    @Test
    void addAuthor_ShouldReturnAuthor() {
        when(authorService.addAuthor(any(AuthorDto.class))).thenReturn(authorDto);
        ResponseEntity<AuthorDto> response = authorController.addAuthor(authorDto);

        assertNotNull(response.getBody());
        assertEquals(authorDto.getAuthorId(), response.getBody().getAuthorId());
        verify(authorService, times(1)).addAuthor(any(AuthorDto.class));
    }

    @Test
    void getAuthorById_ShouldReturnAuthor() {
        when(authorService.getAuthorById(authorId)).thenReturn(authorDto);
        ResponseEntity<AuthorDto> response = authorController.getAuthorById(authorId);

        assertNotNull(response.getBody());
        assertEquals(authorId, response.getBody().getAuthorId());
        verify(authorService, times(1)).getAuthorById(authorId);
    }

    @Test
    void getAllAuthors_ShouldReturnAuthorList() {
        when(authorService.getAllAuthors()).thenReturn(Collections.singletonList(authorDto));
        ResponseEntity<List<AuthorDto>> response = authorController.getAllAuthors();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(authorService, times(1)).getAllAuthors();
    }

    @Test
    void updateAuthor_ShouldReturnUpdatedAuthor() {
        when(authorService.updateAuthor(eq(authorId), any(AuthorDto.class))).thenReturn(authorDto);
        ResponseEntity<AuthorDto> response = authorController.updateAuthor(authorId, authorDto);

        assertNotNull(response.getBody());
        assertEquals(authorId, response.getBody().getAuthorId());
        verify(authorService, times(1)).updateAuthor(eq(authorId), any(AuthorDto.class));
    }

    @Test
    void deleteAuthor_ShouldReturnNoContent() {
        doNothing().when(authorService).deleteAuthor(authorId);
        ResponseEntity<Void> response = authorController.deleteAuthor(authorId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(authorService, times(1)).deleteAuthor(authorId);
    }
}
