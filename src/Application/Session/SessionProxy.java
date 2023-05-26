package Application.Session;

import Domain.User.Role;
import Domain.User.User;

import java.util.Set;

/**
 * Read-only data class for Session object, encapsulating the Session object
 */
public class SessionProxy {
    private final Session session;

    /**
     * Creates a read-only data object for the given Session object
     *
     * @param session
     */
    public SessionProxy(Session session) {
        this.session = session;
    }

    /**
     * @return The Session this data object represents
     */
    private Session getSession() {
        return session;
    }

    /**
     * @return The currently logged-in user
     */
    public User getCurrentUser() {
        return getSession().getCurrentUser();
    }

    /**
     * @return The roles of the currently logged-in user
     */
    public Set<Role> getRoles() {
        return getSession().getRoles();
    }
}
