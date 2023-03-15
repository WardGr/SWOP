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

  private Session getSession() {
    return session;
  }

  private UserManager getUserManager(){
    return userManager;
  }

  public boolean loginPrecondition() {
    return !getSession().isLoggedIn();
  }

  /**
   * Passes the username and password on to the UserManager, initialises the current session by setting the
   * appropriate role, and tells the UI to print a welcome message, or error if the given user does not exist.
   * @param username The username the user gave via the UI login prompt
   * @param password The password the user gave via the UI login prompt
   */
  public Role login(String username, String password) throws LoginException {
    if (getSession().isLoggedIn()) {
      throw new LoginException("Incorrect permission: User already logged in");
    }
    User newUser = getUserManager().getUser(username, password);
    return getSession().login(newUser);
  }

  public boolean logout() {
    if (!getSession().isLoggedIn()) {
      return false;
    }
    getSession().logout();
    return true;
  }
}
