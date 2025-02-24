CREATE TYPE user_role AS ENUM ('ROLE_ADMIN', 'ROLE_USER', 'ROLE_MODERATOR');

CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role user_role NOT NULL
);

CREATE TABLE user_profile (
                              user_profile_id SERIAL PRIMARY KEY,
                              first_name VARCHAR(50) NOT NULL,
                              last_name VARCHAR(50) NOT NULL,
                              phone TEXT NOT NULL,
                              address VARCHAR(200) NOT NULL,
                              user_id INTEGER NOT NULL UNIQUE,
                              CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE author (
                        author_id SERIAL PRIMARY KEY,
                        first_name VARCHAR(50) NOT NULL,
                        last_name VARCHAR(50) NOT NULL,
                        biography TEXT NOT NULL
);

CREATE TABLE book (
                      book_id SERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT NOT NULL,
                      available_copies INTEGER NOT NULL CHECK (available_copies >= 0),
                      total_copies INTEGER NOT NULL CHECK (total_copies >= 0 AND available_copies <= total_copies)
);

CREATE TABLE category (
                          category_id SERIAL PRIMARY KEY,
                          category_name VARCHAR(50) NOT NULL UNIQUE,
                          description VARCHAR(50) NOT NULL
);

CREATE TABLE loan (
                      loan_id SERIAL PRIMARY KEY,
                      status VARCHAR(10) NOT NULL CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE')),
                      loan_date TIMESTAMP NOT NULL,
                      return_date TIMESTAMP NOT NULL,
                      user_id INTEGER NOT NULL,
                      book_id INTEGER NOT NULL,
                      CONSTRAINT fk_loan_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                      CONSTRAINT fk_loan_book FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE
);

CREATE TABLE reservation (
                             reservation_id SERIAL PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             book_id INTEGER NOT NULL,
                             reservation_time TIMESTAMP NOT NULL,
                             status VARCHAR(10) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED')),
                             CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                             CONSTRAINT fk_reservation_book FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE
);

CREATE TABLE review (
                        review_id SERIAL PRIMARY KEY,
                        user_id INTEGER NOT NULL,
                        book_id INTEGER NOT NULL,
                        comment VARCHAR(500) NOT NULL,
                        rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 10),
                        created_at TIMESTAMP NOT NULL,
                        CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
                        CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE
);

CREATE TABLE book_author (
                             book_id INTEGER NOT NULL,
                             author_id INTEGER NOT NULL,
                             PRIMARY KEY (book_id, author_id),
                             CONSTRAINT fk_book_author_book FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE,
                             CONSTRAINT fk_book_author_author FOREIGN KEY (author_id) REFERENCES author(author_id) ON DELETE CASCADE
);

CREATE TABLE book_category (
                               book_id INTEGER NOT NULL,
                               category_id INTEGER NOT NULL,
                               PRIMARY KEY (book_id, category_id),
                               CONSTRAINT fk_book_category_book FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE,
                               CONSTRAINT fk_book_category_category FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE CASCADE
);
