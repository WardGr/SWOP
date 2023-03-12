import java.util.Scanner;

public class SessionController {
    private final Session session;
    private final UserManager userManager;
    private final SessionUI sessionUI;

    public SessionController(Session session, SessionUI sessionUI, UserManager userManager) {
        this.session = session;
        this.sessionUI = sessionUI;
        this.userManager = userManager;
    }

    public void loginRequest(Scanner scanner){
        if (session.isLoggedIn()) {
            sessionUI.printAlreadyLoggedInError();
            return;
        }
        sessionUI.loginPrompt(scanner);
    }


    /**
     * Passes the username and password on to the UserManager, initialises the current session by setting the
     * appropriate role, and tells the UI to print a welcome message, or error if the given user does not exist.
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     */
    public void login(String username, String password, Scanner scanner) {
        if (session.isLoggedIn()) {
            sessionUI.printAlreadyLoggedInError();
            return;
        }
        try {
            User newUser = userManager.getUser(username, password);
            session.login(newUser);
            sessionUI.printWelcome(username, newUser.getRole().toString());
        }
        catch (UserNotFoundException e) {
            sessionUI.handleLoginError(scanner);
        }
    }

    public void logout() {
        if (!session.isLoggedIn()) {
            sessionUI.printLogoutError();
            return;
        }
        session.logout();
        sessionUI.printLogout();
    }
}
