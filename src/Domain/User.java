package Domain;

import java.util.HashSet;
import java.util.Set;

/**
 * A user currently registered within the system
 */
public class User {

    private final String username;
    private final String password;
    private final Set<Role> roles;

    public User(String username, String password, Set<Role> roles) {
        if (username == null || password == null || roles == null || roles.size() == 0) {
            throw new IllegalArgumentException("Username, password and roles have to be initiated");
        }
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>(roles);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }
}
