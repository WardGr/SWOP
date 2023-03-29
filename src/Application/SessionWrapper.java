package Application;

import Domain.Role;
import Domain.User;

/**
 * Read-only wrapper class for Session object, encapsulating the Session object
 */
public class SessionWrapper {
    private final Session session;

    public SessionWrapper(Session session){
        this.session = session;
    }

    private Session getSession(){
        return session;
    }

    public User getCurrentUser(){
        return getSession().getCurrentUser();
    }

    public Role getRole(){
        return getSession().getRole();
    }
}
