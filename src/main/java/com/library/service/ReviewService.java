package com.library.service;

import com.library.dto.ReviewDto;
import com.library.entity.Review;
import com.library.mapper.ReviewMapper;
import com.library.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewDto addReview(ReviewDto reviewDto) {
        Review review = reviewMapper.toEntity(reviewDto);
        Review savedReview = reviewRepository.save(review);
        log.info("Added new review with ID: {}", savedReview.getReviewId());
        return reviewMapper.toDto(savedReview);
    }

    public ReviewDto getReviewById(Integer reviewId) {
        return reviewRepository.findById(reviewId)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Review with ID: {} not found", reviewId);
                    return new NoSuchElementException("Review with ID: " + reviewId + " not found");
                });
    }

    public List<ReviewDto> getAllReviews() {
        log.info("Fetching all reviews");
        return reviewRepository.findAll().stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDto> getReviewsByBookId(Integer bookId) {
        log.info("Fetching reviews by book ID: {}", bookId);
        return reviewRepository.findByBookBookId(bookId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewDto updateReview(Integer reviewId, ReviewDto reviewDto) {
        if (!reviewRepository.existsById(reviewId)) {
            log.error("Review with ID: {} not found", reviewId);
            throw new NoSuchElementException("Review with ID: " + reviewId + " not found");
        }

        Review updatedReview = reviewMapper.toEntity(reviewDto);
        updatedReview.setReviewId(reviewId);

        Review savedReview = reviewRepository.save(updatedReview);
        log.info("Updated review with ID: {}", savedReview.getReviewId());

        return reviewMapper.toDto(savedReview);
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            log.error("Review with ID: {} not found", reviewId);
            throw new NoSuchElementException("Review with ID: " + reviewId + " not found");
        }
        reviewRepository.deleteById(reviewId);
        log.info("Deleted review with ID: {}", reviewId);
    }
}
