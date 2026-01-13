package quickstart.backend;

import io.javalin.http.ContentType;
import io.javalin.http.Context;

import com.google.gson.*;

/**
 * All of the routing logic for the app is stored in Routes, so that the code
 * will be easier to keep organized, and so that it will be easier to test.
 */
public class Routes {
    /**
     * StructuredResponse provides a common format for success and failure
     * messages, with an optional payload of type Object that can be converted
     * into JSON.
     *
     * @param status  for applications to determine if response indicates an
     *                error
     * @param message only useful when status indicates an error, or when data
     *                is null
     * @param data    any JSON-friendly object can be referenced here, so a
     *                client gets a rich reply
     */
    static record StructuredResponse(String status, String message, Object data) {
    }

    /**
     * Get a list of all people, return it as JSON in ctx.result
     *
     * @param ctx  The HTTP context, with cookies, querystring, etc
     * @param db   The database
     * @param gson A thread-safe object for converting to/from JSON
     */
    public static void readPersonAll(Context ctx, Database db, Gson gson) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            ctx.result(gson.toJson(new StructuredResponse("ok", null, db.getAllPerson())));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }
}
