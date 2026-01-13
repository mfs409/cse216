package quickstart.backend;

import io.javalin.Javalin;
import io.javalin.http.ContentType;

/** A backend built with the Javalin framework */
public class App {
    public static void main(String[] args) {
        // get the port on which to listen. If this crashes the program, that's
        // fine... it means "configuration error".
        int port = Integer.parseInt(System.getenv("PORT"));

        System.out.println("-".repeat(45));
        System.out.println("Using the following environment variables:");
        System.out.println("  PORT=" + port);
        System.out.println("-".repeat(45));

        // Do some quick validation to ensure the port is in range
        if (port < 80 || port > 65535) {
            System.err.println("Error in environment configuration");
            return;
        }

        // Create the web server. This doesn't start it yet!
        var app = Javalin.create();

        // When a client requests the root resource ('/'), return "hello"
        app.get("/", ctx -> {
            ctx.status(200);
            ctx.contentType(ContentType.TEXT_PLAIN);
            ctx.result("hello");
        });

        // This next line launches the server, so it can start receiving
        // requests. Note that main will return, but the server keeps running.
        app.start(port);
    }
}
