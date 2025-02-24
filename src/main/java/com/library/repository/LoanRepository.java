package com.library.repository;

import com.library.entity.Loan;
import com.library.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {

    List<Loan> findByUserUserId(Integer userId);

    List<Loan> findByStatusAndReturnDateBefore(LoanStatus status, LocalDateTime returnDate);
}
