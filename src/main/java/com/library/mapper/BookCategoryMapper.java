package com.library.mapper;

import com.library.dto.BookCategoryDto;
import com.library.entity.BookCategory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookCategoryMapper {

    @Mapping(source = "book.bookId", target = "bookId")
    @Mapping(source = "category.categoryId", target = "categoryId")
    BookCategoryDto toDto(BookCategory bookCategory);

    @Mapping(source = "bookId", target = "book.bookId")
    @Mapping(source = "categoryId", target = "category.categoryId")
    @Mapping(target = "id", expression = "java(new BookCategoryId(bookCategoryDto.getBookId(), bookCategoryDto.getCategoryId()))")
    BookCategory toEntity(BookCategoryDto bookCategoryDto);

    List<BookCategoryDto> toDtoList(List<BookCategory> bookCategories);

    List<BookCategory> toEntityList(List<BookCategoryDto> bookCategoryDtos);

}
