package Application;

import Domain.Role;
import Domain.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Keeps track of the user currently logged in, ensures logging in and logging out happens correctly
 */
public class Session {

    private User currentUser;

    public Session() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public Set<Role> getRoles() {
        if (getCurrentUser() == null) {
            return new HashSet<>();
        }
        return getCurrentUser().getRoles();
    }

    /**
     * Logs in with the given user
     *
     * @param user user to log in with
     * @return Role of set user
     * @post getCurrentUser() == user
     * @post getRole() == user.getRole()
     */
    public Set<Role> login(User user) {
        setCurrentUser(user);
        return getRoles();
    }

    /**
     * Logs the current user out
     *
     * @post getCurrentUser() == null
     * @post getRole() == null
     */
    public void logout() {
        setCurrentUser(null);
    }

    /**
     * Returns whether user is logged in or not
     *
     * @return getCurrentUser() != null
     */
    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
}
