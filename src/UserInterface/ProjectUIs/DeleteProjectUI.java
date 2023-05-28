package UserInterface.ProjectUIs;

import Application.Controllers.ProjectControllers.DeleteProjectController;
import Application.IncorrectPermissionException;
import Domain.Project.ProjectData;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystemData;
import Domain.User.Role;

import java.util.Scanner;

/**
 * Handles user input for the create and delete use-case, requests necessary domain-level information from the Application.ProjectController
 */
public class DeleteProjectUI {

    private final DeleteProjectController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public DeleteProjectUI(DeleteProjectController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private DeleteProjectController getController() {
        return controller;
    }

    /**
     * Does the initial project deletion request, checks if the user has the projectmanager role
     */
    public void deleteProject() {
        if (getController().deleteProjectPreconditions()) {
            try {
                deleteProjectForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }

    /**
     * Shows the project deletion form allows the user to delete the project if the given information is valid according to system
     * @throws IncorrectPermissionException     if the user is not logged in as a projectmanager
     */
    private void deleteProjectForm() throws IncorrectPermissionException{
        Scanner scanner = new Scanner(System.in);

        while(true) {
            printProjectList();

            System.out.println("Project Name to Delete (type 'BACK' to return): ");
            String projectName = scanner.nextLine();
            if (projectName.equals("BACK")) {
                System.out.println("Project deletion cancelled");
                return;
            }

            try {
                getController().deleteProject(projectName);
                System.out.println("Project successfully deleted\n");
                return;
            } catch (ProjectNotFoundException e) {
                System.out.println();
                System.out.println("WARNING: Project couldn't be found, try again");
                System.out.println();
            }
        }
    }

    /**
     * Prints a list of all projects in the system along with the number of tasks they contain
     * @throws IncorrectPermissionException    if the user is not logged in as a projectmanager
     */
    private void printProjectList() throws IncorrectPermissionException {
        System.out.println(" *** PROJECT LIST ***");
        TaskManSystemData taskManSystemData = getController().getTaskManSystemData();
        if (taskManSystemData.getProjectsData().size() == 0){
            System.out.println("There are currently no projects in the system.");
        }
        for (ProjectData projectData : taskManSystemData.getProjectsData()){
            System.out.println("- " + projectData.getName() + " --- Containing " + projectData.getTotalTaskCount() + " Task(s)");
        }
        System.out.println();
    }
}
