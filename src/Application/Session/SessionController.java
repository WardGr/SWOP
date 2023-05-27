package Application.Session;

import Application.Session.LoginException;
import Application.Session.Session;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserManager;

import java.util.Set;

/**
 * Separates domain from UI for the login and logout use-cases
 */
public class SessionController {

    private final Session session;
    private final UserManager userManager;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param userManager       Object managing all users in the system
     */
    public SessionController(
            Session session,
            UserManager userManager
    ) {
        this.session = session;
        this.userManager = userManager;
    }

    /**
     * @return  The Session object with currently logged-in user
     */
    private Session getSession() {
        return session;
    }

    /**
     * @return  Returns the object that manages all users
     */
    private UserManager getUserManager() {
        return userManager;
    }

    /**
     * @return  true if user is not logged in, false otherwise
     */
    public boolean loginPrecondition() {
        return !getSession().isLoggedIn();
    }

    /**
     * Passes the username and password on to the UserManager, initialises the current session by setting the
     * appropriate user.
     *
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     * @throws LoginException if username and password do not match or user is already logged in.
     * @post User is set in session
     */
    public Set<Role> login(String username, String password) throws LoginException {
        if (getSession().isLoggedIn()) {
            throw new LoginException("Incorrect permission: User already logged in");
        }
        User newUser = getUserManager().getUser(username, password);
        return getSession().login(newUser);
    }

    /**
     * Logs out of the current session
     *
     * @return true if user was logged in before call, false otherwise
     * @post getSession().getUser() == null
     */
    public boolean logout() {
        if (!getSession().isLoggedIn()) {
            return false;
        }
        getSession().logout();
        return true;
    }
}
