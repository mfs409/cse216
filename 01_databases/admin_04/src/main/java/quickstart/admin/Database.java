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

    // #region add_drop
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
    // #endregion add_drop
}
