package com.library.mapper;

import com.library.dto.BookCategoryDto;
import com.library.entity.BookCategory;
import com.library.entity.BookCategoryId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface BookCategoryMapper {

    @Mapping(source = "book.bookId", target = "bookId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    BookCategoryDto toDto(BookCategory bookCategory);

    @Mapping(source = "bookId", target = "book.bookId")
    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(target = "id", expression = "java(mapToId(bookCategoryDto.getBookId(), bookCategoryDto.getCategoryId()))")
    BookCategory toEntity(BookCategoryDto bookCategoryDto);

    default BookCategoryId mapToId(Integer bookId, Integer categoryId) {
        return new BookCategoryId(bookId, categoryId);
    }
}
