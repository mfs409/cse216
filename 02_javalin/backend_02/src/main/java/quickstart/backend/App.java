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

        // #region create
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
        // #endregion create

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
