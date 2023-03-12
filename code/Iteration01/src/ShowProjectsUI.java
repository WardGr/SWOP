import java.util.List;
import java.util.Scanner;

public class ShowProjectsUI {
    private final ShowProjectsController showProjectsController;


    public ShowProjectsUI(Session session, TaskManSystem taskManSystem) {
        this.showProjectsController = new ShowProjectsController(session, this, taskManSystem);
    }

    public void showProjects() {
        showProjectsController.showProjects();
    }

    public void printProjects(List<String> names, List<String> statuses) {
        System.out.println("********* PROJECTS *********");
        int i = 0;
        for (String name : names) {
            System.out.print(i+1);
            System.out.print(".");
            System.out.println(name + ", status: " + statuses.get(i));
            i++;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Type a projects name to see its details, including a list of its tasks, type BACK to exit:");
        String projectName = scanner.nextLine();
        if (projectName.equals("BACK")) {
            return;
        }
        showProjectsController.showProject(projectName);
    }

    public void printProjectDetails(String projectString, String projectName) {
        System.out.println("******** PROJECT DETAILS ********");
        System.out.println(projectString);

        showTask(projectName);
    }

    private void showTask(String projectString) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Type a tasks name to see its details or type BACK to choose another project:");
        String taskString = scanner.nextLine();
        if (taskString.equals("BACK")) {
            showProjects();
        }
        else {
            showProjectsController.showTask(projectString, taskString);
        }
    }

    public void printTaskDetails(String taskString, String projectString) {
        System.out.println("******** TASK DETAILS ********");
        System.out.println(taskString);

        showTask(projectString);
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }

    public void projectNotFoundError() {
        System.out.println("Project not found\n");
        showProjects();
    }

    public void taskNotFoundError(String projectName) {
        System.out.println("Task not found\n");
        showTask(projectName);
    }


}
