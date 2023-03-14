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

  public Role getRole() {
    if (getCurrentUser() == null) {
      return null;
    }
    return getCurrentUser().getRole();
  }

  public Role login(User user) {
    setCurrentUser(user);
    return getRole();
  }

  public void logout() {
    setCurrentUser(null);
  }

  public boolean isLoggedIn() {
    return getCurrentUser() != null;
  }
}
