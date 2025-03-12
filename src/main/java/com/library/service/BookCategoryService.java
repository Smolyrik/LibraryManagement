package com.library.service;

import com.library.dto.BookCategoryDto;

import java.util.List;

public interface BookCategoryService {

    BookCategoryDto addBookCategory(BookCategoryDto bookCategoryDto);

    BookCategoryDto getBookCategoryById(Integer bookId, Integer categoryId);

    List<BookCategoryDto> getAllBookCategories();

    void deleteBookCategory(Integer bookId, Integer categoryId);
}
