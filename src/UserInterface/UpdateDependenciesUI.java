package UserInterface;

import Application.IncorrectPermissionException;
import Application.UpdateDependenciesController;
import Domain.*;
import Domain.TaskStates.TaskProxy;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateDependenciesUI {
    private final UpdateDependenciesController controller;

    public UpdateDependenciesUI(UpdateDependenciesController controller){
        this.controller = controller;
    }

    private UpdateDependenciesController getController(){
        return controller;
    }

    public void updateDependencies(){
        if (!getController().updateDependenciesPreconditions()){
            System.out.println("ERROR: You must be a project manager to call this function");
            return;
        }

        try {
            Scanner scanner = new Scanner(System.in);

            while(true){
                showOngoingProjects();

                System.out.println("Give the name of the project you want to edit:");
                String projectName = scanner.nextLine();
                if (projectName.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }

                try{
                    ProjectProxy projectData = getController().getProjectData(projectName);

                    while(true){
                        showRelevantTasks(projectData);

                        System.out.println("Give the name of the task you want to edit:");
                        String taskName = scanner.nextLine();
                        if (taskName.equals("BACK")) {
                            System.out.println("Cancelled task creation");
                            return;
                        }

                        try{
                            TaskProxy taskData = getController().getTaskData(projectName, taskName);
                            if (taskData.getStatus() == Status.UNAVAILABLE || taskData.getStatus() == Status.AVAILABLE){
                                while(true) {
                                    showTaskDependencies(projectData, taskData);

                                    System.out.println("Please put in the desired command: ");
                                    System.out.println("   addprev/addnext/removeprev/removenext <taskName>");
                                    String fullCommand = scanner.nextLine();
                                    if (fullCommand.equals("BACK")) {
                                        System.out.println("Cancelled task creation");
                                        return;
                                    }

                                    // TODO cut the fullCommand in two parts and do checks

                                    switch (fullCommand) {
                                        case ("addprev") -> {}// TODO: checken of het in orde is via taskData
                                        case ("addnext") -> {}// TODO: checken of het in orde is via taskData
                                        case ("removeprev") -> {} // TODO: checken of de task in de prev zit via taskData
                                        case ("removenext") -> {} // TODO same
                                        default -> System.out.println("Unrecognized command");
                                    }
                                }
                            } else {
                                System.out.println("ERROR: Chosen task is not (un)available");
                            }
                        } catch (TaskNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (ProjectNotFoundException e) {
                    System.out.println("ERROR: The given project name could not be found.");
                }


            }
        } catch (IncorrectPermissionException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void showOngoingProjects() throws IncorrectPermissionException {
        System.out.println("***** UNFINISHED PROJECTS *****");
        if (getController().getTaskManSystemData().getProjectNames().size() == 0){
            System.out.println(" --- There are no unfinished projects in the system");
        }
        for (String projectName : getController().getTaskManSystemData().getProjectNames()){
            try{
                if (getController().getProjectData(projectName).getStatus() == ProjectStatus.ONGOING){
                    System.out.println(" - " + projectName);
                }
            } catch (ProjectNotFoundException e) {
                throw new RuntimeException(); // TODO mag echt niet gebeuren!
            }
        }
        System.out.println();
    }

    private void showRelevantTasks(ProjectProxy projectData) throws IncorrectPermissionException {
        System.out.println("***** (UN)AVAILABLE TASKS *****");
        if (projectData.getActiveTasksNames().size() == 0){
            System.out.println("There are no (un)available tasks in this project");
            return;
        }

        for (String taskName : projectData.getActiveTasksNames()){
            try{
                System.out.println(" - " + taskName + " with status: " +
                        getController().getTaskData(projectData.getName(), taskName).getStatus().toString());
            } catch (ProjectNotFoundException | TaskNotFoundException e) {
                throw new RuntimeException(e); // TODO mag echt niet!
            }
        }
        System.out.println();
    }

    private void showTaskDependencies(ProjectProxy projectData, TaskProxy taskData){
        System.out.print("Previous tasks: ");
        if (taskData.getPreviousTasksNames().size() == 0){
            System.out.println("There are no previous tasks.");
        } else {
            System.out.println(
                    taskData.getPreviousTasksNames().stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }
        System.out.print("Next tasks: ");
        if (taskData.getNextTasksNames().size() == 0){
            System.out.println("There are no previous tasks.");
        } else {
            System.out.println(
                    taskData.getNextTasksNames().stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }

        List<String> possiblePreviousTasks = projectData.getActiveTasksNames();
        possiblePreviousTasks.removeIf(prevTaskName -> !taskData.safeAddPrevTask(prevTaskName));
        System.out.print("Possible previous tasks: ");
        if (possiblePreviousTasks.size() == 0){
            System.out.println("There are no possible previous tasks to add.");
        } else {
            System.out.println(
                    possiblePreviousTasks.stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }

        List<String> possibleNextTasks = projectData.getActiveTasksNames();
        possibleNextTasks.removeIf(nextTaskName -> !taskData.safeAddNextTask(nextTaskName));
        System.out.print("Possible previous tasks: ");
        if (possibleNextTasks.size() == 0){
            System.out.println("There are no possible previous tasks to add.");
        } else {
            System.out.println(
                    possibleNextTasks.stream().
                            map(Object::toString).
                            collect(Collectors.joining(", ")));
        }
    }
}
