package UserInterface;

import Application.IncorrectPermissionException;
import Application.StartTaskController;
import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.TaskProxy;
import Domain.TaskStates.UserAlreadyExecutingTaskException;
import org.junit.Test;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class StartTaskUI {
    private final StartTaskController controller;

    public StartTaskUI(StartTaskController controller) {
        this.controller = controller;
    }

    private StartTaskController getController() {
        return controller;
    }

    public void startTask(){
        if (!getController().startTaskPreconditions()){
            System.out.println("ERROR: You need a developer role to call this function.");
            return;
        }
        if (getController().getUserTaskData() != null && getController().getUserTaskData().getStatus() == Status.EXECUTING){
            System.out.println("ERROR: You are already working on an executing task.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while(true) {

            printTasksList();

            System.out.println("Please give the project name you want to start working in: ");
            String projectName = scanner.nextLine();
            if (projectName.equals("BACK")) {
                System.out.println("Cancelled task creation");
                return;
            }

            System.out.println("Please give the task name you want to start working on in project " + projectName + ": ");
            String taskName = scanner.nextLine();
            if (taskName.equals("BACK")) {
                System.out.println("Cancelled task creation");
                return;
            }

            try {
                TaskProxy taskData = getController().getTaskData(projectName, taskName);

                if (taskData.getStatus() != Status.AVAILABLE && taskData.getStatus() != Status.PENDING){
                    System.out.println("ERROR: The given task is not available or pending");
                    break;
                }

                System.out.print("You have roles: ");
                Set<Role> userRoles = getController().getUserRoles();
                if (userRoles.size() > 0) {
                    System.out.println(
                            userRoles.stream().
                                    map(Object::toString).
                                    collect(Collectors.joining(", ")));
                } else {
                    System.out.println("You currently don't have any roles");
                    return;
                }
                System.out.print("Task requires roles: ");
                List<Role> taskRoles = taskData.getRequiredRoles();
                if (taskRoles.size() > 0) {
                    System.out.println(
                            taskRoles.stream().
                                    map(Object::toString).
                                    collect(Collectors.joining(", ")));
                } else {
                    System.out.println("The task currently doesn't require any roles");
                    return;
                }

                Role role = null;
                while(role == null){
                    System.out.println("Give the role you want to fulfill in task " + taskName + ":");
                    String roleString = scanner.nextLine();
                    if (roleString.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }

                    switch (roleString) {
                        case ("sysadmin") -> role = Role.SYSADMIN;
                        case ("java programmer") -> role = Role.JAVAPROGRAMMER;
                        case ("python programmer") -> role = Role.PYTHONPROGRAMMER;
                        default -> System.out.println("Unrecognized developer role");
                    }
                    if (role != null && !taskData.getRequiredRoles().contains(role)){
                        System.out.println("ERROR: The given role is not required in this task.");
                        role = null;
                    }
                }

                //CONFIRM
                showTask(taskData);
                System.out.println("Start working on this task as a " + role +
                        " at the current system time: " +
                        getController().getTaskManSystemData().getSystemTime().toString());

                System.out.println("Confirm? (y/n)");
                String answer = scanner.nextLine();
                if (answer.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }

                while (!answer.equals("y") && !answer.equals("n")) {
                    System.out.println("Confirm that you want to start working on this task at the current system time: "
                            + getController().getTaskManSystemData().getSystemTime().toString());
                    System.out.println("Confirm? (y/n)");
                    answer = scanner.nextLine();
                    if (answer.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                }
                System.out.println();

                if (answer.equals("n")) {
                    System.out.println("Cancelled starting task " + taskName);
                    return;

                }

                // PENDING TASK
                if (getController().getUserTaskData() != null && getController().getUserTaskData().getStatus() == Status.PENDING) {
                    String pendingTaskName = getController().getUserTaskData().getName();

                    System.out.println("You are already pending for task " + pendingTaskName);
                    System.out.println("Confirm you want to stop pending for task " + pendingTaskName + " and start working on task " + taskName + "? (y/n)");
                    answer = scanner.nextLine();
                    if (answer.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }

                    while (!answer.equals("y") && !answer.equals("n")) {
                        System.out.println("Confirm you want to stop pending for task " + pendingTaskName + " and start working on task " + taskName + "? (y/n)");
                        answer = scanner.nextLine();
                        if (answer.equals("BACK")) {
                            System.out.println("Cancelled task creation");
                            return;
                        }
                    }
                    System.out.println();

                    if (answer.equals("n")) {
                        System.out.println("Cancelled starting task " + taskName);
                        return;

                    }
                }

                getController().startTask(projectName, taskName, role);
                System.out.println("Successfully started working on task " + taskName + " in project " + projectName +
                        " as " + role);


                System.out.println();
                return;

            } catch (ProjectNotFoundException e) {
                System.out.println("ERROR: Given project could not be found");
            } catch (TaskNotFoundException e) {
                System.out.println("ERROR: Given task could not be found");
            } catch (IncorrectPermissionException | IncorrectRoleException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (IncorrectTaskStatusException e) {
                System.out.println("ERROR: Given state has not the right status to start");
            } catch (UserAlreadyExecutingTaskException e) {
                System.out.println("ERROR: User is already executing a task");
            }
        }
    }


    private void printTasksList(){
        System.out.println("***** LIST OF AVAILABLE OR PENDING TASKS *****");
        try {
            for (String projectName : getController().getTaskManSystemData().getProjectNames()) {

                ProjectProxy projectData = getController().getProjectData(projectName);

                if (projectData.getStatus() == ProjectStatus.ONGOING) {
                    for (String taskName : projectData.getActiveTasksNames()) {

                        TaskProxy taskData = getController().getTaskData(projectName, taskName);

                        if (taskData.getStatus() == Status.AVAILABLE || taskData.getStatus() == Status.PENDING) {

                            Set<Role> userRoles = getController().getUserRoles();
                            userRoles.retainAll(taskData.getRequiredRoles());

                            if (userRoles.size() > 0) {
                                System.out.println(" - Task: " + taskName + ", belonging to Project: " + projectName);
                            }
                        }
                    }
                }
            }
            System.out.println();
        } catch (ProjectNotFoundException | TaskNotFoundException e) {
            throw new RuntimeException(e); // TODO het zijn wel echt errors die nooit mogen!
        } catch (IncorrectPermissionException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void showTask(TaskProxy taskData) {
        System.out.println("******** TASK DETAILS ********");

        System.out.println("Task Name:            " + taskData.getName());
        System.out.println("Belonging to project: " + taskData.getProjectName());
        System.out.println("Description:          " + taskData.getDescription());
        System.out.println("Estimated Duration:   " + taskData.getEstimatedDuration().toString());
        System.out.println("Status:               " + taskData.getStatus().toString() + "\n");

        System.out.print("Replaces Task:      ");
        if (taskData.getReplacesTaskName() == null){
            System.out.println("Replaces no tasks\n");
        } else {
            System.out.println(taskData.getReplacesTaskName() + '\n');
        }

        System.out.println("Required roles:");
        if (taskData.getRequiredRoles().size() > 0){
            for (Role role : taskData.getRequiredRoles()){
                System.out.println("- " + role.toString());
            }
        } else {
            System.out.println("All roles are filled in.");
        }
        System.out.println();

        System.out.println("Committed users:");
        if (taskData.getUserNamesWithRole().size() > 0){
            taskData.getUserNamesWithRole().forEach((userName, role) -> System.out.println("- " + userName + " as " + role.toString()));
        } else {
            System.out.println("No users are committed to this task.");
        }
        System.out.println();
    }
}
