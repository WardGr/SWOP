package UserInterface;

import Application.IncorrectPermissionException;
import Application.LoadSystemController;
import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Scanner;

/**
 * Handles user input for the loadsystem use-case, requests necessary domain-level information from the Application.LoadSystemController
 */
public class LoadSystemUI {
    private final LoadSystemController controller;

    public LoadSystemUI(LoadSystemController controller) {
        this.controller = controller;
    }

    private LoadSystemController getController() {
        return controller;
    }

    /**
     * Initial loadSystem request, checks the role before printing the prompt
     */
    public void loadSystem() {
        if (getController().loadSystemPreconditions()) {
            try {
                loadSystemForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            } catch (InvalidTimeException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }

    /**
     * Prints the system load form, loads  in the given JSON file to initialise the systems projects
     *
     * @throws IncorrectPermissionException if the current user is not a project manager
     */
    private void loadSystemForm() throws IncorrectPermissionException, InvalidTimeException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel system load at any time");
        System.out.println("*********** SYSTEM LOAD FORM ***********");
        System.out.println("please enter the path of the load file: ");
        String path = scanner.nextLine();
        if (path.equals("BACK")) {
            System.out.println("Cancelled system load");
            return;
        }
        try {
            getController().LoadSystem(path);
            System.out.println("system succesfully loaded");
        } catch (NewTimeBeforeSystemTimeException | ParseException | TaskNotFoundException | UserNotFoundException |
                 ProjectNameAlreadyInUseException | InvalidTimeException | IncorrectUserException |
                 IncorrectTaskStatusException | DueBeforeSystemTimeException | ProjectNotFoundException |
                 TaskNameAlreadyInUseException | EndTimeBeforeStartTimeException | UserAlreadyAssignedToTaskException |
                 LoopDependencyGraphException | IncorrectRoleException | NonDeveloperRoleException |
                 RoleNotFoundException | DueTimeBeforeCreationTimeException | ProjectNotOngoingException e) {
            System.out.println("ERROR: invalid file logic");
            getController().clear();
        } catch (IOException e) {
            System.out.println("ERROR: file not found");
            getController().clear();
        }
    }
}
