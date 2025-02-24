package com.library.mapper;

import com.library.dto.BookDto;
import com.library.entity.Book;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    Book toEntity(BookDto bookDto);

    List<BookDto> toDtoList(List<Book> books);

    List<Book> toEntityList(List<BookDto> bookDtos);
}
