package Application;

import Domain.Role;
import Domain.User;

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

  public Role getRole() {
    if (getCurrentUser() == null) {
      return null;
    }
    return getCurrentUser().getRole();
  }

  /**
   * Logs in with the given user
   *
   * @post getCurrentUser() == user
   * @post getRole() == user.getRole()
   * @param user user to log in with
   * @return Domain.Role of set user
   */
  public Role login(User user) {
    setCurrentUser(user);
    return getRole();
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
