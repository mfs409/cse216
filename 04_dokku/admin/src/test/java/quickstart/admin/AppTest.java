package quickstart.admin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import quickstart.admin.Database.Person;

/**
 * Unit test for simple App.
 * Creates a shared test db for all tests in initAll, and deletes it at the end
 * in tearDownAll.
 * To better isolate each test, the tables and views are dropped after each
 * test, and recreated (empty) before each test begins.
 * See https://docs.junit.org/current/user-guide/#writing-tests
 */
@Disabled("This class is part of a feature still in development")
// TODO: fixme
public class AppTest {

    /**
     * rather than mocking, we share among tests a newly constructed database object
     */
    static Database db = null;
    /**
     * the test db has its own filename based on system time; we track it so we can
     * delete it when finished
     */
    static String dbFileName = null;

    /**
     * Ensure that DB_FILE env var is provided, has 1 or more characters, then
     * create a db just for testing
     */
    @BeforeAll
    static void initAll() {
        String dbFile = System.getenv("DB_FILE");
        assertNotNull(dbFile, "Missing environment variable: DB_FILE.");
        assertFalse(dbFile.length() == 0, "DB_FILE should be 1 or more characters.");
        dbFileName = String.format("%s_junit_%d.db", dbFile, System.currentTimeMillis());
        try {
            assertDoesNotThrow(() -> db = new Database(dbFileName), "Exception thrown while creating test db.");
        } catch (Exception e) {
            System.err.println("ERROR: AppTest.initAll failed to establish test database connection.");
        }
    }

    /** Shuts down the database and deletes the file */
    @AfterAll
    static void tearDownAll() throws Exception {
        System.out.println("Cleaning up: Shutting down the test database.");
        if (db != null) {
            try {
                db.close();
            } catch (Exception e) {
                System.err.println("WARNING: problem closing test database.");
            }
        }
        if (dbFileName != null) {
            java.io.File dbFileToDelete = new File(dbFileName);
            if (dbFileToDelete.exists()) {
                System.out.println("Cleaning up: deleting " + dbFileName);
                if (dbFileToDelete.delete()) {
                    System.out.println("  Delete test database successful.");
                } else {
                    System.err.println("WARNING: could not delete test db " + dbFileName);
                }
            }
        }
    }

    /** Ensures before each test that db exists, and has fresh tables and views */
    @BeforeEach
    void initEach() {
        System.out.println("running initEach");
        assertNotNull(db);
        if (db != null) {
            assertDoesNotThrow(() -> db.createTables());
            assertDoesNotThrow(() -> db.createViews());
        }
    }

    /** Ensures after each test that all tables and views are dropped from db */
    @AfterEach
    void tearDownEach() {
        System.out.println("running tearDownEach");
        if (db != null) {
            assertDoesNotThrow(() -> db.dropViews());
            assertDoesNotThrow(() -> db.dropTables());
        }
    }

    /**
     * Foreign keys are essential for our apps correctness; this ensures they are on
     */
    @Test
    @DisplayName("Verify FOREIGN_KEYS are on.")
    public void givenDatabase_whenCreated_thenPragmaIsOn() throws SQLException {
        assertNotNull(db);
        assertTrue(db.getPragmaForeignKeysStatus(), "This application requires that FOREIGN_KEYS be on.");
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    @Disabled("For demonstration purposes")
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    /** The app expects env var DB_FILE to exist; this ensures it does. */
    @Test
    @DisplayName("Verify environment variable DB_FILE")
    public void checkEnv_DB_FILE() {
        String dbFile = System.getenv("DB_FILE");
        assertNotNull(dbFile, "Missing environment variable: DB_FILE.");
        assertTrue(dbFile.length() >= 1, "DB_FILE should be 1 or more characters.");
    }

    /**
     * Database::insertPerson should not be able to insert person with null email
     */
    @Test
    @DisplayName("insertPerson should throw if the email is null")
    public void givenPerson_whenEmailNull_thenRejectInsert() {
        assertNotNull(db);
        var ex = assertThrows(RuntimeException.class, () -> db.insertPerson(null, null));
        assertEquals("Invalid email address", ex.getMessage());
    }

    /**
     * Database::insertPerson should not be able to insert person with overly long
     * email
     */
    @Test
    @DisplayName("insertPerson should throw if the email is too long")
    public void givenPerson_whenEmailTooLong_thenRejectInsert() {
        assertNotNull(db);
        var ex = assertThrows(RuntimeException.class, () -> db.insertPerson(".".repeat(31), null));
        assertEquals("Invalid email address", ex.getMessage());
    }

    /** Should not be able to insert a new person with same email as another */
    @Test
    @DisplayName("insertPerson should throw if the email is already in use")
    public void givenPerson_whenAlreadyExists_thenRejectInsert() {
        assertNotNull(db);
        final String sEmail = "test@email.com", sName = "test person";
        assertDoesNotThrow(() -> db.insertPerson(sEmail, sName));
        var ex = assertThrows(SQLException.class, () -> db.insertPerson(sEmail, sName));
        assertEquals(
                "[SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: tblPerson.email)",
                ex.getMessage());
    }

    /**
     * Should not be able to update a person's email to one in use by another user
     */
    @Test
    @DisplayName("updatePerson should throw if the email is already in use")
    public void givenPerson_whenChangingEmailToOneInUse_thenRejectUpdate() {
        assertNotNull(db);
        final String sEmail1 = "test1@email.com", sName1 = "test person1";
        final String sEmail2 = "test2@email.com", sName2 = "test person2";
        final int idUser1 = assertDoesNotThrow(() -> {
            return db.insertPerson(sEmail1, sName1);
        });
        final int idUser2 = assertDoesNotThrow(() -> {
            return db.insertPerson(sEmail2, sName2);
        });
        assertNotEquals(idUser1, idUser2, "Two distinct users should not get the same id upon creation.");
        var ex = assertThrows(RuntimeException.class, () -> db.updatePerson(idUser2, sEmail1, sName2));
        assertEquals("Email already in use", ex.getMessage());
    }

    /**
     * Confirm a new user can be created, and getOnePerson retreives it correctly
     */
    @Test
    @DisplayName("getOnePerson should retreive the information for a newly created user")
    public void givenPerson_whenCreating_then_getOnePerson_should_match() {
        assertNotNull(db);
        final String sEmail1 = "test1@email.com", sName1 = "test person1";
        final int idUser1 = assertDoesNotThrow(() -> {
            return db.insertPerson(sEmail1, sName1);
        });
        Database.Person p = assertDoesNotThrow(() -> {
            return db.getOnePerson(idUser1);
        });
        assertNotNull(p, "Could not reteive new user by their id.");
        assertEquals(idUser1, p.id(), "getOneUser returned a user with an id different than expected");
        assertEquals(sEmail1, p.email());
        assertEquals(sName1, p.name());
    }

    /**
     * Confirm a new user can be created, and getAllPerson retreives it correctly
     */
    @Test
    @DisplayName("getAllPerson should retreive the information for a newly created user")
    public void givenPerson_whenCreating_then_getAllPerson_should_haveThem() {
        assertNotNull(db);
        final String sEmail1 = "test1@email.com", sName1 = "test person1";
        final int idUser1 = assertDoesNotThrow(() -> {
            return db.insertPerson(sEmail1, sName1);
        });
        java.util.List<Database.Person> allPeople = assertDoesNotThrow(() -> {
            return db.getAllPerson();
        });
        assertNotNull(allPeople);
        boolean foundUser = false;
        for (Person pEntry : allPeople) {
            if (pEntry.id() == idUser1 || sEmail1.equals(pEntry.email())) {
                foundUser = true;
                assertEquals(idUser1, pEntry.id());
                assertEquals(sEmail1, pEntry.email());
                assertEquals(sName1, pEntry.name());
            }
        }
        assertTrue(foundUser, "Did not find user in list returned by getAllPerson()");
    }

    /** Confirm a user can be deleted if they have no messages */
    @Test
    @DisplayName("deletePerson should remove an existing user if they have no messages")
    public void givenPerson_whenDeleting_thenSucceedWhenNoMessagesExist() {
        assertNotNull(db);
        final String sEmail1 = "test1@email.com", sName1 = "test person1";
        final int idUser1 = assertDoesNotThrow(() -> {
            return db.insertPerson(sEmail1, sName1);
        });
        Database.Person p = assertDoesNotThrow(() -> {
            return db.getOnePerson(idUser1);
        });
        assertNotNull(p);
        assertEquals(idUser1, p.id());
        assertDoesNotThrow(() -> db.deletePerson(idUser1));
        p = assertDoesNotThrow(() -> {
            return db.getOnePerson(idUser1);
        });
        assertNull(p);
        java.util.List<Database.Person> allPeople = assertDoesNotThrow(() -> {
            return db.getAllPerson();
        });
        for (Person pEntry : allPeople)
            assertNotEquals(pEntry.id(), idUser1);
    }

    /**
     * Confirm a user canNOT be deleted if they have messages associated with their
     * account
     */
    @Test
    @DisplayName("deletePerson should NOT remove an existing user if they have messages")
    public void givenPerson_whenDeleting_thenFailWhenMessagesExist() {
        assertNotNull(db);
        final String sEmail1 = "test1@email.com", sName1 = "test person1";
        final String sSubject = "test subject", sDetails = "test details";
        final int idUser1 = assertDoesNotThrow(() -> {
            return db.insertPerson(sEmail1, sName1);
        });
        Database.Person p = assertDoesNotThrow(() -> {
            return db.getOnePerson(idUser1);
        });
        assertNotNull(p);
        assertEquals(idUser1, p.id());
        assertDoesNotThrow(() -> db.insertMessage(sSubject, sDetails, idUser1));

        assertThrows(SQLException.class, () -> db.deletePerson(idUser1));
        p = assertDoesNotThrow(() -> {
            return db.getOnePerson(idUser1);
        });
        assertNotNull(p);
    }

    /** Should not be able to insert message with a creatorId not in user table */
    @Test
    @DisplayName("insertMessage should fail when associated creatorId is not in users table")
    public void givenMessage_whenCreatorIdDoesNotExistInUsers_thenRejectInsert() {
        assertNotNull(db);
        final String sSubject = "test subject", sDetails = "test details";
        java.util.List<Database.Person> allPeople = assertDoesNotThrow(() -> {
            return db.getAllPerson();
        });
        assertNotNull(allPeople);
        assertEquals(allPeople.size(), 0);
        var ex = assertThrows(SQLException.class, () -> db.insertMessage(sSubject, sDetails, 42));
        assertEquals(ex.getMessage(),
                "[SQLITE_CONSTRAINT_FOREIGNKEY] A foreign key constraint failed (FOREIGN KEY constraint failed)");
    }
}
