package quickstart.backend;

import io.javalin.http.ContentType;
import io.javalin.http.Context;

import com.google.gson.*;

// #region newdeps
import java.util.Base64;
// #endregion newdeps

/**
 * All of the routing logic for the app is stored in Routes, so that the code
 * will be easier to keep organized, and so that it will be easier to test.
 */
public class Routes {
    // #region constants
    /** The path for the callback from Google OAuth */
    public static final String RT_AUTH_GOOGLE_CALLBACK = "/auth/google/callback";

    /**
     * The path to use when OAuth fails. There won't be a handler for this
     * route, which means that going to it will simply restart the OAuth flow.
     */
    public static final String RT_AUTHERROR = "/autherror";
    // #endregion constants

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

    // #region new_routes
    /**
     * Handle a code returned from Google during OAuth flow
     *
     * @param ctx      The HTTP context, with cookies, querystring, etc
     * @param db       The database
     * @param gson     A thread-safe object for converting to/from JSON
     * @param sessions The session store
     * @param gOAuth   A fully configured GoogleOAuth object
     */
    public static void authCallback(Context ctx, Database db, Gson gson, Sessions sessions, GoogleOAuth gOAuth) {
        System.out.println(">>>>>>>>>>>>>> invoking authCallBack");
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        try {
            GoogleOAuth.OAuthProfile profile = gOAuth.getProfileInformation(ctx.queryParam("code"));

            // Make sure they're in the database
            var user = db.getPersonByEmail(profile.email());
            if (user == null) {
                // NB: "/autherror" is not a valid path, but using it will
                // redirect to login
                ctx.redirect(Routes.RT_AUTHERROR);
                return;
            }

            // Set up a cookie with the user's important info, put the user in
            // the session store, and redirect to the home page
            var key = sessions.onLogin(profile.gId(), user.id(), profile.email(), profile.name());
            ctx.cookie("auth.gId", profile.gId());
            ctx.cookie("auth.key", key);
            ctx.cookie("auth.email", profile.email());
            ctx.cookie("auth.name", Base64.getEncoder().encodeToString(profile.name().getBytes()));
            ctx.cookie("auth.id", "" + user.id());
            ctx.redirect("/");
        } catch (Exception e) {
            System.out.println("Authentication Error" + e);
            // NB: This actually redirects to OAuth login screen
            ctx.redirect(Routes.RT_AUTHERROR);
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
        if (sessions.logOut(gId)) {
            ctx.result(gson.toJson("ok"));
        } else {
            ctx.result(gson.toJson("error logging out"));
        }
    }
    // #endregion new_routes
}
