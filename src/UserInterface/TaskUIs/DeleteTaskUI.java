package UserInterface.TaskUIs;

import Application.TaskControllers.DeleteTaskController;
import Application.TaskControllers.UnconfirmedActionException;
import Application.TaskControllers.CreateTaskController;
import Application.IncorrectPermissionException;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.Project.ProjectData;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystemData;
import Domain.User.Role;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles user input for the deletetask use-case, requests necessary domain-level information from the Application.DeleteTaskController
 */
public class DeleteTaskUI {

    private final DeleteTaskController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public DeleteTaskUI(DeleteTaskController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private DeleteTaskController getController() {
        return controller;
    }



    /**
     * Initial request from the UserInterface class to the deletetask usecase, checks permissions and then prints the
     * delete task form
     */
    public void deleteTask(){
        if (getController().taskPreconditions()) {
            try {
                deleteTaskForm();
            } catch (IncorrectPermissionException e) {
                System.out.println('\n' + e.getMessage() + '\n');
            } catch (BackException e){
                System.out.println("\nCancelled Task Deletion\n");
            }
        } else {
            System.out.println("\nYou must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n");
        }
    }

    /**
     * Shows the task deletion form to the user, asking for the project name and task name of the task to be deleted
     *
     * @throws IncorrectPermissionException     If the user is not logged in with the correct role
     * @throws BackException                    If the user inputs "BACK"
     */
    private void deleteTaskForm() throws IncorrectPermissionException, BackException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Use 'BACK' to return to main menu\n");

        while (true){
            try {
                printProjectsList();
                System.out.println("Give the project name in which you want to delete a task:");
                String projectName = scanner.nextLine();
                if (projectName.equals("BACK")){
                    throw new BackException();
                }

                printTasksList(projectName);
                System.out.println("Give the task name you want to delete:");
                String taskName = scanner.nextLine();
                if (taskName.equals("BACK")){
                    throw new BackException();
                }

                boolean confirmation = false;
                if (getController().needDeleteConfirmation(projectName, taskName)){
                    confirmation = confirmTaskDeletion(scanner, projectName, taskName);
                }

                getController().deleteTask(projectName, taskName, confirmation);
                System.out.println("Successfully deleted Task\n");
                return;

            } catch (ProjectNotFoundException e) {
                System.out.println("Given project name could not be found, try again\n");
            } catch (TaskNotFoundException e) {
                System.out.println("Given task name could not be found, try again\n");
            } catch (UnconfirmedActionException e) {
                System.out.println(e.getMessage() + '\n');
            }

        }
    }

    /**
     * Prints the list of all projects to the user, along with the number of tasks in each project
     *
     * @throws ProjectNotFoundException
     */
    private void printProjectsList() throws ProjectNotFoundException {
        System.out.println(" *** PROJECTS ***");
        TaskManSystemData taskManSystemData = getController().getTaskManSystemData();
        for (ProjectData projectData : taskManSystemData.getProjectsData()){
            System.out.println(" - " + projectData.getName() + " | Containing " + projectData.getTotalTaskCount() + " Tasks");
        }
    }

    /**
     * Prints the list of all tasks in the given project to the user
     *
     * @param projectName               Name of the project to print the tasks of
     * @throws ProjectNotFoundException If the given project name could not be found
     * @throws TaskNotFoundException    If the given task name could not be found
     */
    private void printTasksList(String projectName) throws ProjectNotFoundException, TaskNotFoundException {
        ProjectData projectData = getController().getProjectData(projectName);
        System.out.println(" *** TASKS in " + projectName + " ***");
        if (projectData.getTasksData().isEmpty() && projectData.getReplacedTasksData().isEmpty()) {
            System.out.println("There are no tasks in this project");
        }
        for (TaskData task : projectData.getTasksData()){
            System.out.println(" - " + task.getName());
        }
        for (TaskData task : projectData.getReplacedTasksData()){
            System.out.println(" - " + task.getName() + " - Replaced by: " + task.getReplacementTaskName());
        }
        System.out.println();
    }

    /**
     * Asks the user for confirmation to delete the given task
     *
     * @param scanner       Scanner to read input from
     * @param projectName   Name of the project the task to be deleted is in
     * @param taskName      Name of the task to be deleted
     * @throws BackException                If the user inputs "BACK"
     * @throws ProjectNotFoundException     If the given project name could not be found
     * @throws TaskNotFoundException        If the given task name could not be found
     */
    private boolean confirmTaskDeletion(Scanner scanner, String projectName, String taskName) throws BackException, ProjectNotFoundException, TaskNotFoundException {
        TaskData taskData = getController().getTaskData(projectName, taskName);

        System.out.println("\nTask " + taskName + " has status " + taskData.getStatus());
        System.out.println("   With users committed: ");
        Set<String> userNames = taskData.getUserNamesWithRole().keySet();
        System.out.println(
                userNames.stream().
                        map(Object::toString).
                        collect(Collectors.joining(", ")));
        return getBooleanInput(scanner, "Confirm you want to delete this task.");
    }



    /**
     * Retrieves user boolean input
     * @param scanner   Scanner to read input from
     * @param message   Message to print to the user
     * @return          True if user inputs "y", false if user inputs "n"
     * @throws BackException    If the user inputs "BACK"
     */
    private boolean getBooleanInput(Scanner scanner, String message) throws BackException {
        System.out.println(message + " (y/n)");
        String answer = scanner.nextLine();
        if (answer.equals("BACK")) {
            throw new BackException();
        }

        while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println("\nInput has to be 'y' or 'n', try again");
            System.out.println(message + " (y/n)");
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
                throw new BackException();
            }
        }

        return answer.equals("y");
    }


    private static class BackException extends Exception {
        public BackException() {super();}
    }
}
