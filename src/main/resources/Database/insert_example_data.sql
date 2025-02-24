INSERT INTO users (username, email, password, role) VALUES
                                                        ('admin_user', 'admin@example.com', 'hashed_password_1', 'ROLE_ADMIN'),
                                                        ('regular_user', 'user@example.com', 'hashed_password_2', 'ROLE_USER'),
                                                        ('moderator_user', 'mod@example.com', 'hashed_password_3', 'ROLE_MODERATOR');


INSERT INTO user_profile (first_name, last_name, phone, address, user_id) VALUES
                                                                              ('Admin', 'User', '+12345678901', '123 Admin Street', 1),
                                                                              ('John', 'Doe', '+19876543210', '456 User Lane', 2),
                                                                              ('Jane', 'Smith', '+11223344556', '789 Mod Avenue', 3);


INSERT INTO author (first_name, last_name, biography) VALUES
                                                          ('George', 'Orwell', 'British writer, famous for 1984 and Animal Farm.'),
                                                          ('J.K.', 'Rowling', 'British author, best known for the Harry Potter series.'),
                                                          ('Leo', 'Tolstoy', 'Russian writer, known for War and Peace and Anna Karenina.');


INSERT INTO book (title, description, available_copies, total_copies) VALUES
                                                                          ('1984', 'Dystopian novel about totalitarianism.', 5, 10),
                                                                          ('Harry Potter and the Sorcerer''s Stone', 'First book in the Harry Potter series.', 7, 10),
                                                                          ('War and Peace', 'Historical novel by Leo Tolstoy.', 3, 5);


INSERT INTO category (category_name, description) VALUES
                                                      ('Fiction', 'Fictional books and novels.'),
                                                      ('Fantasy', 'Fantasy and magical books.'),
                                                      ('History', 'Books based on historical events.');


INSERT INTO book_author (book_id, author_id) VALUES
                                                 (1, 1),
                                                 (2, 2),
                                                 (3, 3);

INSERT INTO book_category (book_id, category_id) VALUES
                                                     (1, 1),
                                                     (2, 2),
                                                     (3, 3);

INSERT INTO loan (status, loan_date, return_date, user_id, book_id) VALUES
                                                                        ('ACTIVE', '2024-02-01 10:00:00', '2024-03-01 10:00:00', 2, 1),
                                                                        ('RETURNED', '2024-01-10 12:30:00', '2024-02-10 12:30:00', 3, 2);

INSERT INTO reservation (user_id, book_id, reservation_time, status) VALUES
                                                                         (2, 3, '2024-02-15 14:00:00', 'CONFIRMED'),
                                                                         (3, 1, '2024-02-16 09:30:00', 'PENDING');

INSERT INTO review (user_id, book_id, comment, rating, created_at) VALUES
                                                                       (2, 1, 'An eye-opening book about government surveillance.', 9, '2024-02-05 16:45:00'),
                                                                       (3, 2, 'A magical journey into the wizarding world.', 10, '2024-02-07 18:00:00');
