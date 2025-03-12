package com.library.service;

import com.library.dto.BookDto;

import java.util.List;

public interface BookService {

    BookDto addBook(BookDto bookDto);

    BookDto getBookById(Integer bookId);

    List<BookDto> getAllBooks();

    BookDto updateBook(Integer bookId, BookDto bookDto);

    void deleteBook(Integer bookId);

    List<BookDto> findBooksByTitle(String title);

    List<BookDto> findBooksByAvailableCopiesRange(Integer min, Integer max);

    List<BookDto> findBooksByAuthor(String authorName);

    List<BookDto> findBooksByCategory(String categoryName);
}
