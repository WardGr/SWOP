public class User {
    private final String username;
    private final String password;
    private Role role;
    // Kunnen mensen ooit ontslagen of gepromoveerd worden?

    public User(String username, String password, Role role) {
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
