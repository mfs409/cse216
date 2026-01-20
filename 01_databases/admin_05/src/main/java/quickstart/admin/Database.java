package quickstart.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// #region new_imports
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
// #endregion new_imports
// #region import_date
import java.sql.Date;
// #endregion import_date

/**
 * Database has all the logic for connecting to and interacting with a database
 *
 * Note: This class is hard-coded for SQLite.
 */
public class Database implements AutoCloseable {
    // Load the sqlite-JDBC driver when the class is initialized
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** A connection to a SQLite db, or null */
    private Connection conn;

    /**
     * Use dbFile to create a connection to a database, and store it in the
     * constructed Database object
     *
     * @param dbFile the connection string for the database
     * @throws SQLException if a connection cannot be created
     */
    public Database(String dbFile) throws SQLException {
        // Connect to the database or fail
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        // SQLite is odd: we need to opt-in for referential integrity
        try (var stmt = conn.prepareStatement("PRAGMA foreign_keys = ON;")) {
            stmt.execute();
            getPragmaForeignKeysStatus();
        }
    }

    /**
     * If connection isn't null, verifies that referential integrity is enabled
     *
     * @return true if enabled, false otherwise
     */
    synchronized boolean getPragmaForeignKeysStatus() throws SQLException {
        if (conn != null) {
            try (var stmt = conn.prepareStatement("PRAGMA foreign_keys;");
                    var rs = stmt.executeQuery();) {
                if (rs.next()) {
                    int status = rs.getInt(1);
                    if (status == 1)
                        System.out.println("sqlite referential integrity enabled");
                    else
                        System.out.println("WARNING: sqlite referential integrity is DISABLED");
                    return status == 1;
                } else {
                    System.err.println("ERROR: did not get a result set for PRAGMA foreign_keys when expected 0 or 1.");
                }
            }
        }
        return false;
    }

    /**
     * Close the current connection to the database, if one exists.
     *
     * NB: The connection will always be null after this call, even if an
     * error occurred during the closing operation.
     */
    @Override
    public void close() throws Exception {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }

    /**
     * Create the database tables
     *
     * @throws SQLException if any table cannot be created
     */
    synchronized void createTables() throws SQLException {
        var createTblPerson = """
                CREATE TABLE tblPerson (
                    id INTEGER PRIMARY KEY,
                    email VARCHAR(30) NOT NULL UNIQUE COLLATE NOCASE,
                    name VARCHAR(50)
                );""";
        try (var ps = conn.prepareStatement(createTblPerson)) {
            ps.execute();
        }
        var createTblMessage = """
                CREATE TABLE tblMessage (
                    id INTEGER PRIMARY KEY,
                    subject VARCHAR(50) NOT NULL,
                    details VARCHAR(500) NOT NULL,
                    as_of DATE NOT NULL,
                    creatorId INTEGER,
                    FOREIGN KEY (creatorId) REFERENCES tblPerson(id)
                );""";
        try (var ps = conn.prepareStatement(createTblMessage)) {
            ps.execute();
        }
        System.out.println("Tables created successfully");
    }

    /**
     * Create the database views
     *
     * @throws SQLException if any view cannot be created
     */
    synchronized void createViews() throws SQLException {
        var createViewMessage = """
                CREATE VIEW viewMessage AS
                SELECT
                    tblMessage.id as id,
                    tblMessage.subject as subject,
                    tblMessage.details as details,
                    tblMessage.as_of as as_of,
                    tblMessage.creatorId as creatorId,
                    tblPerson.email as email,
                    tblPerson.name as name
                FROM tblMessage INNER JOIN tblPerson on
                    tblMessage.creatorId = tblPerson.id;""";
        try (var ps = conn.prepareStatement(createViewMessage)) {
            ps.execute();
        }
        System.out.println("Views created successfully");
    }

    /**
     * Remove all tables from the database
     *
     * @throws SQLException if any table cannot be dropped
     */
    synchronized void dropTables() throws SQLException {
        var dropTblMessage = "DROP TABLE tblMessage;";
        try (var ps = conn.prepareStatement(dropTblMessage)) {
            ps.execute();
        }
        var dropTblPerson = "DROP TABLE tblPerson;";
        try (var ps = conn.prepareStatement(dropTblPerson)) {
            ps.execute();
        }
        System.out.println("Tables dropped successfully");
    }

    /**
     * Remove all views from the database
     *
     * @throws SQLException if any view cannot be dropped
     */
    synchronized void dropViews() throws SQLException {
        var dropViewMessage = "DROP VIEW viewMessage;";
        try (var ps = conn.prepareStatement(dropViewMessage)) {
            ps.execute();
        }
        System.out.println("Views dropped successfully");
    }

    // #region validators
    /**
     * Perform lightweight validation of the provided email address.
     *
     * @param email The address to validate
     *
     * @throws RuntimeException if the address does not meet length requirements
     */
    private static synchronized void validateEmail(String email) throws RuntimeException {
        if (email == null || email.length() < 3 || email.length() > 30) {
            throw new RuntimeException("Invalid email address");
        }
    }

    /**
     * Perform lightweight validation of the provided display name
     *
     * @param name The name to validate
     * @throws RuntimeException if the address does not meet length requirements
     */
    private static synchronized void validateName(String name) throws RuntimeException {
        if (name == null || name.length() < 1 || name.length() > 50) {
            throw new RuntimeException("Invalid name");
        }
    }
    // #endregion validators

    // #region insert_person
    /**
     * Create a new Person in the database. Note that uniqueness of email
     * addresses is enforced by the database itself, which lets this code remain
     * clean and simple.
     *
     * @param email The new person's email address
     * @param name  The new person's name
     *
     * @throws SQLException     If the person cannot be created
     * @throws RuntimeException If the provided data is invalid
     * @return the id of the new row upon success, Integer.MIN_VALUE on failure
     */
    synchronized int insertPerson(String email, String name) throws SQLException, RuntimeException {
        // Be sure to validate the email and name!
        Database.validateEmail(email);
        Database.validateName(name);

        // NB: The PreparedStatement uses a second parameter to request the row
        // Id of the created row
        try (var stmt = conn.prepareStatement("INSERT INTO tblPerson (email, name) VALUES (?, ?) RETURNING id;");) {
            stmt.setString(1, email);
            stmt.setString(2, name);
            if (stmt.execute()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    if (rs.next()) { // retrieves the id of the new row
                        return rs.getInt(1);
                    }
                }
            }
        }
        return Integer.MIN_VALUE;
    }
    // #endregion insert_person

    // #region delete_person
    /**
     * Delete a person from the database
     *
     * @param id The Id of the person to delete
     *
     * @throws SQLException If the person cannot be deleted
     */
    synchronized void deletePerson(int id) throws SQLException {
        try (var stmt = conn.prepareStatement("DELETE FROM tblPerson WHERE id = ?");) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    // #endregion delete_person

    // #region update_person
    /**
     * Update a person's data in the database
     *
     * @apiNote If this function throws a SQLException, the connection
     *          autocommit setting can be in an undefined or invalid state.
     *          Callers will not be able to recover in that case, so they should
     *          treat SQLExceptions as fatal.
     *
     * @param id    The Id of the person to update
     * @param email The email (might be the same as before)
     * @param name  The name (might be the same as before)
     *
     * @throws SQLException     If the person cannot be updated
     * @throws RuntimeException If the provided data is invalid
     */
    synchronized void updatePerson(int id, String email, String name) throws SQLException, RuntimeException {
        Database.validateEmail(email);
        Database.validateName(name);

        // We could rely on the database to enforce email uniqueness,
        // (and are, through the `UNIQUE` constraint on `email`), but we instead
        // use a *transaction* to check the uniqueness of the address
        // before updating so we can provide nicer error messages
        conn.setAutoCommit(false);

        try (var stmt = conn.prepareStatement("SELECT * FROM tblPerson WHERE email = ? and id <> ?");) {
            stmt.setString(1, email);
            stmt.setInt(2, id);
            try (var rs = stmt.executeQuery();) {
                if (rs.next()) {
                    conn.commit();
                    conn.setAutoCommit(true); // return to non-transaction mode
                    throw new RuntimeException("Email already in use");
                }
            }
        }
        try (var stmt = conn.prepareStatement("UPDATE tblPerson SET email = ?, name = ? WHERE id = ?;");) {
            stmt.setString(1, email);
            stmt.setString(2, name);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true); // return to non-transaction mode
        }
    }
    // #endregion update_person

    // #region record
    /** Person is a Java object that matches the contents of tblPerson */
    public static record Person(int id, String email, String name) {
    }
    // #endregion record

    // #region get
    /**
     * Get all data for a single person
     *
     * @param id The Id of the person to get
     *
     * @return a Person object representing the data that was retrieved from the
     *         database, or null if no person was found
     *
     * @throws SQLException on any error
     */
    synchronized Person getOnePerson(int id) throws SQLException {
        try (var stmt = conn.prepareStatement("SELECT * FROM tblPerson WHERE id = ?;");) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return new Person(rs.getInt("id"), rs.getString("email"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    /**
     * Get all data for all people in the database
     *
     * @return A List with zero or more Person objects
     *
     * @throws SQLException on any error
     */
    synchronized List<Person> getAllPerson() throws SQLException {
        try (var ps = conn.prepareStatement("SELECT * FROM tblPerson;");
                var rs = ps.executeQuery();) {
            var results = new ArrayList<Person>();
            while (rs.next()) {
                results.add(new Person(rs.getInt("id"), rs.getString("email"), rs.getString("name")));
            }
            return results;
        }
    }
    // #endregion get

    // #region tbl_message
    /**
     * Perform lightweight validation of the provided message subject
     *
     * @param subject The subject text to validate
     *
     * @throws RuntimeException if the subject does not meet length requirements
     */
    private static synchronized void validateSubject(String subject) throws RuntimeException {
        if (subject == null || subject.length() < 1 || subject.length() > 50) {
            throw new RuntimeException("Invalid subject");
        }
    }

    /**
     * Perform lightweight validation of the provided message details
     *
     * @param details The details text to validate
     *
     * @throws RuntimeException if the details do not meet length requirements
     */
    private static synchronized void validateDetails(String details) throws RuntimeException {
        if (details == null || details.length() < 1 || details.length() > 500) {
            throw new RuntimeException("Invalid details");
        }
    }

    /**
     * Create a new message in the database
     *
     * @param subject   The subject
     * @param details   The details
     * @param creatorId The Id of the user creating the message
     *
     * @throws SQLException     If the person cannot be created
     * @throws RuntimeException If the provided data is invalid
     */
    synchronized void insertMessage(String subject, String details, int creatorId)
            throws SQLException, RuntimeException {
        Database.validateSubject(subject);
        Database.validateDetails(details);
        try (var stmt = conn.prepareStatement(
                "INSERT INTO tblMessage (subject, details, as_of, creatorId) VALUES (?, ?, ?, ?);");) {
            stmt.setString(1, subject);
            stmt.setString(2, details);
            stmt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            stmt.setInt(4, creatorId);
            stmt.executeUpdate();
        }
    }

    /**
     * Update a message in the database
     *
     * @param id      The Id of the message to update
     * @param subject The subject (might be the same as before)
     * @param details The details (might be the same as before)
     *
     * @throws SQLException     If the message cannot be updated
     * @throws RuntimeException If the provided data is invalid
     */
    synchronized void updateMessage(int id, String subject, String details) throws SQLException, RuntimeException {
        Database.validateSubject(subject);
        Database.validateDetails(details);
        try (var stmt = conn
                .prepareStatement("UPDATE tblMessage SET subject = ?, details = ?, as_of = ? WHERE id = ?;");) {
            stmt.setString(1, subject);
            stmt.setString(2, details);
            stmt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            stmt.setInt(4, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a message from the database
     *
     * @param id The Id of the message to delete
     *
     * @throws SQLException If the message cannot be deleted
     */
    synchronized void deleteMessage(int id) throws SQLException {
        try (var stmt = conn.prepareStatement("DELETE FROM tblMessage WHERE id = ?");) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /** Message is a Java object that matches the contents of viewMessage */
    public static record Message(int id, String subject, String details, Date as_of, int creatorId, String email,
            String name) {
    }

    /**
     * Get all data for a single message
     *
     * @param id The Id of the message to get
     *
     * @return a Message object representing the data that was retrieved from
     *         the database, or null if no message was found
     *
     * @throws SQLException on any error
     */
    synchronized Message getOneMessage(int id) throws SQLException {
        try (var stmt = conn.prepareStatement("SELECT * FROM viewMessage WHERE id = ?;");) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery();) {
                if (rs.next()) {
                    return new Message(rs.getInt("id"), rs.getString("subject"), rs.getString("details"),
                            rs.getDate("as_of"), rs.getInt("creatorId"), rs.getString("email"), rs.getString("name"));
                }
            }
        }
        return null;
    }

    /**
     * Get all data for all messages in the database
     *
     * @return a List with zero or more message objects
     *
     * @throws SQLException on any error
     */
    synchronized List<Message> getAllMessage() throws SQLException {
        try (var stmt = conn.prepareStatement("SELECT * FROM viewMessage;");
                var rs = stmt.executeQuery();) {
            var results = new ArrayList<Message>();
            while (rs.next()) {
                results.add(new Message(rs.getInt("id"), rs.getString("subject"), rs.getString("details"),
                        rs.getDate("as_of"), rs.getInt("creatorId"), rs.getString("email"), rs.getString("name")));
            }
            return results;
        }
    }
    // #endregion tbl_message
}
