package quickstart.backend;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
// #region import
import io.javalin.http.staticfiles.Location;
// #endregion import
import java.sql.SQLException;
import com.google.gson.*;


/** A backend built with the Javalin framework */
public class App {
    public static void main(String[] args) {
        // get the port on which to listen. If this crashes the program, that's
        // fine... it means "configuration error".
        int port = Integer.parseInt(System.getenv("PORT"));
        String dbFile = System.getenv("DB_FILE");
        String clientId = System.getenv("CLIENT_ID");
        String clientSecret = System.getenv("CLIENT_SECRET");
        String serverName = System.getenv("SERVER_NAME");
        // #region get_env
        String staticLocation = System.getenv("STATIC_LOCATION");
        // #endregion get_env

        System.out.println("-".repeat(45));
        System.out.println("Using the following environment variables:");
        System.out.println("  PORT=" + port);
        System.out.println("  DB_FILE=" + dbFile);
        System.out.println("  CLIENT_ID=" + clientId);
        // Warning: you probably don't want to put the secret into the logs!
        System.out.printf("  CLIENT_SECRET=%s%s%n", "*".repeat(clientSecret.length() - 5),
                clientSecret.substring(clientSecret.length() - 5, clientSecret.length()));
        System.out.println("  SERVER_NAME=" + serverName);
        // #region report_env
        System.out.println("  STATIC_LOCATION=" + staticLocation);
        // #endregion report_env
        System.out.println("-".repeat(45));

        // Do some quick validation to ensure the port is in range
        if (dbFile == null || serverName == null ||
                clientId == null || clientSecret == null ||
                port < 80 || port > 65535) {
            System.err.println("Error in environment configuration");
            return;
        }

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

        // NB: `Sessions` makes the back end stateful. This should get migrated
        // to a separate component, such as a memcache, so that it's possible to
        // scale out the backend to multiple servers without users getting
        // accidental logouts.
        var sessions = new Sessions();
        var gOAuth = new GoogleOAuth(serverName, port, clientId, clientSecret, Routes.RT_AUTH_GOOGLE_CALLBACK);

        // Create the web server. This doesn't start it yet!
        var app = Javalin.create(config -> {
            // Attach a logger
            // TODO: Too much logging for production. Make verbosity adjustable.
            config.requestLogger.http((ctx, ms) -> {
                System.out.println("=".repeat(80));
                System.out.printf("%-6s%-8s%-25s%s%n", ctx.scheme(), ctx.method().name(), ctx.path(),
                        ctx.fullUrl());
                if (ctx.queryString() != null)
                    System.out.printf("query string:%s%n", ctx.queryString());
                if (ctx.body().length() > 0)
                    System.out.printf("request body:%s%n", ctx.body());
            });
            // #region config
            // Serve static files from filesystem or jar
            config.staticFiles.add(staticConfig -> {
                // This path is in the JAR, under main/resources
                if (staticLocation == null) {
                    System.out.println("Serving files from JAR");
                    staticConfig.location = Location.CLASSPATH;
                    staticConfig.directory = "/public";
                    // staticConfig.precompress = true; // 
                    staticConfig.precompressMaxSize = 0; // compress/cache in mem at startup 
                } else { // This path is in the file system
                    System.out.println("Serving files from EXTERNAL LOCATION");
                    staticConfig.location = Location.EXTERNAL;
                    staticConfig.directory = staticLocation;
                    // Javalin 7 uses Jetty 12, which marks symlinked filesystem paths as
                    // "aliases" and rejects them unless an aliasCheck is configured.
                    // Without this, any static path that involves a symlink silently
                    // returns 404. Allowing all aliases is safe here because Jetty
                    // already confines resolution to within baseResource, preventing
                    // path-traversal even when aliases are permitted.
                    // io.javalin.http.staticfiles.AliasCheck allowAliases = (path, realPath) -> true;
                    staticConfig.aliasCheck = (path, realPath) -> true;
                    staticConfig.precompressMaxSize = -1; // don't compress/cache in mem
                }
                System.out.printf("Using staticConfig.directory=%s%n", staticConfig.directory);                
            });
            // Support single-page apps
            if (staticLocation == null) {
                String defaultPage = "public/index.html";
                System.out.println("*** Using files in JAR    --> setting spaRoot to " + defaultPage);
                config.spaRoot.addFile("/", defaultPage, Location.CLASSPATH);
            } else {
                String defaultPage = staticLocation + "/index.html";
                System.out.println("*** Using STATIC_LOCATION --> setting spaRoot to " + defaultPage);
                config.spaRoot.addFile("/", defaultPage, Location.EXTERNAL);
            }
            // #endregion config
            // Every interaction with the server requires the user to be authenticated
            config.routes.before(ctx -> {
                // To avoid an infinite loop, we don't cry havoc if the user is in
                // the middle of an auth flow
                if (ctx.url().equals(gOAuth.redirectUri)) {
                    System.out.println(">>>>>>> SETTING UP A NEW SESSION, at " + gOAuth.redirectUri);
                    return;
                }
                String gId = ctx.cookie("auth.gId");
                String key = ctx.cookie("auth.key");
                // We also don't cry havoc if the user is logged in
                if (sessions.checkValid(gId, key)) {
                    return;
                }
                System.out.println(">>>>>>> INVALID SESSION, redirecting to " + gOAuth.newAuthUrl);
                ctx.redirect(gOAuth.newAuthUrl);
            });

            // All routes go here
            config.routes.apiBuilder(() -> {
                // Handle Google oauth by extracting the "code" and authenticating it, then redirecting
                get(Routes.RT_AUTH_GOOGLE_CALLBACK,
                    ctx -> Routes.authCallback(ctx, db, gson, sessions, gOAuth));
                // Log out
                get("/logout", ctx -> Routes.authLogout(ctx, gson, sessions));
                // Get a list of all the people in the system
                get("/people", ctx -> Routes.readPersonAll(ctx, db, gson));
                // Get all details for a specific person
                get("/people/{id}", ctx -> Routes.readPersonOne(ctx, db, gson));
                // Update the current user's name
                put("/people", ctx -> Routes.updatePerson(ctx, db, gson, sessions));

                // Create a message
                post("/messages", ctx -> Routes.createMessage(ctx, db, gson, sessions));
                // Get a list of all the messages in the system
                get("/messages", ctx -> Routes.readMessageAll(ctx, db, gson));
                // Get all details for a specific message
                get("/messages/{id}", ctx -> Routes.readMessageOne(ctx, db, gson));
                // Update a message's fields
                put("/messages/{id}", ctx -> Routes.updateMessage(ctx, db, gson, sessions));
                // Delete a message
                delete("/messages/{id}", ctx -> Routes.deleteMessage(ctx, db, gson, sessions));
            });
        });

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

        // This next line launches the server, so it can start receiving
        // requests. Note that main will return, but the server keeps running.
        app.start(port);
    }
}
