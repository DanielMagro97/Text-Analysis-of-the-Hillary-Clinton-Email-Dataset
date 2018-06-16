import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

// This class adapted from the code available at http://www.sqlitetutorial.net/sqlite-java/select/
public class EmailReader {

    public static ArrayList<Email> readEmails(){

        // The SQL statement which will be executed on the database of emails
        String sql = "SELECT MetadataTo, MetadataFrom, ExtractedSubject, ExtractedBodyText FROM Emails";

        // ArrayList which will store all the emails in the database
        ArrayList<Email> emails = new ArrayList<>();

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            // Loop through every entry in the database
            while (rs.next()) {
                String emailText = rs.getString("ExtractedSubject") + " " + rs.getString("ExtractedBodyText");

                String to = rs.getString("MetadataTo");
                String from = rs.getString("MetadataFrom");
                ArrayList<String> text = new ArrayList<>(Arrays.asList(emailText.split("\\s")));

                Email email = new Email(to, from, text);
                emails.add(email);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return emails;
    }

    private static Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:database.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
