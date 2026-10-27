# Library Management System Using Java and Oracle SQL

## Project Description
A simple Library Management System developed using Java, Oracle Database, and JDBC. The application manages book borrowing and returns, records issue and due dates, calculates overdue fines, and displays books currently borrowed by students.

## Features
- Issue books to students.
- Record book issue dates and due dates.
- Record actual book return dates.
- Calculate overdue fines at ₹5 per day.
- Display books currently borrowed by a student.
- View all borrowing records.
- Store borrowing information in an Oracle Database.

## Technologies Used
- **Java** – Application logic
- **Oracle SQL** – Database management
- **JDBC** – Java Database Connectivity
- **VS Code** – Development environment

## Database Structure
The `BORROW_RECORDS` table stores the following information:

| Column | Description |
|---|---|
| `ID` | Unique borrowing record ID |
| `STUDENT_NAME` | Name of the student |
| `BOOK_NAME` | Name of the book |
| `ISSUE_DATE` | Date the book was issued |
| `DUE_DATE` | Expected return date |
| `RETURN_DATE` | Actual return date; NULL if not returned |

## Fine Calculation
The fine is calculated at ₹5 for every day after the due date.

**Formula:**

Fine = Overdue Days × ₹5

If a book is returned 3 days late, the fine is ₹15. No fine is charged when the book is returned on or before the due date.

## Setup Instructions

1. Install Java JDK.
2. Install Oracle Database and create a database user.
3. Download the Oracle JDBC driver (`ojdbc` JAR file).
4. Add the JDBC driver to your Java project's classpath.
5. Create the `BORROW_RECORDS` table in Oracle Database.
6. Update the JDBC connection URL, username, and password in the Java source code.
7. Compile and run the Java application using VS Code.

## JDBC Connection Example

```java
String url = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
String user = "SYSTEM";
String password = "your_password";
```

Update the URL and credentials to match your Oracle Database configuration.

## Fine Print
This is an educational project demonstrating Java database connectivity, SQL operations, and library borrowing management. Borrowing records are stored in the database, so they persist after the application closes.

## Author
Student Project – Java and Database Management
