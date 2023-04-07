package UserInterface;

import Application.CreateTaskController;
import Application.IncorrectPermissionException;
import Application.Session;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;

import java.util.*;

// TODO: de back met exception? dan kunnen we wel vanuit elke functie back doen
// TODO: support voor het toevoegen van next/prev tasks en alternative tasks bij het creëren?

/**
 * Handles user input for the createtask use-case, requests necessary domain-level information from the Application.CreateTaskController
 */
public class CreateTaskUI {

    private final CreateTaskController controller;

    public CreateTaskUI(CreateTaskController controller) {
        this.controller = controller;
    }

    private CreateTaskController getController() {
        return controller;
    }

    /**
     * Initial task creation request, checks the user's role before giving the prompt
     */
    public void createTask() {
        if (getController().createTaskPreconditions()) {
            try {
                createTaskForm();
            } catch (IncorrectPermissionException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
    }

    /**
     * Shows the task creation prompt, creates the task if the given information is correct
     *
     * @throws IncorrectPermissionException if the user is not logged in as a project manager
     */
    private void createTaskForm() throws IncorrectPermissionException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Type BACK to cancel task creation at any time");

            System.out.println("*********** TASK CREATION FORM ***********");
            printOngoingProjects();
            System.out.println("Project name of an ongoing project to add the task to:");
            String projectName = scanner.nextLine();
            if (projectName.equals("BACK")) {
                System.out.println("Cancelled task creation");
                return;
            }

            System.out.println("Task name:");
            String taskName = scanner.nextLine();
            if (taskName.equals("BACK")) {
                System.out.println("Cancelled task creation");
                return;
            }

            System.out.println("Task description:");
            String description = scanner.nextLine();
            if (description.equals("BACK")) {
                System.out.println("Cancelled task creation");
                return;
            }

            // TODO de estimated duration halen uit de duration van een andere task?

            System.out.println("Task duration hours:");
            String durationHourString = scanner.nextLine();

            int durationHour;
            while (true) {
                try {
                    if (durationHourString.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                    durationHour = Integer.parseInt(durationHourString);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Given task duration is not an integer, please input an integer and try again"
                    );
                    durationHourString = scanner.nextLine();
                }
            }

            System.out.println("Task duration minutes:");
            String durationMinutesString = scanner.nextLine();

            int durationMinutes;
            while (true) {
                try {
                    if (durationMinutesString.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                    durationMinutes = Integer.parseInt(durationMinutesString);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Given task duration is not an integer, please input an integer and try again"
                    );
                    durationMinutesString = scanner.nextLine();
                }
            }

            System.out.println("Task deviation:");
            String deviationString = scanner.nextLine();

            double deviation;
            while (true) {
                try {
                    if (deviationString.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                    deviation = Double.parseDouble(deviationString);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println(
                            "Given task deviation is not a double, please input an integer and try again"
                    );
                    deviationString = scanner.nextLine();
                }
            }

            System.out.println("Is this a replacement task? (y/n)");
            String answer = scanner.nextLine();
            if (answer.equals("BACK")) {
                System.out.println("Cancelled task creation");
                return;
            }

            while (!answer.equals("y") && !answer.equals("n")) {
                System.out.println("Is this a replacement task? (y/n)");
                answer = scanner.nextLine();
                if (answer.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }
            }

            if (answer.equals("y")) {
                try {
                    printActiveFailedTasks(projectName);

                    System.out.println("This task is a replacement for task:");
                    String replaces = scanner.nextLine();
                    if (replaces.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }

                    getController().replaceTask(
                            projectName,
                            taskName,
                            description,
                            new Time(durationHour, durationMinutes),
                            deviation,
                            replaces
                    );
                    System.out.println("Task " + taskName + " successfully added to project " + projectName + " as a replacement for task " + replaces);
                    return;
                } catch (ReplacedTaskNotFailedException e) {
                    System.out.println("ERROR: the task to replace has not failed, please try again\n");
                } catch (ProjectNotFoundException e) {
                    System.out.println("ERROR: the given project does not exist\n");
                } catch (InvalidTimeException e) {
                    System.out.println("ERROR: The given minutes are not of a valid format (0-59)\n");
                } catch (TaskNotFoundException e) {
                    System.out.println("ERROR: the given task to replace does not exist\n");
                } catch (TaskNameAlreadyInUseException e) {
                    System.out.println("ERROR: the given task name is already in use\n");
                } catch (IncorrectTaskStatusException e) {
                    System.out.println("ERROR: " + e.getMessage() + ", please try again\n");
                }
            } else {
                List<Role> roles = new LinkedList<>();

                System.out.println("Give developer roles needed for this task, end with a '.'");
                System.out.println("You can choose from: sysadmin, java programmer, python programmer");
                String role = scanner.nextLine();
                if (role.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }
                while(!role.equals(".")){
                    switch (role) {
                        case ("sysadmin") -> roles.add(Role.SYSADMIN);
                        case ("java programmer") -> roles.add(Role.JAVAPROGRAMMER);
                        case ("python programmer") -> roles.add(Role.PYTHONPROGRAMMER);
                        default -> System.out.println("(Unrecognized developer role)");
                    }
                    role = scanner.nextLine();
                    if (role.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                }

                System.out.println("Tasks that this task depends on, enter '.' to stop adding new tasks:");
                String previousTask = scanner.nextLine();
                if (previousTask.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }

                Set<String> previousTasks = new HashSet<>();
                while (!previousTask.equals(".")) {
                    previousTasks.add(previousTask);
                    previousTask = scanner.nextLine();
                    if (previousTask.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                }

                System.out.println("Tasks that depend on this task, enter '.' to stop adding new tasks:");
                String nextTask = scanner.nextLine();
                if (nextTask.equals("BACK")) {
                    System.out.println("Cancelled task creation");
                    return;
                }

                Set<String> nextTasks = new HashSet<>();
                while (!nextTask.equals(".")) {
                    nextTasks.add(nextTask);
                    nextTask = scanner.nextLine();
                    if (nextTask.equals("BACK")) {
                        System.out.println("Cancelled task creation");
                        return;
                    }
                }


                try {
                    getController().createTask(
                            projectName,
                            taskName,
                            description,
                            new Time(durationHour, durationMinutes),
                            deviation,
                            roles,
                            previousTasks,
                            nextTasks
                    );
                    System.out.println("Task " + taskName + " successfully added to project " + projectName);
                    return;
                } catch (UserNotFoundException e) {
                    // TODO
                    System.out.println("ERROR: Given user does not exist or is not a developer\n");
                } catch (ProjectNotFoundException e) {
                    System.out.println("ERROR: Given project does not exist\n");
                } catch (InvalidTimeException e) {
                    System.out.println("ERROR: The given minutes are not of a valid format (0-59)\n");
                } catch (TaskNameAlreadyInUseException e) {
                    System.out.println("ERROR: The given task name is already in use\n");
                } catch (TaskNotFoundException e) {
                    System.out.println("ERROR: A given previous or next task name can't be found\n");
                } catch (IncorrectTaskStatusException e) {
                    System.out.println("ERROR: " + e.getMessage() + '\n');
                } catch (LoopDependencyGraphException e) {
                    System.out.println("ERROR: Given list of tasks introduces a loop\n");
                } catch (NonDeveloperRoleException e) {
                    System.out.println("ERROR: One of the given roles is not a developer role");
                }
            }
        }
    }

    private void printOngoingProjects(){
        System.out.println("-- Ongoing Projects --");
        List<String> ongoingProjectsNames = getController().getTaskManSystemData().getProjectNames();
        ongoingProjectsNames.removeIf( e -> {
                try {
                    return getController().getProjectData(e).getStatus() == ProjectStatus.FINISHED;
                } catch (ProjectNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });
        if (ongoingProjectsNames.size() > 0) {
            for (String projectName : ongoingProjectsNames) {
                System.out.println(" - " + projectName);
            }
        } else {
            System.out.println(" - There is no ongoing project in the system.");
        }
    }

    private void printActiveFailedTasks(String projectName) throws ProjectNotFoundException, TaskNotFoundException {
        System.out.println("-- Tasks that can be replaced --");

        for (String taskName : getController().getProjectData(projectName).getActiveTasksNames()){
            if (getController().getTaskData(projectName, taskName).getStatus() == Status.FAILED){
                System.out.println(" - " + taskName);
            }
        }
    }
}
