package quickstart.admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
