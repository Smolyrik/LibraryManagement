package com.library.mapper;

import com.library.dto.BookAuthorDto;
import com.library.entity.BookAuthor;
import com.library.entity.BookAuthorId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookAuthorMapper {

    @Mapping(source = "book.bookId", target = "bookId")
    @Mapping(source = "author.authorId", target = "authorId")
    BookAuthorDto toDto(BookAuthor bookAuthor);

    @Mapping(source = "bookId", target = "book.bookId")
    @Mapping(source = "authorId", target = "author.authorId")
    @Mapping(target = "id", expression = "java(mapToId(bookAuthorDto.getBookId(), bookAuthorDto.getAuthorId()))")
    BookAuthor toEntity(BookAuthorDto bookAuthorDto);

    default BookAuthorId mapToId(Integer bookId, Integer authorId) {
        return new BookAuthorId(bookId, authorId);
    }
}
