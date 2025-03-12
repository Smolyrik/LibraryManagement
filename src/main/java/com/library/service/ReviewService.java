package com.library.service;

import com.library.dto.ReviewDto;

import java.util.List;

public interface ReviewService {

    ReviewDto addReview(ReviewDto reviewDto);

    ReviewDto getReviewById(Integer reviewId);

    List<ReviewDto> getAllReviews();

    List<ReviewDto> getReviewsByBookId(Integer bookId);

    ReviewDto updateReview(Integer reviewId, ReviewDto reviewDto);

    void deleteReview(Integer reviewId);
}
