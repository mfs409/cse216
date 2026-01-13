package quickstart.backend;

// #region newdeps
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
// #endregion newdeps

/** All of the logic for interacting with Google OAuth. */
public class GoogleOAuth {
    // #region setupOauth
    /**
     * URI google redirects to after login event, equal to 'serverName + ":" + port
     * + "/auth/google/callback"'
     */
    public final String redirectUri;

    /** URL to redirect a user to when authenticating them for the first time */
    public final String newAuthUrl;

    /** used by the PeopleService builder */
    public static final String APPLICATION_NAME = "CSE216 Tutorials";

    /** The object used for sending HTTP requests to Google */
    private NetHttpTransport httpTransport;

    /** The flow object for managing credentials */
    private GoogleAuthorizationCodeFlow flow;

    /**
     * Utility class for performing google oauth operations.
     * This sets up Google OAuth so we can get users email and profile data. It also
     * creates the URI where google returns after an auth, the redirectUri, as well
     * as the URI to redirect a user to to log them in, the newAuthUrl.
     * 
     * @param serverName           the host on which this is running, e.g.
     *                             'http://localhost'
     * @param port                 the port on which the webserver is running, e.g.
     *                             3000
     * @param clientId             the web client id from
     *                             https://console.cloud.google.com/
     * @param clientSecret         the secret associated with clientId
     * @param authCallbackEndpoint the callback endpoint used by google after signin
     *                             event, e.g. '/auth/google/callback'
     */
    public GoogleOAuth(String serverName, int port, String clientId, String clientSecret, String authCallbackEndpoint) {
        var gApis = new ArrayList<String>(Arrays.asList(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/userinfo.profile"));
        if (serverName.toLowerCase().endsWith("dokku.cse.lehigh.edu"))
            this.redirectUri = serverName + authCallbackEndpoint;
        else // only specify the port when the host isn't dokku.cse.lehigh.edu
            this.redirectUri = serverName + ":" + port + authCallbackEndpoint;
        this.httpTransport = new NetHttpTransport();
        this.flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, GsonFactory.getDefaultInstance(), clientId,
                clientSecret, gApis)
                .setAccessType("offline").build();
        this.newAuthUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
        System.out.println("<".repeat(45));
        System.out.println("Using the following oauth information:");
        System.out.println("  redirectURI: " + redirectUri);
        System.out.println("  newAuthUrl: " + newAuthUrl);
        System.out.println(">".repeat(45));
    }

    /** An object holding the user's user information from Google */
    public static record OAuthProfile(String gId, String email, String name) {
    }

    /**
     * Use the People API to get a user's information from Google
     *
     * @param queryParamCode The data that Google returned after a successful OAuth
     *                       flow
     * 
     * @return The user's information as an OAuthProfile object
     * 
     * @throws IOException if there is an issue interacting with Google
     */
    public OAuthProfile getProfileInformation(String queryParamCode) throws IOException {
        // Use PeopleService to ask Google for the user's name and email
        var ps = new PeopleService.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                flow.createAndStoreCredential(
                        flow.newTokenRequest(queryParamCode).setRedirectUri(redirectUri).execute(),
                        null))
                .setApplicationName(APPLICATION_NAME)
                .build();
        // Extract fields
        var profile = ps.people().get("people/me").setPersonFields("names,emailAddresses").execute();
        String email = profile.getEmailAddresses().get(0).getValue();
        String name = profile.getNames().get(0).getDisplayName();
        String gId = profile.getEmailAddresses().get(0).getMetadata().getSource().getId();
        return new OAuthProfile(gId, email, name);
    }
    // #endregion setupOauth
}
