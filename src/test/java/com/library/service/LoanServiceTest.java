package com.library.service;

import com.library.dto.LoanDto;
import com.library.entity.Book;
import com.library.entity.Loan;
import com.library.entity.LoanStatus;
import com.library.entity.User;
import com.library.mapper.LoanMapper;
import com.library.repository.LoanRepository;
import com.library.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan loan;
    private LoanDto loanDto;
    private Book book;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);

        book = new Book();
        book.setBookId(1);
        book.setAvailableCopies(2);

        loan = new Loan(1, LoanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7), user, book);
        loanDto = new LoanDto(1, "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusDays(7), 1, 1);
    }

    @Test
    void addLoan_ShouldReturnSavedLoanDto() {
        when(loanMapper.toEntity(loanDto)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto savedLoan = loanService.addLoan(loanDto);

        assertNotNull(savedLoan);
        assertEquals(loanDto.getLoanId(), savedLoan.getLoanId());
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    void getLoanById_WhenLoanExists_ShouldReturnLoanDto() {
        when(loanRepository.findById(1)).thenReturn(Optional.of(loan));
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto foundLoan = loanService.getLoanById(1);

        assertNotNull(foundLoan);
        assertEquals(loanDto.getLoanId(), foundLoan.getLoanId());
    }

    @Test
    void getLoanById_WhenLoanDoesNotExist_ShouldThrowException() {
        when(loanRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> loanService.getLoanById(1));
    }

    @Test
    void updateLoan_WhenLoanExists_ShouldReturnUpdatedLoanDto() {
        when(loanRepository.existsById(1)).thenReturn(true);
        when(loanMapper.toEntity(loanDto)).thenReturn(loan);
        when(loanRepository.save(loan)).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        LoanDto updatedLoan = loanService.updateLoan(1, loanDto);

        assertNotNull(updatedLoan);
        assertEquals(loanDto.getLoanId(), updatedLoan.getLoanId());
    }

    @Test
    void updateLoan_WhenLoanDoesNotExist_ShouldThrowException() {
        when(loanRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> loanService.updateLoan(1, loanDto));
    }

    @Test
    void deleteLoan_WhenLoanExists_ShouldDeleteLoan() {
        when(loanRepository.existsById(1)).thenReturn(true);
        doNothing().when(loanRepository).deleteById(1);

        assertDoesNotThrow(() -> loanService.deleteLoan(1));
        verify(loanRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteLoan_WhenLoanDoesNotExist_ShouldThrowException() {
        when(loanRepository.existsById(1)).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> loanService.deleteLoan(1));
    }

    @Test
    void checkOverdueLoans_ShouldReturnOverdueLoans() {
        List<Loan> overdueLoans = Collections.singletonList(loan);
        when(loanRepository.findByStatusAndReturnDateBefore(eq(LoanStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(overdueLoans);
        when(loanMapper.toDto(loan)).thenReturn(loanDto);

        List<LoanDto> result = loanService.checkOverdueLoans();

        assertEquals(1, result.size());
        verify(loanRepository, times(1)).findByStatusAndReturnDateBefore(eq(LoanStatus.ACTIVE), any(LocalDateTime.class));
    }
}
