public class SessionController {
    private final UserInterface userInterface;
    private final UserManager userManager;
    private Role userRole;

    public SessionController(UserInterface userInterface) {
        this.userInterface = userInterface;
        this.userManager = new UserManager();
    }


    public void login(String username, String password) {
        Role role = userManager.login(username, password);
        if (role == null) {
            userInterface.loginerror();
        }
        else {
            this.userRole = role;
            userInterface.welcome(roleToString(role));
        }
    }

    // Ik dacht deze hier te zetten, de Controller heeft als taak vertaling..
    public String roleToString(Role role) {
        return switch (role) {
            case PROJECTMANAGER -> "Project Manager";
            case DEVELOPER -> "Developer";
        };
    }
}
