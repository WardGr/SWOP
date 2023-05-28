package UserInterface.TaskUIs;

import Application.IncorrectPermissionException;
import Application.Controllers.TaskControllers.StartTaskController;
import Application.Controllers.TaskControllers.UnconfirmedActionException;
import Domain.Project.ProjectData;
import Domain.Project.TaskNotFoundException;
import Domain.Task.IncorrectTaskStatusException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.Task.IncorrectRoleException;
import Domain.Task.TaskData;
import Domain.User.Role;
import Domain.User.UserAlreadyAssignedToTaskException;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handles user input for the starttask use-case, requests necessary domain-level information from the StartTaskController
 */
public class StartTaskUI {
    private final StartTaskController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public StartTaskUI(StartTaskController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private StartTaskController getController() {
        return controller;
    }

    /**
     *  Prints the start task prompt, giving the currently logged-in user the ability to start working on a task of
     *  their choice in a project of their choice.
     */
    public void startTask() {
        if (!getController().startTaskPreconditions()) {
            System.out.println("ERROR: You need a developer role to call this function.");
            return;
        }
        try {
            startTaskForm();
        } catch (BackException e) {
            System.out.println("Cancelled starting task");
        } catch (IncorrectPermissionException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Shows the start task form and starts the task if the given information is valid
     *
     * @throws BackException                    if the user input BACK to cancel the operation
     * @throws IncorrectPermissionException     if the user is not logged in as a developer
     */
    private void startTaskForm() throws BackException, IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                printTasksList();

                System.out.println("Please give the project name you want to start working in:");
                String projectName = scanner.nextLine();
                if (projectName.equals("BACK")) {
                    System.out.println("Cancelled starting task");
                    return;
                }

                System.out.println("Please give the task name you want to start working on in project " + projectName + ":");
                String taskName = scanner.nextLine();
                if (taskName.equals("BACK")) {
                    System.out.println("Cancelled starting task");
                    return;
                }

                TaskData taskData = getController().getTaskData(projectName, taskName);

                System.out.print("You have roles: ");
                Set<Role> userRoles = getController().getUserRoles();
                if (userRoles.size() > 0) {
                    System.out.println(
                            userRoles.stream().
                                    map(Object::toString).
                                    collect(Collectors.joining(", ")));
                } else {
                    System.out.println("You currently don't have any roles");
                }
                System.out.print("Task requires roles: ");
                List<Role> taskRoles = taskData.getUnfulfilledRoles();
                if (taskRoles.size() > 0) {
                    System.out.println(
                            taskRoles.stream().
                                    map(Object::toString).
                                    collect(Collectors.joining(", ")));
                } else {
                    System.out.println("The task doesn't require any roles");
                }

                Role role = null;
                while (role == null) {
                    System.out.println("Give the role you want to fulfill in task " + taskName + ":");
                    String roleString = scanner.nextLine();
                    if (roleString.equals("BACK")) {
                        System.out.println("Cancelled starting task " + taskName);
                        return;
                    }

                    switch (roleString) {
                        case ("sysadmin") -> role = Role.SYSADMIN;
                        case ("java programmer") -> role = Role.JAVAPROGRAMMER;
                        case ("python programmer") -> role = Role.PYTHONPROGRAMMER;
                        default -> System.out.println("Unrecognized developer role");
                    }
                }

                //CONFIRM
                showTask(taskData);
                boolean startTaskConfirmation = getBooleanInput(scanner, "Start working on this task as a " + role +
                        " at the current system time: " +
                        getController().getTaskManSystemData().getSystemTime().toString() + "\nConfirm?");

                if (!startTaskConfirmation) {
                    System.out.println("Cancelled starting task " + taskName);
                    return;
                }

                // PENDING TASK
                boolean confirmation = false;
                if (getController().startTaskNeedsConfirmation()) {
                    String pendingTaskName = getController().getUserTaskData().getName();
                    System.out.println("You are already pending for task " + pendingTaskName);

                    confirmation = getBooleanInput(scanner, "Confirm you want to stop pending for task " + pendingTaskName + " and start working on task " + taskName + "?");
                }

                getController().startTask(projectName, taskName, role, confirmation);
                System.out.println("Successfully started working on task " + taskName + " in project " + projectName +
                        " as " + role);


                System.out.println();
                return;

            } catch (ProjectNotFoundException e) {
                System.out.println("ERROR: Given project could not be found");
            } catch (TaskNotFoundException e) {
                System.out.println("ERROR: Given task could not be found");
            } catch (IncorrectRoleException | UnconfirmedActionException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (IncorrectTaskStatusException e) {
                System.out.println("ERROR: Given task does not have the right status to start");
            } catch (UserAlreadyAssignedToTaskException e) {
                System.out.println("ERROR: User is already executing a task");
            }
        }
    }

    /**
     * Pretty-prints a list of all available or pending tasks which the current user can start
     *
     * @throws IncorrectPermissionException if the user is not logged-in as a developer\
     */
    private void printTasksList() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
        System.out.println("***** LIST OF AVAILABLE OR PENDING TASKS *****");
        for (ProjectData projectData : getController().getTaskManSystemData().getOngoingProjectsData()) {

            for (TaskData taskData : projectData.getAvailableAndPendingTasksData()) {
                Set<Role> userRoles = getController().getUserRoles();
                userRoles.retainAll(taskData.getUnfulfilledRoles());

                if (userRoles.size() > 0) {
                    System.out.println(" - Task: " + taskData.getName() + ", belonging to Project: " + projectData.getName());
                }
            }
        }
        System.out.println();
    }

    /**
     * Pretty-prints the given taskdata
     *
     * @param taskData the taskdata to pretty-print
     */
    private void showTask(TaskData taskData) {
        System.out.println("******** TASK DETAILS ********");

        System.out.println("Task Name:            " + taskData.getName());
        System.out.println("Belonging to project: " + taskData.getProjectName());
        System.out.println("Description:          " + taskData.getDescription());
        System.out.println("Estimated Duration:   " + taskData.getEstimatedDuration().toString());
        System.out.println("Status:               " + taskData.getStatus().toString() + "\n");

        System.out.print("Replaces Task:      ");
        if (taskData.getReplacesTaskName() == null) {
            System.out.println("Replaces no tasks\n");
        } else {
            System.out.println(taskData.getReplacesTaskName() + '\n');
        }

        System.out.println("Required roles:");
        if (taskData.getUnfulfilledRoles().size() > 0) {
            for (Role role : taskData.getUnfulfilledRoles()) {
                System.out.println("- " + role.toString());
            }
        } else {
            System.out.println("All roles are filled in.");
        }
        System.out.println();

        System.out.println("Committed users:");
        if (taskData.getUserNamesWithRole().size() > 0) {
            taskData.getUserNamesWithRole().forEach((userName, role) -> System.out.println("- " + userName + " as " + role.toString()));
        } else {
            System.out.println("No users are committed to this task.");
        }
        System.out.println();
    }


    /**
     * Translates user input to a boolean
     *
     * @param scanner           the scanner to read input from
     * @param message           the message to show to the user if the input is invalid
     * @return                  true if the user inputted 'y', false if the user inputted 'n'
     * @throws BackException    if the user inputted 'BACK'
     */
    private boolean getBooleanInput(Scanner scanner, String message) throws BackException {
        System.out.println(message + " (y/n)\n");
        String answer = scanner.nextLine();
        if (answer.equals("BACK")) {
            throw new BackException();
        }

        while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println("Input has to be 'y' or 'n', try again");
            System.out.println(message + " (y/n)\n");
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