import java.util.Scanner;

public class SessionController {

  private final Session session;
  private final UserManager userManager;

  public SessionController(
    Session session,
    UserManager userManager
  ) {
    this.session = session;
    this.userManager = userManager;
  }

  public boolean loginPrecondition() {
    return !getSession().isLoggedIn();
  }

  private Session getSession() {
    return session;
  }

  /**
   * Passes the username and password on to the UserManager, initialises the current session by setting the
   * appropriate role, and tells the UI to print a welcome message, or error if the given user does not exist.
   * @param username The username the user gave via the UI login prompt
   * @param password The password the user gave via the UI login prompt
   */
  public Role login(String username, String password) throws IncorrectPermissionException, UserNotFoundException {
    if (session.isLoggedIn()) {
      throw new IncorrectPermissionException();
    }
    User newUser = userManager.getUser(username, password);
    return session.login(newUser);
  }

  public boolean logout() {
    if (!session.isLoggedIn()) {
      return false;
    }
    session.logout();
    return true;
  }
}
