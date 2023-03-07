public class SessionController {
    public Session session;
    public UserManager userManager;
    public SessionUI sessionUI;

    /**
     * Passes the username and password on to the UserManager, initialises the current session by setting the
     * appropriate role, and tells the UI to print a welcome message, or error if the given user does not exist.
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     */
    public void login(String username, String password) {
        if (session.isLoggedIn()) {
            sessionUI.printAlreadyLoggedInError();
            return;
        }
        try {
            User newUser = userManager.getUser(username, password);
            session.login(newUser);
            sessionUI.printWelcome(username, newUser.getRole().toString()); // Dependency to Role enum, shouldnt be an issue.
        }
        catch (IncorrentLoginException e) {
            sessionUI.handleLoginError();
        }
    }

    public void logout() {
        if (!session.isLoggedIn()) {
            sessionUI.printLogoutError();
        }
        session.logout();
    }
}
