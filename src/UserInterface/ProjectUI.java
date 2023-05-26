package UserInterface;

import Application.ProjectController;
import Application.IncorrectPermissionException;
import Domain.*;

import java.util.Scanner;

/**
 * Handles user input for the createproject use-case, requests necessary domain-level information from the Application.CreateProjectController
 */
public class ProjectUI {

    private final ProjectController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public ProjectUI(ProjectController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private ProjectController getController() {
        return controller;
    }

    /**
     * Does the initial project creation request, checks if the user has the projectmanager role
     */
    public void createProject() {
        if (getController().projectPreconditions()) {
            try {
                createProjectForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }

    /**
     * Shows the project creation form and creates the project if the given information is valid according to system
     * specifications
     *
     * @throws IncorrectPermissionException if the user is not logged in as a projectmanager
     */
    public void createProjectForm() throws IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("Type BACK to cancel project creation at any time");
            System.out.println("*********** PROJECT CREATION FORM ***********");

            System.out.println("Project Name: ");
            String projectName = scanner.nextLine();
            if (projectName.equals("BACK")) {
                System.out.println("Project creation cancelled");
                return;
            }

            System.out.println("Project Description: ");
            String projectDescription = scanner.nextLine();
            if (projectDescription.equals("BACK")) {
                System.out.println("Project creation cancelled");
                return;
            }

            System.out.println("Project due hour: ");
            String dueHourString = scanner.nextLine();

            int dueHour;
            while (true) {
                try {
                    if (dueHourString.equals("BACK")) {
                        System.out.println("Project creation cancelled");
                        return;
                    }
                    dueHour = Integer.parseInt(dueHourString);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Given due hour is not an integer, please input an integer and try again"
                    );
                    dueHourString = scanner.nextLine();
                }
            }

            System.out.println("Project due minute: ");
            String dueMinuteString = scanner.nextLine();

            int dueMinute;
            while (true) {
                try {
                    if (dueMinuteString.equals("BACK")) {
                        System.out.println("Project creation cancelled");
                        return;
                    }
                    dueMinute = Integer.parseInt(dueMinuteString);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Given due minute is not an integer, please input an integer and try again"
                    );
                    dueMinuteString = scanner.nextLine();
                }
            }

            try {
                getController().createProject(projectName, projectDescription, new Time(dueHour, dueMinute));
                System.out.println("Project with name " + projectName + " created!");
                return;
            } catch (ProjectNameAlreadyInUseException e) {
                System.out.println("The given project name is already in use, please try again\n");
            } catch (InvalidTimeException e) {
                System.out.println("The given due minutes are not of the correct format (0-59)\n");
            } catch (DueBeforeSystemTimeException e) {
                System.out.println("The given due time is before the current system time, please try again\n");
            }
        }
    }

    public void deleteProject() {
        if (getController().projectPreconditions()) {
            try {
                deleteProjectForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }

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
