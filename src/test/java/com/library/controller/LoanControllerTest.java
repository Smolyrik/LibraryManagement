package com.library.controller;

import com.library.dto.LoanDto;
import com.library.service.impl.LoanServiceImpl;
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
class LoanControllerTest {

    @Mock
    private LoanServiceImpl loanService;

    @InjectMocks
    private LoanController loanController;

    private LoanDto loanDto;
    private Integer loanId;

    @BeforeEach
    void setUp() {
        loanId = 1;
        loanDto = LoanDto.builder()
                .loanId(loanId)
                .status("ACTIVE")
                .loanDate(LocalDateTime.now().minusDays(1))
                .returnDate(LocalDateTime.now().plusDays(7))
                .userId(1)
                .bookId(1)
                .build();
    }

    @Test
    void addLoan_ShouldReturnLoan() {
        when(loanService.addLoan(any(LoanDto.class))).thenReturn(loanDto);
        ResponseEntity<LoanDto> response = loanController.addLoan(loanDto);

        assertNotNull(response.getBody());
        assertEquals(loanDto.getLoanId(), response.getBody().getLoanId());
        verify(loanService, times(1)).addLoan(any(LoanDto.class));
    }

    @Test
    void getLoanById_ShouldReturnLoan() {
        when(loanService.getLoanById(loanId)).thenReturn(loanDto);
        ResponseEntity<LoanDto> response = loanController.getLoanById(loanId);

        assertNotNull(response.getBody());
        assertEquals(loanId, response.getBody().getLoanId());
        verify(loanService, times(1)).getLoanById(loanId);
    }

    @Test
    void getAllLoans_ShouldReturnLoanList() {
        when(loanService.getAllLoans()).thenReturn(Collections.singletonList(loanDto));
        ResponseEntity<List<LoanDto>> response = loanController.getAllLoans();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(loanService, times(1)).getAllLoans();
    }

    @Test
    void updateLoan_ShouldReturnUpdatedLoan() {
        when(loanService.updateLoan(eq(loanId), any(LoanDto.class))).thenReturn(loanDto);
        ResponseEntity<LoanDto> response = loanController.updateLoan(loanId, loanDto);

        assertNotNull(response.getBody());
        assertEquals(loanId, response.getBody().getLoanId());
        verify(loanService, times(1)).updateLoan(eq(loanId), any(LoanDto.class));
    }

    @Test
    void deleteLoan_ShouldReturnNoContent() {
        doNothing().when(loanService).deleteLoan(loanId);
        ResponseEntity<Void> response = loanController.deleteLoan(loanId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(loanService, times(1)).deleteLoan(loanId);
    }

    @Test
    void loanBook_ShouldReturnLoan() {
        when(loanService.loanBook(1, 1, 7)).thenReturn(loanDto);
        ResponseEntity<LoanDto> response = loanController.loanBook(1, 1, 7);

        assertNotNull(response.getBody());
        assertEquals(loanDto.getLoanId(), response.getBody().getLoanId());
        verify(loanService, times(1)).loanBook(1, 1, 7);
    }

    @Test
    void returnBook_ShouldReturnTrue() {
        when(loanService.returnBook(loanId)).thenReturn(true);
        ResponseEntity<Boolean> response = loanController.returnBook(loanId);

        assertNotNull(response.getBody());
        assertTrue(response.getBody());
        verify(loanService, times(1)).returnBook(loanId);
    }

    @Test
    void getLoansByUser_ShouldReturnUserLoans() {
        when(loanService.getLoansByUserId(1)).thenReturn(Collections.singletonList(loanDto));
        ResponseEntity<List<LoanDto>> response = loanController.getLoansByUser(1);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(loanService, times(1)).getLoansByUserId(1);
    }

    @Test
    void getOverdueLoans_ShouldReturnOverdueLoans() {
        when(loanService.checkOverdueLoans()).thenReturn(Collections.singletonList(loanDto));
        ResponseEntity<List<LoanDto>> response = loanController.getOverdueLoans();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(loanService, times(1)).checkOverdueLoans();
    }
}
