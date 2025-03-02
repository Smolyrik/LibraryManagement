package com.library.mapper;

import com.library.dto.BookDto;
import com.library.entity.Book;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto toDto(Book book);

    Book toEntity(BookDto bookDto);

}
