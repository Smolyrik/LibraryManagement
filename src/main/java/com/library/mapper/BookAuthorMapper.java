package com.library.mapper;

import com.library.dto.BookAuthorDto;
import com.library.entity.BookAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookAuthorMapper {

    @Mapping(source = "book.bookId", target = "bookId")
    @Mapping(source = "author.id", target = "authorId")
    BookAuthorDto toDto(BookAuthor bookAuthor);

    @Mapping(source = "bookId", target = "book.bookId")
    @Mapping(source = "authorId", target = "author.id")
    @Mapping(target = "id", expression = "java(new BookAuthorId(bookAuthorDto.getBookId(), bookAuthorDto.getAuthorId()))")
    BookAuthor toEntity(BookAuthorDto bookAuthorDto);

    List<BookAuthorDto> toDtoList(List<BookAuthor> bookAuthors);

    List<BookAuthor> toEntityList(List<BookAuthorDto> bookAuthorDtos);
}
