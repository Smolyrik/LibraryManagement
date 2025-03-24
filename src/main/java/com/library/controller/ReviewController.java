package com.library.controller;

import com.library.dto.ReviewDto;
import com.library.service.impl.ReviewServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "Operations related to book reviews")
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    @Operation(
            summary = "Add a new review",
            description = "Creates a new review for a book.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created review",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<ReviewDto> addReview(@Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.addReview(reviewDto));
    }

    @Operation(
            summary = "Get review by ID",
            description = "Retrieves a review by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved review",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewDto.class))),
                    @ApiResponse(responseCode = "404", description = "Review not found")
            }
    )
    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<ReviewDto> getReviewById(@PathVariable Integer id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @Operation(
            summary = "Get all reviews",
            description = "Retrieves a list of all reviews.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reviews",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReviewDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @Operation(
            summary = "Get reviews by book ID",
            description = "Retrieves all reviews for a specific book.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReviewDto.class))))
            }
    )
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDto>> getReviewsByBookId(@PathVariable Integer bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    @Operation(
            summary = "Update review",
            description = "Updates an existing review.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated review",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewDto.class))),
                    @ApiResponse(responseCode = "404", description = "Review not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Integer id, @Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.updateReview(id, reviewDto));
    }

    @Operation(
            summary = "Delete review",
            description = "Deletes a review by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted review"),
                    @ApiResponse(responseCode = "404", description = "Review not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
