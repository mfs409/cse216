package quickstart.backend;

import io.javalin.http.ContentType;
import io.javalin.http.Context;

import com.google.gson.*;

import java.util.Base64;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;

public class Routes {
    public static final String RT_AUTH_GOOGLE_CALLBACK = "/auth/google/callback";
    public static final String RT_AUTHERROR = "/autherror";
    
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

    /**
     * Handle a code returned from Google during OAuth flow
     *
     * @param ctx           The HTTP context, with cookies, querystring, etc
     * @param db            The database
     * @param gson          A thread-safe object for converting to/from JSON
     * @param sessions      The session store
     * @param httpTransport A HTTP transport, for sending HTTP requests to Google
     * @param flow          An OAuth flow object, for managing the flow
     * @param redirectUri   The redirect URI for the app
     */
    public static void authCallback(Context ctx, Database db, Gson gson, Sessions sessions, HttpTransport httpTransport,
            GoogleAuthorizationCodeFlow flow, String redirectUri) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            // Use PeopleService to ask Google for the user's name and email
            var ps = new PeopleService.Builder(httpTransport, GsonFactory.getDefaultInstance(),
                    flow.createAndStoreCredential(
                            flow.newTokenRequest(ctx.queryParam("code")).setRedirectUri(redirectUri).execute(),
                            null))
                    .build();
            // Extract fields
            var profile = ps.people().get("people/me").setPersonFields("names,emailAddresses").execute();
            String email = profile.getEmailAddresses().get(0).getValue();
            String name = profile.getNames().get(0).getDisplayName();

            // Make sure they're in the database
            var user = db.getPersonByEmail(email);
            if (user == null) {
                // NB: "/autherror" is not a valid path, but using it will
                // redirect to login
                ctx.redirect("/autherror");
                return;
            }

            // Set up a cookie with the user's important info, put the user in
            // the session store, and redirect to the home page
            String gId = profile.getEmailAddresses().get(0).getMetadata().getSource().getId();
            var key = sessions.onLogin(gId, user.id(), email, name);
            ctx.cookie("auth.gId", gId);
            ctx.cookie("auth.key", key);
            ctx.cookie("auth.email", email);
            ctx.cookie("auth.name", Base64.getEncoder().encodeToString(name.getBytes()));
            ctx.cookie("auth.id", "" + user.id());
            ctx.redirect("/");
        } catch (Exception e) {
            System.out.println("Authentication Error" + e);
            // NB: This actually redirects to OAuth login screen
            ctx.redirect("/autherror");
        }
    }

    /**
     * Log out by dropping a user's entry in the sessions table, which makes
     * their cookie invalid
     *
     * @param ctx      The HTTP context, with cookies, querystring, etc
     * @param gson     A thread-safe object for converting to/from JSON
     * @param sessions The session store
     */
    public static void authLogout(Context ctx, Gson gson, Sessions sessions) {
        String gId = ctx.cookie("auth.gId");
        sessions.logOut(gId);
        ctx.result(gson.toJson("ok"));
    }

    /**
     * Get all data for one person, return it as JSON
     *
     * @param ctx  The HTTP context, with cookies, querystring, etc
     * @param db   The database
     * @param gson A thread-safe object for converting to/from JSON
     */
    public static void readPersonOne(Context ctx, Database db, Gson gson) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            var data = db.getOnePerson(Integer.parseInt(ctx.pathParam("id")));
            if (data == null)
                throw new Exception("No data found");
            ctx.result(gson.toJson(new StructuredResponse("ok", null, data)));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }

    /**
     * Update the name for the current user
     *
     * @param ctx      The HTTP context, with cookies, querystring, etc
     * @param db       The database
     * @param gson     A thread-safe object for converting to/from JSON
     * @param sessions The session store
     */
    public static void updatePerson(Context ctx, Database db, Gson gson, Sessions sessions) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            var req = gson.fromJson(ctx.body(), Database.NameChangeRequest.class);
            db.updatePersonName(sessions.get(ctx.cookie("auth.gId")).id(), req);
            ctx.result(gson.toJson(new StructuredResponse("ok", null, null)));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }

    /**
     * Create a new message
     *
     * @param ctx      The HTTP context, with cookies, querystring, etc
     * @param db       The database
     * @param gson     A thread-safe object for converting to/from JSON
     * @param sessions The session store
     */
    public static void createMessage(Context ctx, Database db, Gson gson, Sessions sessions) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            var req = gson.fromJson(ctx.body(), Database.NewMessageRequest.class);
            long id = db.insertMessage(req, sessions.get(ctx.cookie("auth.gId")).id());
            if (id == -1)
                throw new Exception("Error inserting");
            ctx.result(gson.toJson(new StructuredResponse("ok", null, "" + id)));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }

    /**
     * Get a summary of all the messages
     *
     * @param ctx  The HTTP context, with cookies, querystring, etc
     * @param db   The database
     * @param gson A thread-safe object for converting to/from JSON
     */
    public static void readMessageAll(Context ctx, Database db, Gson gson) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            ctx.result(gson.toJson(new StructuredResponse("ok", null, db.getAllMessage())));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }

    /**
     * Get everything about a single message
     *
     * @param ctx  The HTTP context, with cookies, querystring, etc
     * @param db   The database
     * @param gson A thread-safe object for converting to/from JSON
     */
    public static void readMessageOne(Context ctx, Database db, Gson gson) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            var data = db.getOneMessage(Integer.parseInt(ctx.pathParam("id")));
            if (data == null)
                throw new Exception("No data found");
            ctx.result(gson.toJson(new StructuredResponse("ok", null, data)));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }

    /**
     * Update the details (and date) for a message, but only if the current user
     * created it
     *
     * @param ctx      The HTTP context, with cookies, querystring, etc
     * @param db       The database
     * @param gson     A thread-safe object for converting to/from JSON
     * @param sessions The session store
     */
    public static void updateMessage(Context ctx, Database db, Gson gson, Sessions sessions) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            var req = gson.fromJson(ctx.body(), Database.UpdateMessageRequest.class);
            db.updateMessage(Integer.parseInt(ctx.pathParam("id")), req, sessions.get(ctx.cookie("auth.gId")).id());
            ctx.result(gson.toJson(new StructuredResponse("ok", null, null)));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }

    /**
     * Delete a message, but only if the current user created it
     *
     * @param ctx      The HTTP context, with cookies, querystring, etc
     * @param db       The database
     * @param gson     A thread-safe object for converting to/from JSON
     * @param sessions The session store
     */
    public static void deleteMessage(Context ctx, Database db, Gson gson, Sessions sessions) {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            db.deleteMessage(Integer.parseInt(ctx.pathParam("id")), sessions.get(ctx.cookie("auth.gId")).id());
            ctx.result(gson.toJson(new StructuredResponse("ok", null, null)));
        } catch (Exception e) {
            ctx.result(gson.toJson(new StructuredResponse("error", e.getMessage(), null)));
        }
    }
}
