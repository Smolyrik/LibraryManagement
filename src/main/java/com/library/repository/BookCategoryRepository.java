package com.library.repository;

import com.library.entity.BookCategory;
import com.library.entity.BookCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCategoryRepository extends JpaRepository<BookCategory, BookCategoryId> {
}
