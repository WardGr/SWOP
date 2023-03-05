public class SessionController {
    private final UserInterface userInterface;
    private UserManager userManager;
    private Role userRole;

    public SessionController(UserInterface userInterface) {
        this.userInterface = userInterface;
        this.userManager = new UserManager();
    }

    /**
     * Passes the username and password on to the UserManager, initialises the current session by setting the
     * appropriate role, and tells the UI to print a welcome message, or error if the given user does not exist.
     *
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     */
    public void login(String username, String password) {
        /*if (isLoggedIn()) {
            System.out.println("You are already logged in!");
            return;
        }*/ // Todo dit niet nemen??
        this.userRole = userManager.login(username, password);
        if (isLoggedIn()) {
            userInterface.printWelcome(roleToString(getUserRole()));
        }
        else {
            userInterface.printLoginError();
        }
    }

    /**
     * Logs the user out by setting the role to null, and initialising a new UserManager.
     *
     * @return True if the user was logged in, false otherwise
     */
    public boolean logout() {
        if (!isLoggedIn()) {
            return false;
        }
        this.userRole = null;
        return true;
    }

    /**
     * Translates the role enum to its corresponding string
     * @param role Role enum returned by userManager
     * @return String that denotes the users role
     */
    public String roleToString(Role role) {
        return switch (role) {
            case PROJECTMANAGER -> "Project Manager";
            case DEVELOPER -> "Developer";
        };
    }

    private Role getUserRole() {
        return userRole;
    }

    /**
     * Checks if the user is logged in by checking if the role is set.
     *
     * @return True if the user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return userRole != null;
    }
}
