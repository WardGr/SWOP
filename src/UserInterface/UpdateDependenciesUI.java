package UserInterface;

import Application.IncorrectPermissionException;
import Application.UpdateDependenciesController;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Handles user input for the updatedependencies use-case, requests necessary domain-level information from the UpdateDependenciescontroller
 */
public class UpdateDependenciesUI {
    private final UpdateDependenciesController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public UpdateDependenciesUI(UpdateDependenciesController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private UpdateDependenciesController getController() {
        return controller;
    }

    /**
     * Shows the update dependency form, allowing the user to add next or previous tasks to a task of their choice
     */
    public void updateDependencies() {
        if (!getController().updateDependenciesPreconditions()) {
            System.out.println("ERROR: You must be a project manager to call this function");
            return;
        }

        try {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("You can always use 'BACK' to return to previous menu\n");

                showOngoingProjects();

                System.out.println("Give the name of the project you want to update:");
                String projectName = scanner.nextLine();
                if (projectName.equals("BACK")) {
                    System.out.println("Quiting updating task dependencies");
                    return;
                }

                try {
                    updateProject(projectName, scanner);
                } catch (ProjectNotFoundException e) {
                    System.out.println("ERROR: The given project name could not be found.");
                }
            }
        } catch (IncorrectPermissionException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Prompts the user to choose a task to update in the given project
     *
     * @param projectName   Name of the project within which to update the task
     * @param scanner       Scanner object to retrieve the desired user input
     * @throws IncorrectPermissionException if the current user is not logged in with the project manager role
     * @throws ProjectNotFoundException     if projectName does not correspond to an existing project in the system
     */
    private void updateProject(String projectName, Scanner scanner) throws IncorrectPermissionException, ProjectNotFoundException {
        ProjectData projectData = getController().getProjectData(projectName);

        while (true) {
            try {
                printUpdateableTasks(projectData);

                System.out.println("Give the name of the task you want to update:");
                String taskName = scanner.nextLine();
                if (taskName.equals("BACK")) {
                    System.out.println("Returning to project menu...");
                    return;
                }

                updateTask(projectName, taskName, scanner);
            } catch (TaskNotFoundException e) {
                System.out.println("ERROR: Given task name could not be found, try again.");
            }
        }
    }

    /**
     * Prompts the user to update a specific task, allowing them to add/remove previous and next tasks from it
     *
     * @param projectName Name of the project which the given task is a part of
     * @param taskName    The task which dependencies to update
     * @param scanner     Scanner object to get user input from
     * @throws IncorrectPermissionException if the currently logged-in user is not a project manager
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project within the system
     * @throws TaskNotFoundException        if the given taskName does not correspond to an existing task within the given project
     */
    private void updateTask(String projectName, String taskName, Scanner scanner) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
        ProjectData projectData = getController().getProjectData(projectName);
        TaskData taskData = getController().getTaskData(projectName, taskName);

        if (taskData.getStatus() == Status.UNAVAILABLE || taskData.getStatus() == Status.AVAILABLE) {
            while (true) {
                showTaskDependencies(projectData, taskData);

                System.out.println("Please put in the desired command:");
                System.out.println("   addprev/addnext/removeprev/removenext <taskName>");
                String fullCommand = scanner.nextLine();
                if (fullCommand.equals("BACK")) {
                    System.out.println("Returning to task menu...");
                    return;
                }

                String[] command = fullCommand.split(" ", 2);
                if (command.length != 2) {
                    System.out.println("ERROR: Unrecognized command, try again.");
                } else {
                    try {
                        switch (command[0]) {
                            case ("addprev") -> getController().addPrevTask(projectName, taskName, command[1]);
                            case ("addnext") -> getController().addNextTask(projectName, taskName, command[1]);
                            case ("removeprev") -> {
                                if (taskData.getPrevTaskNames().contains(command[1])) {
                                    getController().removePrevTask(projectName, taskName, command[1]);
                                } else {
                                    System.out.println("ERROR: Given task name is not present in previous tasks, try again.");
                                }
                            }
                            case ("removenext") -> {
                                if (taskData.getNextTasksNames().contains(command[1])) {
                                    getController().removeNextTask(projectName, taskName, command[1]);
                                } else {
                                    System.out.println("ERROR: Given task name is not present in next tasks, try again.");
                                }
                            }
                            default -> System.out.println("ERROR: Unrecognized command, try again.");
                        }
                    } catch (TaskNotFoundException e) {
                        System.out.println("ERROR: The given task could not be found, try again.");
                    } catch (IncorrectTaskStatusException | LoopDependencyGraphException e) {
                        System.out.println("ERROR: The given task could not safely be added/removed, try again.");
                    }
                }
            }
        } else {
            System.out.println("ERROR: Chosen task is not (un)available");
        }
    }

    /**
     * Pretty-prints the currently ongoing projects
     *
     * @throws IncorrectPermissionException if the currently logged-in user is not a project manager
     */
    private void showOngoingProjects() throws IncorrectPermissionException {
        System.out.println("***** UNFINISHED PROJECTS *****");
        if (getController().getTaskManSystemData().getProjectNames().size() == 0) {
            System.out.println(" --- There are no unfinished projects in the system");
        }
        for (String projectName : getController().getTaskManSystemData().getProjectNames()) {
            try {
                if (getController().getProjectData(projectName).getStatus() == ProjectStatus.ONGOING) {
                    System.out.println(" - " + projectName);
                }
            } catch (ProjectNotFoundException e) {
                throw new RuntimeException();
            }
        }
        System.out.println();
    }

    /**
     * Prints all AVAILABLE/UNAVAILABLE tasks of the given project, which can be updated
     *
     * @param projectData   Data object of the project of which to print all updateable tasks
     * @throws IncorrectPermissionException if the currently logged-in user is not a project manager
     */
    private void printUpdateableTasks(ProjectData projectData) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
        System.out.println("***** (UN)AVAILABLE TASKS *****");
        List<String> unAvailableTasks = projectData.getActiveTasksNames();
        for (String taskName : projectData.getActiveTasksNames()){
            if (getController().getTaskData(projectData.getName(), taskName).getStatus() != Status.AVAILABLE &&
                    getController().getTaskData(projectData.getName(), taskName).getStatus() != Status.UNAVAILABLE){
                unAvailableTasks.remove(taskName);
            }
        }
        if (unAvailableTasks.size() == 0) {
            System.out.println("There are no (un)available tasks in this project");
            System.out.println();
            return;
        }

        for (String taskName : unAvailableTasks) {
            try {
                System.out.println(" - " + taskName + " with status: " +
                        getController().getTaskData(projectData.getName(), taskName).getStatus().toString());
            } catch (ProjectNotFoundException | TaskNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();
    }

    /**
     * Prints all dependencies of the given task within the given project
     *
     * @param projectData Data object of the project of which the given task is a part of
     * @param taskData    Data object of the task of which to print its dependencies
     */
    private void showTaskDependencies(ProjectData projectData, TaskData taskData) {
        System.out.print("Previous tasks: ");
        if (taskData.getPrevTaskNames().size() == 0) {
            System.out.println("There are no previous tasks.");
        } else {
            System.out.println(
                    taskData.getPrevTaskNames().stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }
        System.out.print("Next tasks: ");
        if (taskData.getNextTasksNames().size() == 0) {
            System.out.println("There are no next tasks.");
        } else {
            System.out.println(
                    taskData.getNextTasksNames().stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }

        List<String> possiblePrevTasks = projectData.getActiveTasksNames();
        possiblePrevTasks.removeIf(prevTaskName -> taskData.getName().equals(prevTaskName));
        possiblePrevTasks.removeIf(prevTaskName -> taskData.getPrevTaskNames().contains(prevTaskName));
        possiblePrevTasks.removeIf(prevTaskName -> !taskData.canSafelyAddPrevTask(prevTaskName));
        System.out.print("Possible previous tasks: ");
        if (possiblePrevTasks.size() == 0) {
            System.out.println("There are no possible previous tasks to add.");
        } else {
            System.out.println(
                    possiblePrevTasks.stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }

        List<String> possibleNextTasks = projectData.getActiveTasksNames();
        possibleNextTasks.removeIf(nextTaskName -> nextTaskName.equals(taskData.getName()));
        possibleNextTasks.removeIf(nextTaskName -> taskData.getNextTasksNames().contains(nextTaskName));
        possibleNextTasks.removeIf(nextTaskName -> {
            try {
                return !getController().getTaskData(projectData.getName(), nextTaskName).canSafelyAddPrevTask(taskData.getName());
            } catch (ProjectNotFoundException | TaskNotFoundException | IncorrectPermissionException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.print("Possible next tasks: ");
        if (possibleNextTasks.size() == 0) {
            System.out.println("There are no possible next tasks to add.");
        } else {
            System.out.println(
                    possibleNextTasks.stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }
    }
}
