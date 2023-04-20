package UserInterface;

import java.util.Scanner;

/**
 * First point of interaction with the user, separates use-case specific UI-logic from the user by delegating the work
 * to the respective use-case UI class based on user commands.
 */
public class UserInterface {

    private final SessionUI sessionUI;
    private final ShowProjectsUI showProjectsUI;
    private final CreateProjectUI createProjectUI;
    private final CreateTaskUI createTaskUI;
    private final AdvanceTimeUI advanceTimeUI;
    private final LoadSystemUI loadSystemUI;
    private final StartTaskUI startTaskUI;
    private final EndTaskUI endTaskUI;
    private final UpdateDependenciesUI updateDependenciesUI;

    public UserInterface(
            SessionUI sessionUI,
            AdvanceTimeUI advanceTimeUI,
            CreateProjectUI createProjectUI,
            ShowProjectsUI showProjectsUI,
            CreateTaskUI createTaskUI,
            LoadSystemUI loadSystemUI,
            StartTaskUI startTaskUI,
            EndTaskUI endTaskUI,
            UpdateDependenciesUI updateDependenciesUI
    ) {
        this.sessionUI = sessionUI;
        this.advanceTimeUI = advanceTimeUI;
        this.createProjectUI = createProjectUI;
        this.showProjectsUI = showProjectsUI;
        this.createTaskUI = createTaskUI;
        this.loadSystemUI = loadSystemUI;
        this.startTaskUI = startTaskUI;
        this.endTaskUI = endTaskUI;
        this.updateDependenciesUI = updateDependenciesUI;
    }

    /**
     * Starts the system by sequentially handling commands input by the user
     * System quits if user gives "shutdown" command
     */
    public void startSystem() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("At your order! Enter 'help' for a list of commands.");
        System.out.print(">");

        String nextCommand = scanner.nextLine();
        while (!nextCommand.equals("shutdown")) {
            handleCommand(nextCommand);
            System.out.print(">");
            nextCommand = scanner.nextLine();
        }
    }

    /**
     * Calls the appropriate function depending on the user input command
     *
     * @param command String representation of the command given by the user via CLI
     */
    private void handleCommand(String command) {
        switch (command) {
            case "help" -> printHelp();
            case "login" -> sessionUI.loginRequest();
            case "logout" -> sessionUI.logout();
            case "showprojects" -> showProjectsUI.showProjects();
            case "createproject" -> createProjectUI.createProject();
            case "createtask" -> createTaskUI.createTask();
            case "advancetime" -> advanceTimeUI.advanceTime();
            case "loadsystem" -> loadSystemUI.loadSystem();
            case "starttask" -> startTaskUI.startTask();
            case "endtask" -> endTaskUI.endTask();
            case "updatedependencies" -> updateDependenciesUI.updateDependencies();
            default -> System.out.println(
                    "Unknown command, type help to see available commands"
            );
        }
    }

    /**
     * Prints all available commands with information for the user
     */
    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("help:               Prints this message");
        System.out.println("login:              Shows the login prompt");
        System.out.println("logout:             Logs out");
        System.out.println("shutdown:           Exits the system");
        System.out.println("showprojects:       Shows a list of all current projects");
        System.out.println("createproject:      Shows the project creation prompt and creates a project");
        System.out.println("createtask:         Shows the task creation prompt to add a task to a project");
        System.out.println("advancetime:        Allows the user to modify the system time");
        System.out.println("loadsystem:         Allows the user to load projects and tasks into the system");
        System.out.println("starttask:          Allows the user to start a task");
        System.out.println("endtask:            Allows the user to end a task");
        System.out.println("updatedependencies: Allows the user to update the dependencies of a task");
    }
}
