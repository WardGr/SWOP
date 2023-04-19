package UserInterface;

import Application.IncorrectPermissionException;
import Application.UpdateDependenciesController;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskProxy;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class UpdateDependenciesUI {
    private final UpdateDependenciesController controller;

    public UpdateDependenciesUI(UpdateDependenciesController controller) {
        this.controller = controller;
    }

    private UpdateDependenciesController getController() {
        return controller;
    }

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

                System.out.println("Give the name of the project you want to edit:");
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

    private void updateProject(String projectName, Scanner scanner) throws IncorrectPermissionException, ProjectNotFoundException {
        ProjectProxy projectData = getController().getProjectData(projectName);

        while (true) {
            try {
                showRelevantTasks(projectData);

                System.out.println("Give the name of the task you want to edit:");
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

    private void updateTask(String projectName, String taskName, Scanner scanner) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
        ProjectProxy projectData = getController().getProjectData(projectName);
        TaskProxy taskData = getController().getTaskData(projectName, taskName);

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
                            case ("addprev") -> getController().addPreviousTask(projectName, taskName, command[1]);
                            case ("addnext") -> getController().addNextTask(projectName, taskName, command[1]);
                            case ("removeprev") -> {
                                if (taskData.getPreviousTasksNames().contains(command[1])) {
                                    getController().removePreviousTask(projectName, taskName, command[1]);
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

    private void showRelevantTasks(ProjectProxy projectData) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {
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

    private void showTaskDependencies(ProjectProxy projectData, TaskProxy taskData) {
        System.out.print("Previous tasks: ");
        if (taskData.getPreviousTasksNames().size() == 0) {
            System.out.println("There are no previous tasks.");
        } else {
            System.out.println(
                    taskData.getPreviousTasksNames().stream().
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

        List<String> possiblePreviousTasks = projectData.getActiveTasksNames();
        possiblePreviousTasks.removeIf(prevTaskName -> taskData.getName().equals(prevTaskName));
        possiblePreviousTasks.removeIf(prevTaskName -> taskData.getPreviousTasksNames().contains(prevTaskName));
        possiblePreviousTasks.removeIf(prevTaskName -> !taskData.canSafelyAddPrevTask(prevTaskName));
        System.out.print("Possible previous tasks: ");
        if (possiblePreviousTasks.size() == 0) {
            System.out.println("There are no possible previous tasks to add.");
        } else {
            System.out.println(
                    possiblePreviousTasks.stream().
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
