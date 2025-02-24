package com.library.service;

import com.library.dto.LoanDto;
import com.library.entity.Book;
import com.library.entity.Loan;
import com.library.entity.LoanStatus;
import com.library.entity.User;
import com.library.mapper.LoanMapper;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final LoanMapper loanMapper;

    @Transactional
    public LoanDto addLoan(LoanDto loanDto) {
        Loan loan = loanMapper.toEntity(loanDto);
        Loan savedLoan = loanRepository.save(loan);
        log.info("Added new loan with ID: {}", savedLoan.getLoanId());
        return loanMapper.toDto(savedLoan);
    }

    public LoanDto getLoanById(int loanId) {
        return loanRepository.findById(loanId)
                .map(loanMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Loan with ID: {} not found", loanId);
                    return new NoSuchElementException("Loan with ID: " + loanId + " not found");
                });
    }

    public List<LoanDto> getAllLoans() {
        log.info("Fetching all loans");
        return loanRepository.findAll().stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public LoanDto updateLoan(int loanId, LoanDto loanDto) {
        if (!loanRepository.existsById(loanId)) {
            log.error("Loan with ID: {} not found", loanId);
            throw new NoSuchElementException("Loan with ID: " + loanId + " not found");
        }

        Loan updatedLoan = loanMapper.toEntity(loanDto);
        updatedLoan.setLoanId(loanId);

        Loan savedLoan = loanRepository.save(updatedLoan);
        log.info("Updated loan with ID: {}", savedLoan.getLoanId());

        return loanMapper.toDto(savedLoan);
    }

    @Transactional
    public void deleteLoan(int loanId) {
        if (!loanRepository.existsById(loanId)) {
            log.error("Loan with ID: {} not found", loanId);
            throw new NoSuchElementException("Loan with ID: " + loanId + " not found");
        }
        loanRepository.deleteById(loanId);
        log.info("Deleted loan with ID: {}", loanId);
    }

    @Transactional
    public Loan loanBook(Integer userId, Integer bookId, Integer loanTime) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book with ID: " + bookId + " not found"));
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies");
        }

        Loan loan = new Loan();
        loan.setUser(User.builder().userId(userId).build());
        loan.setBook(book);
        loan.setLoanDate(LocalDateTime.now());
        loan.setReturnDate(LocalDateTime.now().plusDays(loanTime));
        loan.setStatus(LoanStatus.ACTIVE);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return loanRepository.save(loan);
    }

    @Transactional
    public boolean returnBook(Integer loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NoSuchElementException("Loan with ID: " + loanId + " not found"));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            log.warn("Loan with ID: {} is already returned", loanId);
            return false;
        }

        loan.setStatus(LoanStatus.RETURNED);
        loanRepository.save(loan);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        log.info("Book with ID: {} returned by user {}", book.getBookId(), loan.getUser().getUserId());
        return true;
    }

    public List<LoanDto> getLoansByUserId(int userId) {
        log.info("Fetching all loans by user {}", userId);
        return loanRepository.findByUserUserId(userId).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<LoanDto> checkOverdueLoans() {
        log.info("Checking overdue loans");

        List<Loan> overdueLoans = loanRepository.findByStatusAndReturnDateBefore(LoanStatus.ACTIVE, LocalDateTime.now());

        if (overdueLoans.isEmpty()) {
            log.info("No overdue loans found.");
        } else {
            log.info("Found {} overdue loans.", overdueLoans.size());
        }

        return overdueLoans.stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }
}
