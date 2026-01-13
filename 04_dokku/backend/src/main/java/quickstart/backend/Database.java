package quickstart.backend;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database has all our logic for connecting to and interacting with SQLite
 *
 * NB: Since the backend is concurrent, this class needs to be thread-safe,
 * achieved by making all methods "synchronized".
 */
public class Database implements AutoCloseable {
    // load the sqlite-JDBC driver using the current class loader
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** A connection to a SQLite db, or null */
    private Connection conn;

    /**
     * Use dbStr to create a connection to a database, and stores it in the
     * constructed Database object
     * 
     * @param dbStr the connection string for the database
     * @throws SQLException if a connection cannot be created
     */
    public Database(String dbStr) throws SQLException {
        String jdbcUrl = null, username = null, password = null;
        try {
            java.net.URI dbUri = new java.net.URI(dbStr);
            jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                    dbUri.getHost(), dbUri.getPort(), dbUri.getPath());
            String[] uname_pword = dbUri.getUserInfo().split(":");
            username = uname_pword[0];
            password = uname_pword[1];
        } catch (java.net.URISyntaxException e) {
            throw new RuntimeException("Critical problem parsing dbStr; cannot create postgresql connection.", e);
        }

        // Connect to the database or fail
        conn = DriverManager.getConnection(jdbcUrl, username, password);
        if (conn == null) {
            throw new RuntimeException("Error: conn==null. (DriverManager.getConnection() returned a null object?)");
        }
    }

    /**
     * Close the current connection to the database, if one exists. The
     * connection will always be null after this call, even if an error occurred
     * during the closing operation.
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
     * PersonShort is a Java object with just the data we want to return when
     * getting a list of all people
     */
    public static record PersonShort(int id, String name) {
    }

    /**
     * Get a list of all people in the database
     *
     * @return A List with zero or more PersonShort objects
     *
     * @throws SQLException on any error
     */
    public synchronized List<PersonShort> getAllPerson() throws SQLException {
        try (var ps = conn.prepareStatement("SELECT id, name FROM tblPerson ORDER BY name;");
                var rs = ps.executeQuery();) {
            var results = new ArrayList<PersonShort>();
            while (rs.next()) {
                results.add(new PersonShort(rs.getInt("id"), rs.getString("name")));
            }
            return results;
        }
    }

    /** Person is a Java object with all the data from a row of tblPerson */
    public static record Person(int id, String email, String name) {
    }

    /**
     * Look up a user by their email address, to support start-of-session
     * authentication
     */
    public synchronized Person getPersonByEmail(String email) throws SQLException {
        try (var stmt = conn.prepareStatement("SELECT * FROM tblPerson WHERE email = ?;")) {
            stmt.setString(1, email);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Person(rs.getInt("id"), rs.getString("email"), rs.getString("name"));
                }
                return null;
            }
        }
    }

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
    public synchronized Person getOnePerson(int id) throws SQLException {
        try (var stmt = conn.prepareStatement("SELECT * FROM tblPerson WHERE id = ?;")) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Person(rs.getInt("id"), rs.getString("email"), rs.getString("name"));
                }
                return null;
            }
        }
    }

    /**
     * NameChangeRequest is a Java object containing the contents of a request to
     * change a person's name
     */
    public static record NameChangeRequest(String name) {
        /**
         * Verify that the name matches some basic length requirements
         */
        void validate() {
            if (name == null || name.length() < 1 || name.length() > 50)
                throw new RuntimeException("Invalid name");
        }
    }

    /**
     * Update a person's name
     *
     * @param req The request, as a NameChangeRequest
     *
     * @throws SQLException     If the person cannot be updated
     * @throws RuntimeException If the provided data is invalid
     */
    public synchronized void updatePersonName(int id, NameChangeRequest req) throws SQLException, RuntimeException {
        req.validate();
        try (var stmt = conn.prepareStatement("UPDATE tblPerson SET name = ? WHERE id = ?;")) {
            stmt.setString(1, req.name);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * NewMessageRequest is a java object containing the contents of a request
     * to create a new Message
     */
    public static record NewMessageRequest(String subject, String details) {
        /**
         * Verify that the message subject and body meet some basic requirements
         * about length
         */
        void validate() {
            if (subject == null || subject.length() < 1 || subject.length() > 50)
                throw new RuntimeException("Invalid subject");
            if (details == null || details.length() < 1 || details.length() > 500)
                throw new RuntimeException("Invalid details");
        }
    }

    /**
     * Create a new message
     *
     * @param req The request, as a NewMessageRequest
     *
     * @throws SQLException     If the message cannot be created
     * @throws RuntimeException If the provided data is invalid
     */
    public synchronized long insertMessage(NewMessageRequest req, int creatorId) throws SQLException, RuntimeException {
        req.validate();
        try (
                var stmt = conn.prepareStatement("""
                        INSERT INTO tblMessage
                            (subject, details, as_of, creatorId)
                        VALUES (?, ?, ?, ?);
                        """,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, req.subject);
            stmt.setString(2, req.details);
            stmt.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            stmt.setInt(4, creatorId);
            stmt.executeUpdate();
            try (var rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return -1;
    }

    /**
     * MessageShort is a Java object with just the data we want to return when
     * getting a list of all messages
     */
    public static record MessageShort(int id, String subject, Date as_of) {
    }

    /**
     * Get a list of all messages in the database
     *
     * @return a List with zero or more MessageShort objects
     *
     * @throws SQLException on any error
     */
    public synchronized List<MessageShort> getAllMessage() throws SQLException {
        var results = new ArrayList<MessageShort>();
        try (var ps = conn.prepareStatement("SELECT * FROM viewMessage ORDER BY as_of DESC;");
                var rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(new MessageShort(rs.getInt("id"), rs.getString("subject"), rs.getDate("as_of")));
            }
            return results;
        }
    }

    /** Message is a Java object with all the data from a row of tblMessage */
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
    public synchronized Message getOneMessage(int id) throws SQLException {
        try (var stmt = conn.prepareStatement("SELECT * FROM viewMessage WHERE id = ?;")) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Message(rs.getInt("id"), rs.getString("subject"), rs.getString("details"),
                            rs.getDate("as_of"), rs.getInt("creatorId"), rs.getString("email"), rs.getString("name"));
                }
            }
            return null;
        }
    }

    /**
     * UpdateMessageRequest is a java object containing the contents of a request
     * to update a Message
     */
    public static record UpdateMessageRequest(String details) {
        /**
         * Verify that the message body meets some basic legth requirements
         *
         * NB: We don't have an easy way to validate that creatorId is valid, so
         * we will count on SQL to get that right
         */
        void validate() {
            if (details == null || details.length() < 1 || details.length() > 500)
                throw new RuntimeException("Invalid details");
        }
    }

    /**
     * Update a message in the database
     *
     * @param req The request, as an UpdateMessageRequest
     *
     * @throws SQLException     If the message cannot be updated
     * @throws RuntimeException If the provided data is invalid
     */
    public synchronized void updateMessage(int id, UpdateMessageRequest req, int creatorId)
            throws SQLException, RuntimeException {
        req.validate();
        try (var stmt = conn.prepareStatement("""
                UPDATE tblMessage
                SET
                  details = ?,
                  as_of = ?
                WHERE
                  id = ? AND
                  creatorId = ?;
                """);) {
            stmt.setString(1, req.details);
            stmt.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
            stmt.setInt(3, id);
            stmt.setInt(4, creatorId);
            stmt.executeUpdate();
        }
    }

    /**
     * Delete a message
     *
     * @param id        The Id of the message to delete
     * @param creatorId The creatorId, so we can ensure the user is allowed to
     *                  delete
     *
     * @throws SQLException If the message cannot be deleted
     */
    public synchronized void deleteMessage(int id, int creatorId) throws SQLException {
        try (var stmt = conn.prepareStatement("DELETE FROM tblMessage WHERE id = ? and creatorId = ?");) {
            stmt.setInt(1, id);
            stmt.setInt(2, creatorId);
            stmt.executeUpdate();
        }
    }
}
