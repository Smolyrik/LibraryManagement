package com.library.IT;

import com.library.dto.BookDto;
import com.library.dto.ReviewDto;
import com.library.dto.UserDto;
import com.library.entity.Book;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.repository.BookRepository;
import com.library.repository.ReviewRepository;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.ReviewService;
import com.library.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
public class ReviewServiceIT {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void cleanUp() {
        reviewRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    private UserDto createTestUser(String username) {
        User user = User.builder()
                .email(username + "@test.com")
                .username(username)
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        return userService.addUser(userMapper.toDto(user));
    }

    private BookDto createTestBook(String title) {
        Book book = Book.builder()
                .title(title)
                .description("Test Description")
                .availableCopies(5)
                .totalCopies(10)
                .build();
        return bookService.addBook(bookMapper.toDto(book));
    }

    @Test
    void addReview_ShouldSaveAndReturnReviewDto() {
        UserDto user = createTestUser("testUser1");
        BookDto book = createTestBook("testBook1");
        ReviewDto review = new ReviewDto(null, user.getUserId(), book.getBookId(), "Great book!", (short) 5, LocalDateTime.now());

        ReviewDto savedReview = reviewService.addReview(review);

        assertNotNull(savedReview);
        assertNotNull(savedReview.getReviewId());
        assertEquals(review.getComment(), savedReview.getComment());
        assertEquals(review.getRating(), savedReview.getRating());
    }

    @Test
    void getReviewById_ShouldReturnReviewDto() {
        UserDto user = createTestUser("testUser2");
        BookDto book = createTestBook("testBook2");
        ReviewDto review = new ReviewDto(null, user.getUserId(), book.getBookId(), "Nice read", (short) 4, LocalDateTime.now());
        ReviewDto savedReview = reviewService.addReview(review);

        ReviewDto foundReview = reviewService.getReviewById(savedReview.getReviewId());

        assertNotNull(foundReview);
        assertEquals(savedReview.getReviewId(), foundReview.getReviewId());
    }

    @Test
    void getAllReviews_ShouldReturnListOfReviewDto() {
        UserDto user1 = createTestUser("user1");
        UserDto user2 = createTestUser("user2");
        BookDto book1 = createTestBook("book1");
        BookDto book2 = createTestBook("book2");

        reviewService.addReview(new ReviewDto(null, user1.getUserId(), book1.getBookId(), "Amazing book!", (short) 5, LocalDateTime.now()));
        reviewService.addReview(new ReviewDto(null, user2.getUserId(), book2.getBookId(), "Not bad", (short) 3, LocalDateTime.now()));

        List<ReviewDto> reviews = reviewService.getAllReviews();

        assertTrue(reviews.size() >= 2);
    }

    @Test
    void updateReview_ShouldUpdateAndReturnReviewDto() {
        UserDto user = createTestUser("testUser");
        BookDto book = createTestBook("testBook");
        ReviewDto review = new ReviewDto(null, user.getUserId(), book.getBookId(), "Good read", (short) 4, LocalDateTime.now());
        ReviewDto savedReview = reviewService.addReview(review);
        savedReview.setComment("Excellent read");
        savedReview.setRating((short) 5);

        ReviewDto updatedReview = reviewService.updateReview(savedReview.getReviewId(), savedReview);

        assertNotNull(updatedReview);
        assertEquals("Excellent read", updatedReview.getComment());
        assertEquals(5, (int)updatedReview.getRating());
    }

    @Test
    void deleteReview_ShouldRemoveReview() {
        UserDto user = createTestUser("testUser");
        BookDto book = createTestBook("testBook");
        ReviewDto review = new ReviewDto(null, user.getUserId(), book.getBookId(), "Nice", (short) 3, LocalDateTime.now());
        ReviewDto savedReview = reviewService.addReview(review);

        reviewService.deleteReview(savedReview.getReviewId());

        assertThrows(NoSuchElementException.class, () -> reviewService.getReviewById(savedReview.getReviewId()));
    }
}
