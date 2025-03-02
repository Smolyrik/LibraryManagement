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
@Table(name = "review")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", updatable = false, unique = true, nullable = false)
    private Integer reviewId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false, unique = true)
    private User user;

    @OneToOne
    @JoinColumn(name = "book_id", referencedColumnName = "book_id",
            nullable = false, unique = true)
    private Book book;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "rating", nullable = false)
    private Short rating;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
