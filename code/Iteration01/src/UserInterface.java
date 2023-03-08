import java.util.Scanner;

public class UserInterface {
    private final SessionUI sessionUI;
    private final ShowProjectsUI showProjectsUI;
    private final CreateProjectUI createProjectUI;

    public UserInterface(Session session, TaskManSystem taskManSystem) {
        this.sessionUI = new SessionUI(session);
        this.showProjectsUI = new ShowProjectsUI(session, taskManSystem);
        this.createProjectUI = new CreateProjectUI(session, taskManSystem);
    }

    /**
     * Starts the system by sequentially handling commands input by the user
     */
    public void startSystem() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("At your order! Enter 'help' for a list of commands.");
        System.out.print(">");

        String nextCommand = scanner.nextLine();
        while(!nextCommand.equals("shutdown")) {
            handleCommand(nextCommand);
            System.out.print(">");
            nextCommand = scanner.nextLine();
        }
    }

    /**
     * Calls the appropriate function depending on the user input command
     * @param command String representation of the command given by the user via CLI
     */
    private void handleCommand(String command) {
        switch (command) {
            case "help" -> printHelp();
            case "login" -> sessionUI.loginPrompt();
            case "logout" -> sessionUI.logout();
            case "showprojects" -> showProjectsUI.showProjects();
            case "createproject" -> createProjectUI.createProject();
            case "createtask" -> createProjectUI.createTask();
            default -> System.out.println("Unknown command, type help to see available commands");
        }
    }

    /**
     * Prints all available commands
     */
    public void printHelp() {
        System.out.println("Available commands:");
        System.out.println("help:          Prints this message");
        System.out.println("login:         Shows the login prompt");
        System.out.println("logout:        Logs out");
        System.out.println("shutdown:      Exits the system");
        System.out.println("showprojects:  Shows a list of all current projects");
        System.out.println("createproject: Shows the project creation prompt and creates a project");
        System.out.println("createtask:    Shows the task creation prompt to add a task to a project");
    }
}
