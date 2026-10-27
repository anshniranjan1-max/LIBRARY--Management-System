import java.sql.*;
import java.util.Scanner;

public class LibraryApplication {
    static final String URL = "jdbc:mysql://localhost:3306/library_db";
    static final String USER = "root";
    static final String PASSWORD = "your_mysql_password";

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to Library Database!");

            while (true) {
                System.out.println("\n--- LIBRARY MANAGEMENT ---");
                System.out.println("1. Issue Book");
                System.out.println("2. Return Book");
                System.out.println("3. Display Student's Borrowed Books");
                System.out.println("4. Display All Records");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        issueBook(con);
                        break;
                    case 2:
                        returnBook(con);
                        break;
                    case 3:
                        displayBorrowed(con);
                        break;
                    case 4:
                        displayAll(con);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    static void issueBook(Connection con) throws SQLException {
        System.out.print("Enter student name: ");
        String student = sc.nextLine();

        System.out.print("Enter book name: ");
        String book = sc.nextLine();

        System.out.print("Enter issue date (YYYY-MM-DD): ");
        Date issue = Date.valueOf(sc.nextLine());

        System.out.print("Enter due date (YYYY-MM-DD): ");
        Date due = Date.valueOf(sc.nextLine());

        if (due.before(issue)) {
            System.out.println("Due date cannot be before issue date.");
            return;
        }

        String check = "SELECT id FROM borrow_records " +
                "WHERE student_name=? AND book_name=? AND return_date IS NULL";

        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setString(1, student);
            ps.setString(2, book);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("This student has not returned this book yet.");
                    return;
                }
            }
        }

        String sql = "INSERT INTO borrow_records " +
                "(student_name, book_name, issue_date, due_date) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, student);
            ps.setString(2, book);
            ps.setDate(3, issue);
            ps.setDate(4, due);
            ps.executeUpdate();
            System.out.println("Book issued successfully!");
        }
    }

    static void returnBook(Connection con) throws SQLException {
        System.out.print("Enter student name: ");
        String student = sc.nextLine();

        System.out.print("Enter book name: ");
        String book = sc.nextLine();

        System.out.print("Enter actual return date (YYYY-MM-DD): ");
        Date returned = Date.valueOf(sc.nextLine());

        String sql = "SELECT id, issue_date, due_date FROM borrow_records " +
                "WHERE student_name=? AND book_name=? AND return_date IS NULL " +
                "ORDER BY id DESC LIMIT 1";

        int id;
        Date issue, due;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, student);
            ps.setString(2, book);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No active borrowing record found.");
                    return;
                }

                id = rs.getInt("id");
                issue = rs.getDate("issue_date");
                due = rs.getDate("due_date");
            }
        }

        if (returned.before(issue)) {
            System.out.println("Return date cannot be before issue date.");
            return;
        }

        String update = "UPDATE borrow_records SET return_date=? WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(update)) {
            ps.setDate(1, returned);
            ps.setInt(2, id);
            ps.executeUpdate();
        }

        long overdueDays = Math.max(0,
                (returned.getTime() - due.getTime()) / (1000L * 60 * 60 * 24));

        System.out.println("Book returned successfully!");
        System.out.println("Overdue days: " + overdueDays);
        System.out.println("Fine: Rs. " + (overdueDays * 5));
    }

    static void displayBorrowed(Connection con) throws SQLException {
        System.out.print("Enter student name: ");
        String student = sc.nextLine();

        String sql = "SELECT *, GREATEST(DATEDIFF(CURDATE(), due_date), 0) * 5 " +
                "AS fine FROM borrow_records " +
                "WHERE student_name=? AND return_date IS NULL";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, student);

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    System.out.println("\nBook: " + rs.getString("book_name"));
                    System.out.println("Issue date: " + rs.getDate("issue_date"));
                    System.out.println("Due date: " + rs.getDate("due_date"));
                    System.out.println("Fine so far: Rs. " + rs.getInt("fine"));
                }

                if (!found) {
                    System.out.println("No books currently borrowed by this student.");
                }
            }
        }
    }

    static void displayAll(Connection con) throws SQLException {
        String sql = "SELECT *, GREATEST(DATEDIFF(" +
                "COALESCE(return_date, CURDATE()), due_date), 0) * 5 AS fine " +
                "FROM borrow_records ORDER BY id";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println("\nRecord ID: " + rs.getInt("id"));
                System.out.println("Student: " + rs.getString("student_name"));
                System.out.println("Book: " + rs.getString("book_name"));
                System.out.println("Issue date: " + rs.getDate("issue_date"));
                System.out.println("Due date: " + rs.getDate("due_date"));
                System.out.println("Return date: " + rs.getDate("return_date"));
                System.out.println("Fine: Rs. " + rs.getInt("fine"));
                System.out.println("-------------------------");
            }
        }
    }
}
