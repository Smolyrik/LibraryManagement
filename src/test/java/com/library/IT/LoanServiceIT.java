package com.library.IT;

import com.library.dto.BookDto;
import com.library.dto.LoanDto;
import com.library.dto.UserDto;
import com.library.entity.Book;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.BookMapper;
import com.library.mapper.UserMapper;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import com.library.service.impl.BookServiceImpl;
import com.library.service.impl.LoanServiceImpl;
import com.library.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanServiceIT {

    @Autowired
    private LoanServiceImpl loanService;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BookServiceImpl bookService;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @AfterAll
    static void teardown() {
        postgres.stop();
    }

    @AfterEach
    public void cleanUp() {
        loanRepository.deleteAll();
        loanRepository.flush();
        bookRepository.deleteAll();
        bookRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
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
    void addLoan_ShouldSaveAndReturnLoanDto() {
        UserDto user = createTestUser("testUser1");
        BookDto book = createTestBook("testBook1");

        LoanDto loanDto = new LoanDto(null,
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                user.getUserId(),
                book.getBookId());
        LoanDto savedLoan = loanService.addLoan(loanDto);

        assertNotNull(savedLoan);
        assertNotNull(savedLoan.getLoanId());
        assertEquals(user.getUserId(), savedLoan.getUserId());
        assertEquals(book.getBookId(), savedLoan.getBookId());
    }

    @Test
    void getLoanById_ShouldReturnLoanDto() {
        UserDto user = createTestUser("testUser2");
        BookDto book = createTestBook("testBook2");

        LoanDto loanDto = new LoanDto(null,
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                user.getUserId(),
                book.getBookId());

        LoanDto savedLoan = loanService.addLoan(loanDto);

        LoanDto foundLoan = loanService.getLoanById(savedLoan.getLoanId());

        assertNotNull(foundLoan);
        assertEquals(savedLoan.getLoanId(), foundLoan.getLoanId());
    }

    @Test
    void getAllLoans_ShouldReturnListOfLoanDto() {
        UserDto user1 = createTestUser("user1");
        UserDto user2 = createTestUser("user2");
        BookDto book1 = createTestBook("book1");
        BookDto book2 = createTestBook("book2");

        LoanDto loanDto1 = new LoanDto(null,
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                user1.getUserId(),
                book1.getBookId());

        LoanDto loanDto2 = new LoanDto(null,
                "ACTIVE",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                user2.getUserId(),
                book2.getBookId());

        loanService.addLoan(loanDto1);
        loanService.addLoan(loanDto2);

        List<LoanDto> loans = loanService.getAllLoans();
        assertEquals(2, loans.size());
    }

    @Test
    void updateLoan_ShouldUpdateAndReturnLoanDto() {
        UserDto user = createTestUser("testUser");
        BookDto book = createTestBook("testBook");

        LoanDto loanDto = new LoanDto(null, "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusDays(7), user.getUserId(), book.getBookId());
        LoanDto savedLoan = loanService.addLoan(loanDto);
        savedLoan.setStatus("RETURNED");

        LoanDto updatedLoan = loanService.updateLoan(savedLoan.getLoanId(), savedLoan);

        assertNotNull(updatedLoan);
        assertEquals("RETURNED", updatedLoan.getStatus());
    }

    @Test
    void deleteLoan_ShouldRemoveLoan() {
        UserDto user = createTestUser("testUser");
        BookDto book = createTestBook("testBook");

        LoanDto loanDto = new LoanDto(null, "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusDays(7), user.getUserId(), book.getBookId());
        LoanDto savedLoan = loanService.addLoan(loanDto);

        loanService.deleteLoan(savedLoan.getLoanId());

        assertThrows(NoSuchElementException.class, () -> loanService.getLoanById(savedLoan.getLoanId()));
    }

    @Test
    void loanBook_ShouldCreateLoanForUserAndBook() {
        UserDto user = createTestUser("loanUser");
        BookDto book = createTestBook("loanBook");

        LoanDto loan = loanService.loanBook(user.getUserId(), book.getBookId(), 7);

        assertNotNull(loan);
        assertEquals(user.getUserId(), loan.getUserId());
        assertEquals(book.getBookId(), loan.getBookId());
        assertEquals("ACTIVE", loan.getStatus());
    }

    @Test
    void getLoansByUserId_ShouldReturnListOfLoansForUser() {
        UserDto user = createTestUser("loanUserById");
        BookDto book1 = createTestBook("loanBook1");
        BookDto book2 = createTestBook("loanBook2");

        loanService.addLoan(new LoanDto(null, "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusDays(7), user.getUserId(), book1.getBookId()));
        loanService.addLoan(new LoanDto(null, "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusDays(7), user.getUserId(), book2.getBookId()));

        List<LoanDto> loans = loanService.getLoansByUserId(user.getUserId());

        assertNotNull(loans);
        assertEquals(2, loans.size());
        assertTrue(loans.stream().allMatch(loan -> loan.getUserId().equals(user.getUserId())));
    }

    @Test
    void returnBook_ShouldUpdateLoanStatus() {
        UserDto user = createTestUser("returnUser");
        BookDto book = createTestBook("returnBook");

        LoanDto loan = loanService.loanBook(user.getUserId(), book.getBookId(), 7);
        assertTrue(loanService.returnBook(loan.getLoanId()));
    }

    @Test
    void checkOverdueLoans_ShouldReturnOverdueLoans() {
        UserDto user = createTestUser("overdueUser");
        BookDto book = createTestBook("overdueBook");

        LoanDto loan = new LoanDto(null, "ACTIVE", LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(3), user.getUserId(), book.getBookId());
        loanService.addLoan(loan);

        List<LoanDto> overdueLoans = loanService.checkOverdueLoans();
        assertFalse(overdueLoans.isEmpty());
    }
}
