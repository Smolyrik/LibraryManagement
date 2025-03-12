package com.library.service;

import com.library.dto.BookAuthorDto;

import java.util.List;

public interface BookAuthorService {

    BookAuthorDto addBookAuthor(BookAuthorDto bookAuthorDto);

    BookAuthorDto getBookAuthorById(Integer bookId, Integer authorId);

    List<BookAuthorDto> getAllBookAuthors();

    void deleteBookAuthor(Integer bookId, Integer authorId);

}
