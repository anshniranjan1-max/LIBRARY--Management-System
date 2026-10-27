CREATE DATABASE library_db;
USE library_db;

CREATE TABLE borrow_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_name VARCHAR(100),
    book_name VARCHAR(150),
    issue_date DATE,
    due_date DATE,
    return_date DATE NULL
);

SHOW TABLES;
