import java.util.List;

public class Controller {
    private final UserInterface userInterface;
    private final UserManager userManager;
    private final ProjectManager projectManager;
    private Role userRole;

    public Controller(UserInterface userInterface) {
        this.userInterface = userInterface;
        this.projectManager = new ProjectManager();
        this.userManager = new UserManager();
        this.userRole = null;
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
        }*/ // TODO: dit niet nemen??
        this.userRole = userManager.login(username, password);

        // TODO: Misschien deze test van isLoggedIn beter in de UI zelf doen? Ge roept in beide instanties sws iets op van de UI..
        if (isLoggedIn()) {
            userInterface.printWelcome(roleToString(userRole));
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

    /** TODO: Dit weghalen? Eigenlijk wordt dat alleen gebruikt om wat onnodige extra functionaliteit toe te voegen..
     * Checks if the user is logged in by checking if the role is set.
     *
     * @return True if the user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return userRole != null;
    }


    public String getProjectNames() {
        StringBuilder projectString = new StringBuilder();

        List<Project> projects = projectManager.getProjects();

        int index = 1;
        for (Project project : projects) {
            projectString.append(index++ + ". " + project.getName() + '\n');
        }
        return projectString.toString();
    }

    /* TODO: Ik heb de toString methode voor getProjectDetails() het grootste werk laten doen, maar bij getProjectNames()
     *  (zie hierboven) doet de controller dat zelf, ik heb ze expres zo gehouden zodat we de twee methodes kunnen
     *  vergelijken, het lijkt logisch dat de toString() van 1 project gewoon alle details en tasks print, dus moet de
     *  controller niet veel doen, maar voor "getProjectNames()" worden eerst alle projects opgehaald en deze dan 1 voor
     *  1 afgeprint, zouden we dit de verantwoordelijkheid maken van projectManager (bv in de toString() van projectmanager),
     *  of houden we het zo?
     */
    public String getProjectDetails(String selectedProjectName) {
        Project selectedProject = projectManager.getProject(selectedProjectName);
        if (selectedProject == null) {
            return null;
        }
        return selectedProject.toString();
    }

    public Role getRole() {
        return userRole;
    }

    public String getTaskDetails(String selectedProjectName, String selectedTaskName) {
        Project selectedProject = projectManager.getProject(selectedProjectName);

        Task selectedTask = selectedProject.getTask(selectedTaskName);
        if (selectedTask == null) {
            return null;
        }
        return selectedTask.toString();
    }
}
