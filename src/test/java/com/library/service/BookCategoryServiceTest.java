package com.library.service;

import com.library.dto.BookCategoryDto;
import com.library.entity.Book;
import com.library.entity.BookCategory;
import com.library.entity.BookCategoryId;
import com.library.entity.Category;
import com.library.mapper.BookCategoryMapper;
import com.library.repository.BookCategoryRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.service.impl.BookCategoryServiceImpl;
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
class BookCategoryServiceTest {

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookCategoryMapper bookCategoryMapper;

    @InjectMocks
    private BookCategoryServiceImpl bookCategoryService;

    private Book book;
    private Category category;
    private BookCategory bookCategory;
    private BookCategoryDto bookCategoryDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setBookId(1);

        category = new Category();
        category.setCategoryId(1);

        bookCategory = new BookCategory(new BookCategoryId(1, 1), book, category);
        bookCategoryDto = new BookCategoryDto(1, 1);
    }

    @Test
    void testAddBookCategory() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(bookCategoryRepository.save(any(BookCategory.class))).thenReturn(bookCategory);
        when(bookCategoryMapper.toDto(bookCategory)).thenReturn(bookCategoryDto);

        BookCategoryDto result = bookCategoryService.addBookCategory(bookCategoryDto);

        assertNotNull(result);
        assertEquals(1, result.getBookId());
        assertEquals(1, result.getCategoryId());
        verify(bookCategoryRepository, times(1)).save(any(BookCategory.class));
    }

    @Test
    void testAddBookCategory_BookNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookCategoryService.addBookCategory(bookCategoryDto));
    }

    @Test
    void testAddBookCategory_CategoryNotFound() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookCategoryService.addBookCategory(bookCategoryDto));
    }

    @Test
    void testGetBookCategoryById() {
        when(bookCategoryRepository.findById(any(BookCategoryId.class))).thenReturn(Optional.of(bookCategory));
        when(bookCategoryMapper.toDto(bookCategory)).thenReturn(bookCategoryDto);

        BookCategoryDto result = bookCategoryService.getBookCategoryById(1, 1);

        assertNotNull(result);
        assertEquals(1, result.getBookId());
        assertEquals(1, result.getCategoryId());
    }

    @Test
    void testGetBookCategoryById_NotFound() {
        when(bookCategoryRepository.findById(any(BookCategoryId.class))).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> bookCategoryService.getBookCategoryById(1, 1));
    }

    @Test
    void testGetAllBookCategories() {
        List<BookCategory> bookCategories = Collections.singletonList(bookCategory);

        when(bookCategoryRepository.findAll()).thenReturn(bookCategories);
        when(bookCategoryMapper.toDto(bookCategory)).thenReturn(bookCategoryDto);

        List<BookCategoryDto> result = bookCategoryService.getAllBookCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteBookCategory() {
        when(bookCategoryRepository.existsById(any(BookCategoryId.class))).thenReturn(true);

        bookCategoryService.deleteBookCategory(1, 1);

        verify(bookCategoryRepository, times(1)).deleteById(any(BookCategoryId.class));
    }

    @Test
    void testDeleteBookCategory_NotFound() {
        when(bookCategoryRepository.existsById(any(BookCategoryId.class))).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> bookCategoryService.deleteBookCategory(1, 1));
    }
}
