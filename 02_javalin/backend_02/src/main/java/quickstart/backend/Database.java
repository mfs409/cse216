package quickstart.backend;

import java.sql.Connection;
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
            Class.forName("org.sqlite.JDBC");
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /** A connection to a SQLite db, or null */
    private Connection conn;

    /**
     * Use dbFile to create a connection to a database, and stores it in the
     * constructed Database object
     * 
     * @param dbFile the connection string for the database
     * @throws SQLException if a connection cannot be created
     */
    public Database(String dbFile) throws SQLException {
        if (dbFile == null)
            throw new RuntimeException("Insufficient information to connect to database. Bye.");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        // NB: SQLite is odd: we need to opt-in for referential integrity
        try (var ps = conn.prepareStatement("PRAGMA foreign_keys = ON;")) {
            ps.execute();
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
}