package Domain;

public class User {

    private final String username;
    private final String password;
    private final Role role;

    public User(String username, String password, Role role) {
        if (username == null || password == null || role == null) {
            throw new IllegalArgumentException("Username, password or role cannot be null");
        }
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
