CREATE TABLE authors (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(150) NOT NULL,
                         biography TEXT
);

CREATE TABLE books (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       isbn VARCHAR(20) NOT NULL UNIQUE,
                       published_year INTEGER,
                       available BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE book_authors (
                              book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
                              author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
                              PRIMARY KEY (book_id, author_id)
);

CREATE TABLE members (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(150) NOT NULL,
                         email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE borrowing_records (
                                   id BIGSERIAL PRIMARY KEY,
                                   book_id BIGINT NOT NULL REFERENCES books(id),
                                   member_id BIGINT NOT NULL REFERENCES members(id),
                                   borrowed_at DATE NOT NULL DEFAULT CURRENT_DATE,
                                   due_date DATE NOT NULL,
                                   returned_at DATE,
                                   CONSTRAINT valid_borrowing_dates CHECK (due_date >= borrowed_at),
                                   CONSTRAINT valid_return_date CHECK (
                                       returned_at IS NULL OR returned_at >= borrowed_at
                                       )
);

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_borrowings_member ON borrowing_records(member_id);
CREATE INDEX idx_borrowings_book ON borrowing_records(book_id);

CREATE UNIQUE INDEX idx_one_active_borrowing_per_book
    ON borrowing_records(book_id)
    WHERE returned_at IS NULL;