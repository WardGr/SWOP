package UserInterface;

import Application.IncorrectPermissionException;
import Application.ShowProjectsController;
import Domain.*;
import Domain.TaskStates.FinishedStatus;
import Domain.TaskStates.TaskData;

import java.util.List;
import java.util.Scanner;

/**
 * Handles user input for the showprojects use-case, requests necessary domain-level information from the Application.ShowProjectsController
 */
public class ShowProjectsUI {

    private final ShowProjectsController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public ShowProjectsUI(ShowProjectsController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private ShowProjectsController getController() {
        return controller;
    }

    /**
     * Initial showprojects request: shows all projects if user is logged in as a project manager
     */
    public void showProjects() {
        if (getController().showProjectsPreconditions()) {
            try {
                showProjectsForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }

    /**
     * Shows all projects, displays detailed information about a given project if the user requests it, and allows the
     * user to request information about said projects tasks. User can exit by typing "BACK" at any prompt.
     */
    public void showProjectsForm() throws IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Type \"BACK\" to cancel");
            System.out.println("********* PROJECTS *********");
            showProjectsWithStatuses();

            System.out.println("Type the name of a project to see more details:");
            String response = scanner.nextLine();
            if (response.equals("BACK")) {
                return;
            }
            try {
                chooseProject(response, scanner);
            } catch (ProjectNotFoundException e) {
                System.out.println("The given project could not be found\n");
            }
        }
    }

    /**
     * Shows detailed information about a given project, and allows the user to request details about any of its tasks
     *
     * @param projectName name of project the user wants to see more details of
     * @throws ProjectNotFoundException     if projectName does not correspond to an existing projects' name
     * @throws IncorrectPermissionException if user is not a project manager
     */
    private void chooseProject(String projectName, Scanner scanner) throws ProjectNotFoundException, IncorrectPermissionException {
        showProject(projectName);

        while (true) {
            try {
                System.out.println("Type the name of a task to see more details, or type \"BACK\" to choose another project:");
                String response = scanner.nextLine();

                if (response.equals("BACK")) {
                    return;
                }
                showTask(projectName, response);
            } catch (TaskNotFoundException e) {
                System.out.println("The given task could not be found, please try again\n");
            }
        }
    }

    /**
     * Pretty-prints projects with their given statuses
     *
     * @throws IncorrectPermissionException if current session is not held by a project manager
     */
    private void showProjectsWithStatuses() throws IncorrectPermissionException {
        List<String> projectNames = getController().getTaskManSystemData().getProjectNames();
        for (String projectName : projectNames) {
            try {
                ProjectStatus status = getController().getProjectData(projectName).getStatus();
                System.out.println(projectName + ", status: " + status.toString());
            } catch (ProjectNotFoundException e) {
                System.out.println("The given project could not be found\n");
            }
        }
    }

    /**
     * Pretty-prints given project string
     *
     * @param projectName Project name corresponding to the project that should be pretty-printed
     * @throws ProjectNotFoundException         if the given projectName does not correspond to an existing project
     * @throws IncorrectPermissionException     if the currently logged-in user is not a project manager
     */
    private void showProject(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        ProjectData projectData = getController().getProjectData(projectName);
        System.out.println("******** PROJECT DETAILS ********");

        System.out.println("Project Name:  " + projectData.getName());
        System.out.println("Description:   " + projectData.getDescription());
        System.out.println("Creation Time: " + projectData.getCreationTime().toString());
        System.out.println("Due Time:      " + projectData.getDueTime().toString());
        System.out.println("Status:        " + projectData.getStatus().toString());

        System.out.println();
        System.out.println("Tasks:");

        if (projectData.getActiveTasksNames().size() > 0) {
            int index = 1;
            for (String taskName : projectData.getActiveTasksNames()) {
                System.out.println(index++ + ". " + taskName);
            }
        } else {
            System.out.println("There are no active tasks attached to this project.");
        }

        System.out.println();
        System.out.println("Replaced Tasks:");

        if (projectData.getReplacedTasksNames().size() > 0) {
            int index = 1;
            for (String taskName : projectData.getReplacedTasksNames()) {
                try {
                    System.out.print(index++ + ". " + taskName);
                    String replacedBy = getController().getTaskData(projectName, taskName).getReplacementTaskName();
                    if (replacedBy != null) {
                        System.out.print(" - Replaced by: " + replacedBy);
                    }
                } catch (TaskNotFoundException e) {
                    System.out.print("\n The given task could not be found");
                }
                System.out.println();
            }
        } else {
            System.out.println("There are no tasks replaced in this project.");
        }
        System.out.println();

    }

    /**
     * Pretty-prints the task with given taskName in the project with given projectName
     *
     * @param projectName   Project name of project to which the task belongs
     * @param taskName      Task name of task which belongs to the project
     * @throws ProjectNotFoundException         if the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException            if the given taskName does not correspond to an existing task within the given project
     * @throws IncorrectPermissionException     if the currently logged-in user is not a project manager
     */
    private void showTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        TaskData taskData = getController().getTaskData(projectName, taskName);

        System.out.println("******** TASK DETAILS ********");

        System.out.println("Task Name:            " + taskData.getName());
        System.out.println("Belonging to project: " + taskData.getProjectName());
        System.out.println("Description:          " + taskData.getDescription());
        System.out.println("Estimated Duration:   " + taskData.getEstimatedDuration().toString());
        System.out.println("Accepted Deviation:   " + taskData.getAcceptableDeviation());
        System.out.println("Status:               " + taskData.getStatus().toString());
        try{
            FinishedStatus finishedState = taskData.getFinishedStatus();
            System.out.println("   Finished:          " + finishedState.toString() + '\n');
        } catch (IncorrectTaskStatusException e) {
            System.out.println();
        }

        System.out.print("Replacement Task:   ");
        if (taskData.getReplacementTaskName() == null) {
            System.out.println("No replacement task");
        } else {
            System.out.println(taskData.getReplacementTaskName());
        }
        System.out.print("Replaces Task:      ");
        if (taskData.getReplacesTaskName() == null) {
            System.out.println("Replaces no tasks\n");
        } else {
            System.out.println(taskData.getReplacesTaskName() + '\n');
        }

        System.out.print("Start Time:         ");
        if (taskData.getStartTime() == null) {
            System.out.println("Task has not started yet");
        } else {
            System.out.println(taskData.getStartTime().toString());
        }
        System.out.print("End Time:           ");
        if (taskData.getEndTime() == null) {
            System.out.println("Task has not ended yet\n");
        } else {
            System.out.println(taskData.getEndTime().toString() + '\n');
        }

        System.out.println("Unfulfilled roles:");
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


        System.out.println("Next tasks:");
        if (taskData.getNextTasksNames().size() > 0) {
            int i = 1;
            for (Tuple<String,String> nextTask : taskData.getNextTasksNames()) {
                System.out.println(i++ + ". " + nextTask.getSecond() + " --- Belonging to project: " + nextTask.getFirst());
            }
        } else {
            System.out.println("- There are no next tasks");
        }


        System.out.println("Previous tasks:");
        if (taskData.getPrevTaskNames().size() > 0) {
            int i = 1;
            for (String prevTaskName : taskData.getPrevTaskNames()) {
                System.out.println(i++ + ". " + prevTaskName);
            }
        } else {
            System.out.println("- There are no previous tasks");
        }
        System.out.println();
    }
}
