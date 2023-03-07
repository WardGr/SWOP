import java.util.Scanner;

public class UserInterface {
    private final SessionUI sessionUI;
    private final Controller controller;

    public UserInterface() {
        this.sessionUI = new SessionUI();
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
            case "createtask" -> createTask();
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

    /**
     * Prints the login prompt, receives the username and password from the user via the command line, and sends it over
     * to the SessionController.
     */
    private void login() {
        sessionUI.login();
    }

    private void logout() {
        sessionUI.logout();
    }

    /**
     * Executes the showprojects user command, checks if the user is a project manager, then prints all projects
     * and prompts the user to choose a project to expand.
     */
    private void showProjects() {
        // TODO: deze check kan beter ergens anders, is ni echt UI-verantwoordelijkheid

        try {
            String projects = controller.getProjectNames();
            System.out.println("********* PROJECTS *********");
            System.out.println(projects);
        } catch (RuntimeException e) { // Make this an "InvalidRoleError" or something.
            printAccessError(Role.PROJECTMANAGER);
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a projects name to see its details, including a list of its tasks, type BACK to exit:");

        String response = scanner.nextLine();
        if (!response.equals("BACK")) {
            showSpecificProject(response);
        }
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
     * @pre selectedTask consists of a valid project name // TODO: maybe it's not good to force this precondition, and we should instead decouple it by checking defensively
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

        System.out.println("Type BACK to cancel task creation at any time");

        System.out.println("*********** TASK CREATION FORM ***********");
        System.out.println("Project name of which to add the task to:");
        String projectName = scanner.nextLine();
        if (projectName.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        System.out.println("Task name:");
        String taskName = scanner.nextLine();
        if (taskName.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        System.out.println("Task description:");
        String description = scanner.nextLine();
        if (description.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        System.out.println("Task duration:");
        String duration = scanner.nextLine();
        if (duration.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        System.out.println("Task deviation:");
        String deviation = scanner.nextLine();
        if (deviation.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        controller.createTask(projectName, taskName, description, duration, deviation);

    }

    public void printLoginError() {
        System.out.println("Wrong password/username combination, please try again!");
    }

    public void printWelcome(String role) {
        System.out.println("Welcome! Your assigned role is " + role);
    }

    public void printParseError() {
        System.out.println("A given integer field could not be parsed, please ensure you wrote a valid integer in these fields.\n");
    }

    public static void printTaskCreationComplete(String taskName, String projectName) {
        System.out.println("Task " + taskName + " has been added to project " + projectName + " successfully\n");
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in as " + role.toString() + " to access this function." );
    }

    public void printInvalidProjectDataError() {
        System.out.println("The given data does not constitute a valid project, please check the given details.\n");
    }

    public void printInvalidTaskDataError() {
        System.out.println("The given data does not constitute a valid task, please check the given details.\n");
    }
}
