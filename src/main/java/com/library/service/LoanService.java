package com.library.service;

import com.library.dto.LoanDto;

import java.util.List;

public interface LoanService {

    LoanDto addLoan(LoanDto loanDto);

    LoanDto getLoanById(int loanId);

    List<LoanDto> getAllLoans();

    LoanDto updateLoan(int loanId, LoanDto loanDto);

    void deleteLoan(int loanId);

    LoanDto loanBook(Integer userId, Integer bookId, Integer loanTime);

    boolean returnBook(Integer loanId);

    List<LoanDto> getLoansByUserId(int userId);

    List<LoanDto> checkOverdueLoans();
}
