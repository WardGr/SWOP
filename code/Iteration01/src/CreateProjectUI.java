import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateProjectUI {
    private CreateProjectController createProjectController;


    public CreateProjectUI(Session session, TaskManSystem taskManSystem) {
        this.createProjectController = new CreateProjectController(session, this, taskManSystem);
    }

    public void createProject() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel project creation at any time");
        System.out.println("*********** PROJECT CREATION FORM ***********");

        System.out.print("Project Name: ");
        String projectName = scanner.nextLine();
        if (projectName.equals("BACK")) {
            return;
        }

        System.out.print("Project Description: ");
        String projectDescription = scanner.nextLine();
        if (projectDescription.equals("BACK")) {
            return; //TODO: code duplication, is this an issue? It's pretty readable.
        }

        System.out.print("Project due time: ");
        String dueTime = scanner.nextLine();
        if (dueTime.equals("BACK")) {
            return;
        }

        createProjectController.createProject(projectName, projectDescription, dueTime);
        System.out.println("Project with name " + projectName + " created!");
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }

    public void dueTimeFormatError() {
        System.out.println("The given due time is not a valid time, please try again");
        createProject();
    }

    public void dueBeforeSystemTimeError() {
        System.out.println("The given due time is before the current system time, please try again");
        createProject();
    }

    public void createTask() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type BACK to cancel task creation at any time");

        System.out.println("*********** TASK CREATION FORM ***********");
        System.out.println("Project name of which to add the task to:");
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

        System.out.println("Task duration:");
        String duration = scanner.nextLine();
        if (duration.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        System.out.println("Task deviation:");
        String deviation = scanner.nextLine();
        if (deviation.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
        }

        Time durationTime;
        float deviationFloat;
        try{
            durationTime = new Time(Integer.parseInt(duration));
            deviationFloat = Float.parseFloat(deviation);
        } catch (Exception e){
            System.out.println("Invalid input");
            return;
        }

        System.out.println("Is this a replacement task? (y/n)");
        String answer = scanner.nextLine();

        while(!answer.equals("y") && !answer.equals("n")) {
            System.out.println("Is this a replacement task? (y/n)");
            answer = scanner.nextLine();
        }

        if (answer.equals("y")) {
            System.out.println("This task is a replacement for task:");
            String replaces = scanner.nextLine();
            createProjectController.replaceTask(projectName, taskName, description, durationTime, deviationFloat, replaces);
        }
        else {
            System.out.println("Tasks that should be completed before this task:");
            System.out.println("Enter '.' to stop adding new tasks");
            String previousTask = scanner.nextLine();
            List<String> previousTasks = new ArrayList<String>();
            while (!previousTask.equals(".")) {
                previousTasks.add(previousTask);
                previousTask = scanner.nextLine();
            }
            createProjectController.createTask(projectName, taskName, description, durationTime, deviationFloat, previousTasks);
        }
    }
}
