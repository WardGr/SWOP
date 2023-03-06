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
            case "login" -> login();
            case "logout" -> logout();
            case "showprojects" -> showProjects();
            case "createproject" -> createProject();
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

    private void logout() {
        if (controller.logout()) {
            System.out.println("Logged out!");
        } else {
            System.out.println("Error: you are not logged in!");
        }
    }

    /**
     * Executes the showprojects user command, checks if the user is a project manager, then prints all projects
     * and prompts the user to choose a project to expand.
     */
    private void showProjects() {
        // TODO: deze check kan beter ergens anders, is ni echt UI-verantwoordelijkheid
        if(controller.getRole() != Role.PROJECTMANAGER) {
            System.out.println("You must be logged in as Project Manager to show all projects");
            return;
        }
        showAllProjects();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a projects name to see its details, including a list of its tasks, type BACK to exit:");

        String response = scanner.nextLine();
        if (!response.equals("BACK")) {
            showSpecificProject(response);
        }
    }

    /**
     * Shows all current project names, numbered starting from 1.
     */
    private void showAllProjects() {
        System.out.println("***************** PROJECTS *****************");
        System.out.println(controller.getProjectNames());
    }

    /**
     * Shows the details of one specific project
     * @param selectedProject User input, should contain an existing project name, if not the user is prompted again
     */
    private void showSpecificProject(String selectedProject) {
        Scanner scanner = new Scanner(System.in);

        String projectString = controller.getProjectDetails(selectedProject);
        while(projectString == null) {
            System.out.println("Sorry, that project doesn't exist, please try again or type BACK to exit:");
            selectedProject = scanner.nextLine();
            if(selectedProject.equals("BACK")) {
                return;
            }
            projectString = controller.getProjectDetails(selectedProject);
        }

        System.out.println("*************** PROJECT DETAILS ***************");
        System.out.println(projectString);

        nextTask(selectedProject);
    }

    /**
     * Shows the specific task details of the selectedTask corresponding to a project selectedProject,
     * @param selectedProject User input corresponding to the name of an existing project
     * @param selectedTask User input corresponding to the name of an existing task attached to selectedProject project,
     *                     if it doesn't exist, another is prompted
     * @pre selectedProject consists of a valid project name // TODO: maybe it's not good to force this precondition, and we should instead decouple it by checking defensively
     */
    private void showTask(String selectedProject, String selectedTask) {
        Scanner scanner = new Scanner(System.in);
        String taskString = controller.getTaskDetails(selectedProject, selectedTask);

        while(taskString == null) {
            System.out.println("Sorry, that task doesn't exist, please try again or type BACK to select another project:");
            selectedTask = scanner.nextLine();
            if (selectedTask.equals("BACK")) {
                showProjects();
            }
            taskString = controller.getTaskDetails(selectedProject, selectedTask);
        }

        System.out.println("*************** TASK DETAILS ***************");
        System.out.println(taskString);

        nextTask(selectedProject);
    }

    /**
     * Shows the task select prompt, prints the selected task or shows the project select screen if the response is "BACK"
     * @param selectedProject User input corresponding to the name of an existing project
     * @pre selectedProject consists of a valid project name // TODO: maybe it's not good to force this precondition, and we should instead decouple it by checking defensively
     */
    public void nextTask(String selectedProject) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select another task by typing its name, or type BACK to select another project:");
        String response = scanner.nextLine();

        if (response.equals("BACK")) {
            showProjects();
        }
        else {
            showTask(selectedProject, response);
        }
    }

    public void createProject() {
        // TODO: deze check kan beter ergens anders, is ni echt UI-verantwoordelijkheid
        if(controller.getRole() != Role.PROJECTMANAGER) {
            System.out.println("You must be logged in as Project Manager to show all projects");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel project creation at any time");
        System.out.println("*********** PROJECT CREATION FORM ***********");

        System.out.print("Project Name: ");
        String projectName = scanner.nextLine();
        if (projectName.equals("BACK")) {
            return;
        }

        System.out.print("Project Description: ");
        String projectDescription = scanner.nextLine();
        if (projectDescription.equals("BACK")) {
            return; //TODO: code duplication, is this an issue? It's pretty readable.
        }

        System.out.print("Project due time: ");
        String dueTime = scanner.nextLine();
        if (dueTime.equals("BACK")) {
            return;
        }

        controller.createProject(projectName, projectDescription, dueTime);

    }

    public void createTask() {
    Scanner scanner = new Scanner(System.in);

    System.out.println("*********** TASK CREATION FORM ***********");
    System.out.println("Project name:");
    String pName = scanner.nextLine();
    if (isCancel(pName)) {
        System.out.println("Cancelled task creation");
        return;
    }

    String tName = scanner.nextLine();
    if (isCancel(tName)) {
        System.out.println("Cancelled task creation");
        return;
    }

    System.out.println("Task description:");
    String descr = scanner.nextLine();
    if (isCancel(descr)) {
        System.out.println("Cancelled task creation");
        return;
    }

    System.out.println("Task duration:");
    String durationString = scanner.nextLine();
    if (isCancel(durationString)) {
        System.out.println("Cancelled task creation");
        return;
    }
    int duration = Integer.parseInt(durationString);

    System.out.println("Task deviation:");
    String deviationString = scanner.nextLine();
    if (isCancel(deviationString)) {
        System.out.println("Cancelled task creation");
        return;
    }
    int deviation = Integer.parseInt(deviationString);

    controller.createTask(pName, tName, descr, duration, deviation);





    }

    public void printLoginError() {
        System.out.println("Wrong password/username combination, please try again!");
    }

    public void printWelcome(String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }

    public void printProjectFormError() {
        System.out.println("The given project data is invalid, please try again\n");
    }

    private boolean isCancel(String input) {
        return input.equals("q");
    }



    public void printTaskFormError() {
        System.out.println("The given task data is invalid, please try again\n");
    }

    public static void messageNewTask(String taskName, String projectName) {
        System.out.println("Task " + taskName + " has been added to project " + projectName + " successfully\n");
    }
}
