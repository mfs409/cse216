package quickstart.backend;

import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;

/**
 * Create random strings using a cryptographically good RNG
 * 
 * Details at:
 * https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
 */
class RandStringCreator {
    /** Uppercase Characters */
    private static final String uppers = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /** Lowercase Characters */
    private static final String lowers = "abcdefghijklmnopqrstuvwxyz";

    /** Numerical Characters */
    private static final String nums = "0123456789";

    /** All Valid Characters */
    private static char[] chars = (uppers + lowers + nums).toCharArray();

    /** A random number generator */
    private final SecureRandom rng;

    /** Construct the generator by creating a secure RNG */
    RandStringCreator() {
        this.rng = new SecureRandom();
    }

    /** Create a session key by getting a 21-character random string */
    public String createSessionKey() {
        return getRandString(21);
    }

    /** Get a random string of the requested length (must be >0) */
    private synchronized String getRandString(int length) {
        if (length < 1)
            return null;
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = chars[rng.nextInt(chars.length)];
        return new String(buf);
    }
}

/**
 * Session table entries contain the table's row Id, session key, email, and
 * display name
 */
record UserRecord(int id, String sessionKey, String emailAddress, String displayName) {
}

/** Sessions maintains a key/value store for tracking logged-in users */
public class Sessions {
    /** The ids and tokens for all active users */
    ConcurrentHashMap<String, UserRecord> activeSessions = new ConcurrentHashMap<String, UserRecord>();

    /** A random string generator for making session keys */
    RandStringCreator rsc = new RandStringCreator();

    /**
     * When a user successfully logs in via OAuth, we must create (or update) a
     * pair in mActiveSessions so that we can handle subsequent requests by the
     * user without needing to hit the database again.
     */
    String onLogin(String googleId, int id, String email, String name) {
        // Get a session key
        String sessionKey = rsc.createSessionKey();
        // pair the uid with this key... this invalidates all other logins by
        // this user
        activeSessions.put(googleId, new UserRecord(id, sessionKey, email, name));
        return sessionKey;
    }

    /**
     * Check if the provided uid/session pair corresponds to a valid active
     * login.
     */
    boolean checkValid(String id, String session) {
        if (id == null || session == null || session.equals(""))
            return false;
        var foundSession = activeSessions.get(id);
        return (foundSession != null) && foundSession.sessionKey().equals(session);
    }

    /** Return the whole packet of info for the user given by `id` */
    UserRecord get(String id) {
        return activeSessions.get(id);
    }

    /** Log a user out by removing its email/session mapping */
    boolean logOut(String mId) {
        return activeSessions.remove(mId) != null;
    }
}