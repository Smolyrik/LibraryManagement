package com.library.service;

import com.library.dto.AuthorDto;

import java.util.List;

public interface AuthorService {

    AuthorDto addAuthor(AuthorDto authorDto);

    AuthorDto getAuthorById(Integer authorId);

    List<AuthorDto> getAllAuthors();

    AuthorDto updateAuthor(Integer authorId, AuthorDto authorDto);

    void deleteAuthor(Integer authorId);
}
