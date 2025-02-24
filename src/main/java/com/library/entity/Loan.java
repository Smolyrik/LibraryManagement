package com.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_id", updatable = false, nullable = false, unique = true)
    private Integer loanId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @Column(name = "loan_date", nullable = false)
    private LocalDateTime loanDate;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false, unique = true)
    private User user;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "book_id",
            nullable = false, unique = true)
    private Book book;
}
