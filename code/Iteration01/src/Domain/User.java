package Domain;

import Domain.Role;

public class User {

  private String username;
  private String password;
  private Role role;

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
