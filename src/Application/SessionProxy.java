package Application;

import Domain.Role;
import Domain.User;

import java.util.Set;

/**
 * Read-only wrapper class for Session object, encapsulating the Session object
 */
public class SessionProxy {
    private final Session session;

    public SessionProxy(Session session) {
        this.session = session;
    }

    private Session getSession() {
        return session;
    }

    public User getCurrentUser() {
        return getSession().getCurrentUser();
    }

    public Set<Role> getRoles() {
        return getSession().getRoles();
    }
}
