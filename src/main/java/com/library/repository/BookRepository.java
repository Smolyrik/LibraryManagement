package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN BookAuthor ba ON b.bookId = ba.book.bookId " +
            "JOIN Author a ON ba.author.id = a.id " +
            "WHERE LOWER(a.firstName) LIKE LOWER(CONCAT('%', :authorName, '%')) " +
            "OR LOWER(a.lastName) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthor(@Param("authorName") String authorName);

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN BookCategory bc ON b.bookId = bc.book.bookId " +
            "JOIN Category c ON bc.category.categoryId = c.categoryId " +
            "WHERE LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    List<Book> findByCategory(@Param("categoryName") String categoryName);

    List<Book> findByAvailableCopiesBetween(int start, int end);
}
