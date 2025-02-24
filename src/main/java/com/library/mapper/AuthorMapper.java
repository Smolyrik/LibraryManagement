package com.library.mapper;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDto toDto(Author author);

    Author toEntity(AuthorDto authorDto);

    List<AuthorDto> toDtoList(List<Author> authors);

    List<Author> toEntityList(List<AuthorDto> authorDtos);
}
