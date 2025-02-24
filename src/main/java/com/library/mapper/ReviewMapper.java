package com.library.mapper;

import com.library.dto.ReviewDto;
import com.library.entity.Review;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "book.bookId", target = "bookId")
    ReviewDto toDto(Review review);

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "bookId", target = "book.bookId")
    Review toEntity(ReviewDto reviewDto);

    List<ReviewDto> toDtoList(List<Review> reviews);
    List<Review> toEntityList(List<ReviewDto> reviewDtos);
}
