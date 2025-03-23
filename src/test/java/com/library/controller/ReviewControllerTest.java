package com.library.controller;

import com.library.dto.ReviewDto;
import com.library.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewServiceImpl reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewDto reviewDto;
    private Integer reviewId;
    private Integer bookId;

    @BeforeEach
    void setUp() {
        reviewId = 1;
        Integer userId = 2;
        bookId = 3;
        reviewDto = ReviewDto.builder()
                .reviewId(reviewId)
                .userId(userId)
                .bookId(bookId)
                .comment("Great book!")
                .rating((short) 9)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void addReview_ShouldReturnReview() {
        when(reviewService.addReview(any(ReviewDto.class))).thenReturn(reviewDto);
        ResponseEntity<ReviewDto> response = reviewController.addReview(reviewDto);

        assertNotNull(response.getBody());
        assertEquals(reviewDto.getReviewId(), response.getBody().getReviewId());
        verify(reviewService, times(1)).addReview(any(ReviewDto.class));
    }

    @Test
    void getReviewById_ShouldReturnReview() {
        when(reviewService.getReviewById(reviewId)).thenReturn(reviewDto);
        ResponseEntity<ReviewDto> response = reviewController.getReviewById(reviewId);

        assertNotNull(response.getBody());
        assertEquals(reviewId, response.getBody().getReviewId());
        verify(reviewService, times(1)).getReviewById(reviewId);
    }

    @Test
    void getAllReviews_ShouldReturnReviewList() {
        when(reviewService.getAllReviews()).thenReturn(Collections.singletonList(reviewDto));
        ResponseEntity<List<ReviewDto>> response = reviewController.getAllReviews();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(reviewService, times(1)).getAllReviews();
    }

    @Test
    void getReviewsByBookId_ShouldReturnMatchingReviews() {
        when(reviewService.getReviewsByBookId(bookId)).thenReturn(Collections.singletonList(reviewDto));
        ResponseEntity<List<ReviewDto>> response = reviewController.getReviewsByBookId(bookId);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertEquals(bookId, response.getBody().get(0).getBookId());
        verify(reviewService, times(1)).getReviewsByBookId(bookId);
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        when(reviewService.updateReview(eq(reviewId), any(ReviewDto.class))).thenReturn(reviewDto);
        ResponseEntity<ReviewDto> response = reviewController.updateReview(reviewId, reviewDto);

        assertNotNull(response.getBody());
        assertEquals(reviewId, response.getBody().getReviewId());
        verify(reviewService, times(1)).updateReview(eq(reviewId), any(ReviewDto.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() {
        doNothing().when(reviewService).deleteReview(reviewId);
        ResponseEntity<Void> response = reviewController.deleteReview(reviewId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reviewService, times(1)).deleteReview(reviewId);
    }
}
