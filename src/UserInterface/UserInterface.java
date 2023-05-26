package UserInterface;

import UserInterface.ProjectUIs.ProjectUI;
import UserInterface.ProjectUIs.ShowProjectsUI;
import UserInterface.SystemUIs.AdvanceTimeUI;
import UserInterface.SystemUIs.LoadSystemUI;
import UserInterface.SystemUIs.SessionUI;
import UserInterface.SystemUIs.UndoRedoUI;
import UserInterface.TaskUIs.*;

import java.util.Scanner;

/**
 * First point of interaction with the user, separates use-case specific UI-logic from the user by delegating the work
 * to the respective use-case UI class based on user commands.
 */
public class UserInterface {

    private final SessionUI sessionUI;
    private final ShowProjectsUI showProjectsUI;
    private final ProjectUI projectUI;
    private final CreateTaskUI createTaskUI;
    private final DeleteTaskUI deleteTaskUI;
    private final AdvanceTimeUI advanceTimeUI;
    private final LoadSystemUI loadSystemUI;
    private final StartTaskUI startTaskUI;
    private final EndTaskUI endTaskUI;
    private final UpdateDependenciesUI updateDependenciesUI;
    private final UndoRedoUI undoRedoUI;

    /**
     * Creates the main UI object, setting all its use-case UI's
     */
    public UserInterface(
            SessionUI sessionUI,
            AdvanceTimeUI advanceTimeUI,
            ProjectUI projectUI,
            ShowProjectsUI showProjectsUI,
            CreateTaskUI createTaskUI,
            DeleteTaskUI deleteTaskUI,
            LoadSystemUI loadSystemUI,
            StartTaskUI startTaskUI,
            EndTaskUI endTaskUI,
            UpdateDependenciesUI updateDependenciesUI,
            UndoRedoUI undoRedoUI
    ) {
        this.sessionUI = sessionUI;
        this.advanceTimeUI = advanceTimeUI;
        this.projectUI = projectUI;
        this.showProjectsUI = showProjectsUI;
        this.createTaskUI = createTaskUI;
        this.deleteTaskUI = deleteTaskUI;
        this.loadSystemUI = loadSystemUI;
        this.startTaskUI = startTaskUI;
        this.endTaskUI = endTaskUI;
        this.updateDependenciesUI = updateDependenciesUI;
        this.undoRedoUI = undoRedoUI;
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
            case "createproject" -> projectUI.createProject();
            case "deleteproject" -> projectUI.deleteProject();
            case "createtask" -> createTaskUI.createTask();
            case "deletetask" -> deleteTaskUI.deleteTask();
            case "advancetime" -> advanceTimeUI.advanceTime();
            case "loadsystem" -> loadSystemUI.loadSystem();
            case "starttask" -> startTaskUI.startTask();
            case "endtask" -> endTaskUI.endTask();
            case "updatedependencies" -> updateDependenciesUI.updateDependencies();
            case "undo" -> undoRedoUI.undo();
            case "redo" -> undoRedoUI.redo();
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
        System.out.println("deleteproject:      Allows the user to delete a project in the system");
        System.out.println("createtask:         Shows the task creation prompt to add a task to a project");
        System.out.println("deletetask:         Allows the user to delete a task in the system");
        System.out.println("advancetime:        Allows the user to modify the system time");
        System.out.println("loadsystem:         Allows the user to load projects and tasks into the system");
        System.out.println("starttask:          Allows the user to start a task");
        System.out.println("endtask:            Allows the user to end a task");
        System.out.println("updatedependencies: Allows the user to update the dependencies of a task");
        System.out.println("undo:               Allows the user to undo a previously executed command");
        System.out.println("redo:               Allows the user to redo a previously undone command");
    }
}
