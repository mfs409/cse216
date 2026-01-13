package quickstart.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * App lets us manage the database schema and the data in it.
 */
public class App {
    public static void main(String[] argv) {
        // get the SQLite configuration from environment variables;
        String dbFile = System.getenv("DB_FILE");
        // #region envar
        String dbUrl = System.getenv("DATABASE_URL");
        // #endregion envar
        System.out.println("Using the following environment variables:");
        System.out.println("-".repeat(45));
        System.out.println("  DB_FILE=" + dbFile);
        // #region envar_report
        System.out.println("  DATABASE_URL=" + dbUrl);
        // #endregion envar_report
        System.out.println("-".repeat(45));

        // #region validate
        if (dbFile == null && dbUrl == null) {
            // #endregion validate
            // insufficient information to connect
            System.err.println("Insufficient information to connect. Bye.");
            return;
        }

        // Get a fully-configured connection to the database, or exit immediately
        // #region db_construct
        try (Database db = new Database(dbUrl == null, (dbUrl == null ? dbFile : dbUrl))) {
            // #endregion db_construct
            // Start reading requests and processing them
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                switch (prompt(in)) {
                    case "?": // help
                        menu();
                        break;
                    case "q": // quit
                        return;
                    case "C": // create tables and views
                        db.createTables();
                        db.createViews();
                        break;
                    case "D": // drop tables and views
                        db.dropViews();
                        db.dropTables();
                        break;
                    case "1p": // query for one person row
                        var person = db.getOnePerson(getInt(in, "Enter the person ID"));
                        if (person != null) {
                            System.out.println(" " + person.id() + " | " + person.email() + " | " + person.name());
                        }
                        break;
                    case "*p": // query for all person rows
                        System.out.println("  tblPerson");
                        System.out.println("  -------------------------");
                        for (var row : db.getAllPerson()) {
                            System.out.println(" " + row.id() + " | " + row.email() + " | " + row.name());
                        }
                        break;
                    case "-p": // delete a person
                        db.deletePerson(getInt(in, "Enter the person ID"));
                        break;
                    case "+p": // insert a person
                        int newPid = db.insertPerson(
                                getString(in, "Enter the email"),
                                getString(in, "Enter the name"));
                        System.out.println("id of newly inserted person: " + newPid);
                        break;
                    case "~p": // update a person
                        db.updatePerson(
                                getInt(in, "Enter the person ID"),
                                getString(in, "Enter the new email"),
                                getString(in, "Enter the new name"));
                        break;
                    case "1m": // query for one message row
                        var msg = db.getOneMessage(getInt(in, "Enter the message ID"));
                        if (msg != null) {
                            System.out.println(" " + msg.id() + " | " + msg.subject() + " | " + msg.details() + " | "
                                    + new java.util.Date(msg.as_of().getTime()) + " | " + msg.creatorId() + " | "
                                    + msg.email() + " | " + msg.name());
                        }
                        break;
                    case "*m": // query for all message rows
                        System.out.println("  tblMessage");
                        System.out.println("  -------------------------");
                        for (var row : db.getAllMessage()) {
                            System.out.println(" " + row.id() + " | " + row.subject() + " | " + row.details() + " | "
                                    + new java.util.Date(row.as_of().getTime()) + " | " + row.creatorId() + " | "
                                    + row.email() + " | " + row.name());
                        }
                        break;
                    case "-m": // delete a message
                        db.deleteMessage(getInt(in, "Enter the message ID"));
                        break;
                    case "+m": // insert a message
                        db.insertMessage(getString(in, "Enter the subject"),
                                getString(in, "Enter the message"),
                                getInt(in, "Enter the person ID"));
                        break;
                    case "~m": // update a message
                        db.updateMessage(getInt(in, "Enter the message ID"),
                                getString(in, "Enter the subject"),
                                getString(in, "Enter the message"));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** All of the valid menu options of the program */
    static List<String> menuOptions = Arrays.asList("C", "D", "1p", "*p", "-p", "+p", "~p", "1m", "*m", "-m", "+m",
            "~m", "q", "?");

    /** Print the menu for the program */
    static void menu() {
        System.out.println("Main Menu");
        System.out.println("  [C] Create tables and views");
        System.out.println("  [D] Drop tables and views");
        System.out.println("  [1p] Query for a person");
        System.out.println("  [*p] Query for all person rows");
        System.out.println("  [-p] Delete a person");
        System.out.println("  [+p] Insert a new person");
        System.out.println("  [~p] Update a person");
        System.out.println("  [1m] Query for a specific message");
        System.out.println("  [*m] Query for all message rows");
        System.out.println("  [-m] Delete a message");
        System.out.println("  [+m] Insert a new message");
        System.out.println("  [~m] Update a message");
        System.out.println("  [q] Quit Program");
        System.out.println("  [?] Help (this message)");
    }

    /**
     * Ask the user to enter a menu option; repeat until we get a valid option
     *
     * @param in A BufferedReader, for reading from the keyboard
     *
     * @return The chosen menu option
     */
    static String prompt(BufferedReader in) {
        // Create a set with the valid actions, so it's easy to get the user's
        // request
        var options = new HashSet<String>(menuOptions);

        // Repeat until a valid option is selected
        while (true) {
            System.out.print("[" + String.join(", ", options) + "] :> ");
            try {
                String action = in.readLine();
                if (options.contains(action)) {
                    return action;
                } else if (action == null) {
                    return "q";
                }
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            System.out.println("Invalid Command");
        }
    }

    /**
     * Ask the user to enter a String message
     *
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     *
     * @return The string that the user provided. May be "".
     */
    static String getString(BufferedReader in, String message) {
        try {
            System.out.print(message + " :> ");
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Ask the user to enter an integer
     *
     * @param in      A BufferedReader, for reading from the keyboard
     * @param message A message to display when asking for input
     *
     * @return The integer that the user provided. On error, it will be -1
     */
    static int getInt(BufferedReader in, String message) {
        try {
            System.out.print(message + " :> ");
            return Integer.parseInt(in.readLine());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
