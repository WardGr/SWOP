package UserInterface;

import Application.IncorrectPermissionException;
import Application.InvalidFileException;
import Application.LoadSystemController;
import Domain.*;

import java.util.Scanner;

/**
 * Handles user input for the loadsystem use-case, requests necessary domain-level information from the Application.LoadSystemController
 */
public class LoadSystemUI {
    private final LoadSystemController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public LoadSystemUI(LoadSystemController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
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
    private void loadSystemForm() throws IncorrectPermissionException {
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
        } catch (InvalidFileException e) {
            System.out.println(e.getMessage());
        }
    }
}
