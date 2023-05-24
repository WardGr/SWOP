package UserInterface;

import Application.IncorrectPermissionException;
import Application.UpdateDependenciesController;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;

import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
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
        while (true) {
            try {
                printTasksList(projectName);

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
        while (true) {
            showTaskDependencies(projectName, taskName);

            System.out.println("Please put in the desired command:");
            System.out.println("   addprev/addnext/removeprev/removenext <projectName, taskName>");
            String fullCommand = scanner.nextLine();
            if (fullCommand.equals("BACK")) {
                System.out.println("Returning to task menu...");
                return;
            }

            String[] command = fullCommand.split(" ", 2);
            if (command.length != 2) {
                System.out.println("ERROR: Unrecognized command, try again.");
            } else {
                String[] dependentTaskCommand = command[1].split(", ");
                if (dependentTaskCommand.length == 2){
                    String dependentProjectName = dependentTaskCommand[0];
                    String dependentTaskName = dependentTaskCommand[1];

                    try {
                        switch (command[0]) {
                            case ("addprev") -> getController().addPrevTask(projectName, taskName, dependentProjectName, dependentTaskName);
                            case ("addnext") -> getController().addNextTask(projectName, taskName, dependentProjectName, dependentTaskName);
                            case ("removeprev") -> getController().removePrevTask(projectName, taskName, dependentProjectName, dependentTaskName);
                            case ("removenext") -> getController().removeNextTask(projectName, taskName, dependentProjectName, dependentTaskName);
                            default -> System.out.println("ERROR: Unrecognized command, try again.");
                        }
                    } catch (TaskNotFoundException e) {
                        System.out.println("ERROR: The given task could not be found, try again.");
                    } catch (IncorrectTaskStatusException | LoopDependencyGraphException e) {
                        System.out.println("ERROR: The given task could not safely be added, try again.");
                    }
                } else {
                    System.out.println("\nThe given project and task names are not in the correct form, try again.\n");
                }

            }
        }
    }

    /**
     * Pretty-prints the currently ongoing projects
     *
     * @throws IncorrectPermissionException if the currently logged-in user is not a project manager
     */
    private void showOngoingProjects() throws IncorrectPermissionException {
        System.out.println("***** UNFINISHED PROJECTS *****");

        for (ProjectData projectData : getController().getTaskManSystemData().getOngoingProjectsData()) {
            System.out.println(" - " + projectData.getName());
        }

        if (getController().getTaskManSystemData().getOngoingProjectsData().isEmpty()) {
            System.out.println(" --- There are no unfinished projects in the system");
        }

        System.out.println();
    }

    /**
     * Prints all AVAILABLE/UNAVAILABLE tasks of the given project, which can be updated
     *
     * @param projectName   Name of the project of which to print all tasks
     * @throws ProjectNotFoundException if the given projectName doesn't correspond to a project in the system
     * @throws TaskNotFoundException if a task name doesn't correspond to a task in the system
     * @throws IncorrectPermissionException if the currently logged-in user is not a project manager
     */
    private void printTasksList(String projectName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
        ProjectData projectData = getController().getProjectData(projectName);

        System.out.println("***** TASKS in " + projectName + " *****");
        if (projectData.getTasksData().isEmpty()) {
            System.out.println("There are no active tasks in this project");
        }

        for (TaskData task : projectData.getTasksData()){
            System.out.println(" - " + task.getName() + " --- Status: " + task.getStatus());
        }
        System.out.println();
    }

    /**
     * Prints all dependencies of the given task within the given project
     *
     * @param projectName Name of the project of which the given task is a part of
     * @param taskName    Name of the task of which to print its dependencies
     * @throws IncorrectPermissionException if the currently logged-in user is not a project manager
     * @throws ProjectNotFoundException if the given projectName doesn't correspond to a project in the system
     * @throws TaskNotFoundException if the given task name doesn't correspond to a task in the system
     *
     */
    private void showTaskDependencies(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
        TaskManSystemData taskManSystemData = getController().getTaskManSystemData();
        TaskData taskData = getController().getTaskData(projectName, taskName);

        Set<Tuple<String,String>> possiblePrevTasks = new HashSet<>();
        Set<Tuple<String,String>> possibleNextTasks = new HashSet<>();

        for (ProjectData possibleProjectData : taskManSystemData.getProjectsData()) {
            possiblePrevTasks.addAll(possibleProjectData.getPossiblePrevTasks(taskData).stream().map(t -> new Tuple<>(possibleProjectData.getName(), t.getName())).collect(Collectors.toSet()));
            possibleNextTasks.addAll(possibleProjectData.getPossibleNextTasks(taskData).stream().map(t -> new Tuple<>(possibleProjectData.getName(), t.getName())).collect(Collectors.toSet()));
        }


        System.out.print("\nPrevious tasks: ");
        if (taskData.getPrevTasksData().isEmpty()) {
            System.out.println("There are no previous tasks.");
        }
        for (TaskData prevTaskData : taskData.getPrevTasksData()) {
            System.out.print("Task \"" + prevTaskData.getName() + "\" in project \"" + prevTaskData.getProjectName() + "\"");
        }

        System.out.print("Next tasks: ");
        if (taskData.getNextTasksData().isEmpty()) {
            System.out.println("There are no next tasks.");
        }
        for (TaskData nextTaskData : taskData.getNextTasksData()) {
            System.out.print("Task \"" + nextTaskData.getName() + "\" in project \"" + nextTaskData.getProjectName() + "\"");
        }

        System.out.print("Possible tasks to add as previous task: ");
        if (possiblePrevTasks.isEmpty()) {
            System.out.println("There are no possible previous tasks to add.");
        }
        for (Tuple<String,String> possiblePrevTask : possiblePrevTasks) {
            System.out.print("Task \"" + possiblePrevTask.getSecond() + "\" in project \"" + possiblePrevTask.getFirst() + "\"");
        }

        System.out.print("Possible tasks to add as next task: ");
        if (possibleNextTasks.isEmpty()) {
            System.out.println("There are no possible next tasks to add.");
        }
        for(Tuple<String,String> possibleNextTask : possibleNextTasks) {
            System.out.print("Task \"" + possibleNextTask.getSecond() + "\" in project \"" + possibleNextTask.getFirst() + "\"");
        }
        System.out.println();

    }
}
