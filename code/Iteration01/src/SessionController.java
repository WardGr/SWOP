public class SessionController {
    private final UserInterface userInterface;
    private final UserManager userManager;
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
        Role role = userManager.login(username, password);
        if (isLoggedIn()) {
            this.userRole = role;
            userInterface.printWelcome(roleToString(role));
        }
        else {
            userInterface.printLoginError();
        }
    }


    public String roleToString(Role role) {
        return switch (role) {
            case PROJECTMANAGER -> "Project Manager";
            case DEVELOPER -> "Developer";
        };
    }

    /**
     * Lijkt misschien raar, maar maakt de code wel leesbaarder, en als we beslissen geen role bij te houden kunnen
     * we dat eenvoudig hier aanpassen
     */
    public boolean isLoggedIn() {
        return userRole != null;
    }
}
