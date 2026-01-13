package quickstart.backend;

import io.javalin.Javalin;
// #region import
import java.sql.SQLException;
import com.google.gson.*;
// #endregion import

/** A backend built with the Javalin framework */
public class App {
    public static void main(String[] args) {
        // get the port on which to listen. If this crashes the program, that's
        // fine... it means "configuration error".
        // #region envars1
        int port = Integer.parseInt(System.getenv("PORT"));
        String dbFile = System.getenv("DB_FILE");
        // #endregion envars1

        System.out.println("-".repeat(45));
        System.out.println("Using the following environment variables:");
        // #region envars2
        System.out.println("  PORT=" + port);
        System.out.println("  DB_FILE=" + dbFile);
        // #endregion envars2
        System.out.println("-".repeat(45));

        // Do some quick validation to ensure the port is in range
        if (port < 80 || port > 65535) {
            System.err.println("Error in environment configuration");
            return;
        }

        // #region dbconfig
        // Create the database interface and Gson object. We do this before
        // setting up the server, because failures will be fatal
        Database db;
        try {
            db = new Database(dbFile);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        // gson lets us easily turn objects into JSON
        // This date format works nicely with SQLite and PostgreSQL
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
        // #endregion dbconfig

        // Create the web server. This doesn't start it yet!
        var app = Javalin.create(config -> {
            // Attach a logger
            config.requestLogger.http((ctx, ms) -> {
                System.out.println("=".repeat(80));
                System.out.printf("%-6s%-8s%-25s%s%n", ctx.scheme(), ctx.method().name(), ctx.path(),
                        ctx.fullUrl());
                if (ctx.queryString() != null)
                    System.out.printf("query string:%s%n", ctx.queryString());
                if (ctx.body().length() > 0)
                    System.out.printf("request body:%s%n", ctx.body());
            });
        });

        // #region routes
        // All routes go here
        // Get a list of all the people in the system
        app.get("/people", ctx -> Routes.readPersonAll(ctx, db, gson));
        // #endregion routes

        // #region shutdown
        // The only way to stop the server is by pressing ctrl-c. At that point,
        // the server should try to clean up as best it can.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Try to shut down Javalin before the database, because the
            // database shouldn't shut down until it's 100% certain that no more
            // requests will be sent to it.
            try {
                System.out.println("Shutting down Javalin...");
                app.stop(); // Stops the Javalin instance gracefully
            } catch (Exception e) {
                e.printStackTrace();
            }
            // If Javalin didn't shut down nicely, and the Database shuts down,
            // then some Javalin threads might crash when they try to use a null
            // connection. Javalin shutdown failures are highly unlikely, and
            // almost impossible to solve, so once a Javalin shutdown has been
            // attempted, go ahead and try to shut down the database.
            try {
                System.out.println("Shutting down Database...");
                db.close();
                System.out.println("Done");
            } catch (Exception e) {
                // Database shutdown failures are almost impossible to solve,
                // too, so if this happens, the best thing to do is print a
                // message and return.
                e.printStackTrace();
            }
        }));
        // #endregion shutdown

        // This next line launches the server, so it can start receiving
        // requests. Note that main will return, but the server keeps running.
        app.start(port);
    }
}
