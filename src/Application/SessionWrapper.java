package Application;

import Domain.Role;
import Domain.User;

public class SessionWrapper {
    final Session session;

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
