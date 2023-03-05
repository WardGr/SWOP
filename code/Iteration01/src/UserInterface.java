import java.util.Scanner;

public class UserInterface {
    private final Controller controller;

    public UserInterface() {
        this.controller = new Controller(this);
    }

    /**
     * Starts the system by sequentially handling commands input by the user
     */
    public void startSystem() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("At your order! Enter 'help' for a list of commands.");

        String nextCommand = scanner.nextLine();
        while(!nextCommand.equals("shutdown")) {
            handleCommand(nextCommand);
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
            case "login" -> login();
            case "logout" -> logout();
            case "showprojects" -> showProjects();
            default -> System.out.println("Unknown command, please try again!");
        }
    }

    /**
     * Prints all available commands
     */
    public void printHelp() {
        System.out.println("Available commands:");
        System.out.println("help: Prints this message");
        System.out.println("login: Shows the login prompt");
        System.out.println("logout: Logs out");
        System.out.println("shutdown: Exits the system");
        System.out.println("showprojects: Shows a list of all current projects");
    }

    /**
     * Prints the login prompt, receives the username and password from the user via the command line, and sends it over
     * to the SessionController.
     */
    private void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter username:");
        String username = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        controller.login(username, password);
    }

    /**
     * Removes the current session
     */
    private void logout() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Are you sure you want to log out? (y/n)");
        String answer = scanner.nextLine();

        if (answer.equals("y")) {
            if (controller.logout()) {
                System.out.println("Logged out!");
            } else {
                System.out.println("Error: you are not logged in!");
            }
        }
    }

    private void showProjects() {
        if(controller.getRole() != Role.PROJECTMANAGER) {
            System.out.println("You must be logged in as Project Manager to show all projects");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        showAllProjects();
        System.out.println("Type a projects name to see its details, including a list of its tasks.");

        String selectedProject = scanner.nextLine();
        showSpecificProject(selectedProject);
    }

    private void showAllProjects() {
        System.out.println("***************** PROJECTS *****************");
        System.out.println(controller.getProjectNames());
    }

    private void showSpecificProject(String selectedProject) {
        Scanner scanner = new Scanner(System.in);

        String projectString = controller.getProjectDetails(selectedProject);

        while(projectString == null) {
            System.out.println("Sorry, that project doesn't exist, please try again.");
            selectedProject = scanner.nextLine();
            projectString = controller.getProjectDetails(selectedProject);
        }

        System.out.println("*************** PROJECT DETAILS ***************");
        System.out.println(projectString);

        System.out.println("Select a task by typing its name, or type BACK to select another project");
        String response = scanner.nextLine();

        if (response.equals("BACK")) {
            showProjects();
        }
        else {
            selectTask(selectedProject, response);
        }
    }

    private void selectTask(String selectedProject, String selectedTask) {
        Scanner scanner = new Scanner(System.in);

        String taskString = controller.getTaskDetails(selectedProject, selectedTask);

        while(taskString == null) {
            System.out.println("Sorry, that task doesn't exist, please try again.");
            selectedProject = scanner.nextLine();
            taskString = controller.getTaskDetails(selectedProject, selectedTask);
        }

        System.out.println("*************** TASK DETAILS ***************");
        System.out.println(taskString);

        System.out.println("Select another task by typing its name, or type BACK to select another project");
        String response = scanner.nextLine();

        if (response.equals("BACK")) {
            showProjects();
        }
        else {
            selectTask(selectedProject, response);
        }
    }

    public void printLoginError() {
        System.out.println("Wrong password/username combination, please try again!");
    }

    public void printWelcome(String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }
}
