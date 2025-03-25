CREATE TABLE users
(
    id      SERIAL PRIMARY KEY,
    name    TEXT,
    surname TEXT,
    behaviour TEXT,
    book_id int
);


CREATE TABLE books
(
    id   SERIAL PRIMARY KEY,
    title TEXT,
    author_id INT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users (id)
);

ALTER TABLE users
    ADD CONSTRAINT FK_BOOKS FOREIGN KEY (book_id) REFERENCES books (id);