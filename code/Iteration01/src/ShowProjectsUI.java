import java.util.List;
import java.util.Scanner;

public class ShowProjectsUI {
    private final ShowProjectsController showProjectsController;


    public ShowProjectsUI(Session session, TaskManSystem taskManSystem) {
        this.showProjectsController = new ShowProjectsController(session, this, taskManSystem);
    }

    public static void printProjects(List<String> names, List<String> statuses) {
        int i = 0;
        for (String name : names) {
            System.out.print(i+1);
            System.out.print(".");
            System.out.println(name + ", status: " + statuses.get(i));
            i++;
        }
    }

    public static void printProjectDetails(String projectString) {
        if (projectString == null) {
            System.out.println("Project not found");
        }
        System.out.println("******** PROJECT DETAILS ********");
        System.out.println(projectString);
    }

    public static void printTaskDetails(String taskString) {
        System.out.println("******** TASK DETAILS ********");
        System.out.println(taskString);
    }

    public void showProjects() {
        System.out.println("********* PROJECTS *********");
        showProjectsController.showProjects();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Type a projects name to see its details, including a list of its tasks, type BACK to exit:");
        String projectString = scanner.nextLine();

        if (projectString.equals("BACK")) {
            return;
        } // todo: ik kan nimeer nadenken, dit is heel cursed wanneer ge een verkeerde naam ingeeft, maar het werkt anders wel
        showProjectsController.showProject(projectString);
        System.out.println("Type a tasks name to see its details or type BACK to choose another project:");
        String taskString = scanner.nextLine();
        while(!taskString.equals("BACK")) {
            showProjectsController.showTask(projectString, taskString);
            System.out.println("Type another tasks name to see its details or type BACK to choose another project:");
            taskString = scanner.nextLine();
        }
        showProjects();
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }
}
