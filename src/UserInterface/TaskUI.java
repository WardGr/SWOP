package UserInterface;

import Application.NotConfirmedActionException;
import Application.TaskController;
import Application.IncorrectPermissionException;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.TaskData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles user input for the createtask use-case, requests necessary domain-level information from the Application.CreateTaskController
 */
public class TaskUI {

    private final TaskController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public TaskUI(TaskController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private TaskController getController() {
        return controller;
    }

    /**
     * Initial task creation request, checks the user's role before giving the prompt
     */
    public void createTask() {
        if (getController().taskPreconditions()) {
            try {
                createTaskForm();
            } catch (IncorrectPermissionException e) {
                System.out.println('\n' + e.getMessage() + '\n');
            } catch (BackException e){
                System.out.println("\nCancelled Task Creation\n");
            }
        } else {
            System.out.println("\nYou must be logged in with the " + Role.PROJECTMANAGER + " role to call this function\n");
        }
    }

    /**
     * Shows the task creation prompt, creates the task if the given information is correct
     *
     * @throws IncorrectPermissionException if the user is not logged in as a project manager
     */
    private void createTaskForm() throws IncorrectPermissionException, BackException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Type BACK to cancel task creation at any time");

            System.out.println("*********** TASK CREATION FORM ***********");
            printOngoingProjects();
            System.out.println("Project name of an ongoing project to add the task to:");
            String projectName = scanner.nextLine();
            if (projectName.equals("BACK")) {
                throw new BackException();
            }

            System.out.println("Task name:");
            String taskName = scanner.nextLine();
            if (taskName.equals("BACK")) {
                throw new BackException();
            }

            System.out.println("Task description:");
            String description = scanner.nextLine();
            if (description.equals("BACK")) {
                throw new BackException();
            }

            System.out.println("Task duration hours:");
            int durationHour = getIntegerInput(scanner, "Given task duration is not an integer, please input an integer and try again");

            System.out.println("Task duration minutes:");
            int durationMinutes = getIntegerInput(scanner, "Given task duration is not an integer, please input an integer and try again");

            System.out.println("Task deviation:");
            double deviation = getDoubleInput(scanner, "Given task deviation is not a double, please input an integer and try again");


            boolean replacement = getBooleanInput(scanner, "Is this a replacement task?");

            if (replacement) {
                try {
                    printReplacableTasks(projectName);

                    System.out.println("This task is a replacement for task:");
                    String replaces = scanner.nextLine();
                    if (replaces.equals("BACK")) {
                        throw new BackException();
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

                List<Role> roles = getRolesInput(scanner);

                Set<Tuple<String,String>> prevTasks = getTaskSet(scanner, "Give projectName and taskName of tasks that this task depends on");
                Set<Tuple<String,String>> nextTasks = getTaskSet(scanner, "Give projectName and taskName of tasks that depend on this task");
                System.out.println();

                try {
                    getController().createTask(
                            projectName,
                            taskName,
                            description,
                            new Time(durationHour, durationMinutes),
                            deviation,
                            roles,
                            prevTasks,
                            nextTasks
                    );
                    System.out.println("Task " + taskName + " successfully added to project " + projectName);
                    return;
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
                } catch (IllegalTaskRolesException e) {
                    System.out.println("ERROR: One of the given roles is not a developer role (Python programmer, Java programmer or system administrator)\n");
                } catch (ProjectNotOngoingException e) {
                    System.out.println("ERROR: Project is already finished\n");
                }
            }
        }
    }

    /**
     * Prints all projects with the ONGOING status to the user
     */
    private void printOngoingProjects() {
        System.out.println("-- Ongoing Projects --");
        List<String> ongoingProjectsNames = getController().getTaskManSystemData().getProjectNames();
        ongoingProjectsNames.removeIf(e -> {
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

    /**
     * Prints all tasks that can be replaced
     *
     * @param projectName               Project name of which to print all replacable tasks
     * @throws ProjectNotFoundException if the given projectName does not correspond to an existing project within the system
     */
    private void printReplacableTasks(String projectName) throws ProjectNotFoundException, TaskNotFoundException {
        System.out.println("-- Tasks that can be replaced --");

        for (String taskName : getController().getProjectData(projectName).getActiveTasksNames()) {
            if (getController().getTaskData(projectName, taskName).getStatus() == Status.FAILED) {
                System.out.println(" - " + taskName);
            }
        }
    }


    private int getIntegerInput(Scanner scanner, String tryAgainMessage) throws BackException{
        String string = scanner.nextLine();

        int integer;
        while (true) {
            if (string.equals("BACK")) {
                throw new BackException();
            }
            try {
                integer = Integer.parseInt(string);
                break;
            } catch (NumberFormatException e) {
                System.out.println(tryAgainMessage);
                string = scanner.nextLine();
            }
        }
        return integer;
    }

    private double getDoubleInput(Scanner scanner, String tryAgainMessage) throws BackException{
        String string = scanner.nextLine();

        double doubleValue;
        while (true) {
            if (string.equals("BACK")) {
                throw new BackException();
            }
            try {
                doubleValue = Double.parseDouble(string);
                break;
            } catch (NumberFormatException e) {
                System.out.println(tryAgainMessage);
                string = scanner.nextLine();
            }
        }
        return doubleValue;
    }

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

    private List<Role> getRolesInput(Scanner scanner) throws BackException {
        List<Role> roles = new LinkedList<>();

        System.out.println("Give developer roles needed for this task, end with a '.'");
        System.out.println("You can choose from: sysadmin, java programmer, python programmer");
        String role = scanner.nextLine();
        if (role.equals("BACK")) {
            throw new BackException();
        }
        while (!role.equals(".")) {
            switch (role) {
                case ("sysadmin") -> roles.add(Role.SYSADMIN);
                case ("java programmer") -> roles.add(Role.JAVAPROGRAMMER);
                case ("python programmer") -> roles.add(Role.PYTHONPROGRAMMER);
                default -> System.out.println("(Unrecognized developer role)");
            }
            role = scanner.nextLine();
            if (role.equals("BACK")) {
                throw new BackException();
            }
        }
        return roles;
    }

    private Set<Tuple<String,String>> getTaskSet(Scanner scanner, String message) throws BackException {
        Set<Tuple<String,String>> tasks = new HashSet<>();

        System.out.println(message);
        System.out.println("Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:");
        String input = scanner.nextLine();
        while (!input.equals(".")){
            if (input.equals("BACK")) {
                throw new BackException();
            }

            String[] inputSplit = input.split(" / ");
            if (inputSplit.length == 2){
                String projectName = inputSplit[0];
                String taskName = inputSplit[1];
                tasks.add(new Tuple<>(projectName, taskName));

                System.out.println("Give projectName and taskName of tasks that this task depends on");
                System.out.println("Follow the form: <projectName / taskName>, and enter '.' to stop adding new tasks:");
                input = scanner.nextLine();
            }
        }

        return tasks;
    }


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
                return;

            } catch (ProjectNotFoundException e) {
                System.out.println("Given project name could not be found, try again\n");
            } catch (TaskNotFoundException e) {
                System.out.println("Given task name could not be found, try again\n");
            } catch (NotConfirmedActionException e) {
                System.out.println(e.getMessage() + '\n');
            }

        }
    }

    private void printProjectsList() throws ProjectNotFoundException {
        System.out.println(" *** PROJECTS ***");
        TaskManSystemData taskManSystemData = getController().getTaskManSystemData();
        for (String projectName : taskManSystemData.getProjectNames()){
            ProjectData projectData = getController().getProjectData(projectName);
            int nbOfTasks = projectData.getActiveTasksNames().size() + projectData.getReplacedTasksNames().size();
            System.out.println(" - " + projectName + " | Containing " + nbOfTasks + " Tasks");
        }
    }

    private void printTasksList(String projectName) throws ProjectNotFoundException, TaskNotFoundException {
        ProjectData projectData = getController().getProjectData(projectName);
        System.out.println(" *** TASKS in " + projectName + " ***");
        if (projectData.getActiveTasksNames().size() + projectData.getReplacedTasksNames().size() == 0) {
            System.out.println("There are no tasks in this project");
        }
        for (String taskName : projectData.getActiveTasksNames()){
            System.out.println(" - " + taskName);
        }
        for (String replacedTaskName : projectData.getReplacedTasksNames()){
            System.out.println(" - " + replacedTaskName + " - Replaced by: " + getController().getTaskData(projectName, replacedTaskName).getReplacementTaskName());
        }
        System.out.println();
    }

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


    private static class BackException extends Exception {
        public BackException() {super();}
    }
}
