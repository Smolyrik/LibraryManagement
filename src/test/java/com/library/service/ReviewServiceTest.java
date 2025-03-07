package com.library.service;

import com.library.dto.ReviewDto;
import com.library.entity.Review;
import com.library.mapper.ReviewMapper;
import com.library.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewDto reviewDto;
    private Review review;

    @BeforeEach
    void setUp() {
        reviewDto = new ReviewDto(1, 1, 1, "Great book!", (short) 9, null);
        review = new Review(1, null, null, "Great book!", (short) 9, null);
    }

    @Test
    void addReview_ShouldReturnSavedReviewDto() {
        when(reviewMapper.toEntity(reviewDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto savedReview = reviewService.addReview(reviewDto);

        assertNotNull(savedReview);
        assertEquals(reviewDto.getComment(), savedReview.getComment());
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void getReviewById_ShouldReturnReviewDto_WhenReviewExists() {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto foundReview = reviewService.getReviewById(1);

        assertNotNull(foundReview);
        assertEquals(1, foundReview.getReviewId());
    }

    @Test
    void getReviewById_ShouldThrowException_WhenReviewNotFound() {
        when(reviewRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> reviewService.getReviewById(1));
    }

    @Test
    void getAllReviews_ShouldReturnListOfReviews() {
        when(reviewRepository.findAll()).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        List<ReviewDto> reviews = reviewService.getAllReviews();

        assertFalse(reviews.isEmpty());
        assertEquals(1, reviews.size());
    }

    @Test
    void updateReview_ShouldReturnUpdatedReviewDto() {
        when(reviewRepository.existsById(1)).thenReturn(true);
        when(reviewMapper.toEntity(reviewDto)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        ReviewDto updatedReview = reviewService.updateReview(1, reviewDto);

        assertNotNull(updatedReview);
        assertEquals(reviewDto.getComment(), updatedReview.getComment());
    }

    @Test
    void updateReview_ShouldThrowException_WhenReviewNotFound() {
        when(reviewRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reviewService.updateReview(1, reviewDto));
    }

    @Test
    void deleteReview_ShouldDeleteReview_WhenReviewExists() {
        when(reviewRepository.existsById(1)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1);

        assertDoesNotThrow(() -> reviewService.deleteReview(1));
        verify(reviewRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewNotFound() {
        when(reviewRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> reviewService.deleteReview(1));
    }
}
