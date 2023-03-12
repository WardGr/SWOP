import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateProjectUI {
    private CreateProjectController createProjectController;


    public CreateProjectUI(Session session, TaskManSystem taskManSystem) {
        this.createProjectController = new CreateProjectController(session, this, taskManSystem);
    }

    public void createProject(){
        createProjectController.createProjectForm();
    }

    public void createProjectForm() {
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
            return;
        }

        System.out.print("Project due hour: ");
        String dueHourString = scanner.nextLine();

        int dueHour;
        while (true) {
            try {
                if (dueHourString.equals("BACK")) {
                    return;
                }
                dueHour = Integer.parseInt(dueHourString);
                break;
            }
            catch (NumberFormatException e) {
                System.out.println("Given due hour is not an integer, please input an integer and try again");
                dueHourString = scanner.nextLine();
            }
        }

        System.out.print("Project due minute: ");
        String dueMinuteString = scanner.nextLine();

        int dueMinute;
        while (true) {
            try {
                if (dueMinuteString.equals("BACK")) {
                    return;
                }
                dueMinute = Integer.parseInt(dueMinuteString);
                break;
            }
            catch (NumberFormatException e) {
                System.out.println("Given due minute is not an integer, please input an integer and try again");
                dueMinuteString = scanner.nextLine();
            }
        }

        createProjectController.createProject(projectName, projectDescription, dueHour, dueMinute);
    }

    public void messageProjectCreation(String projectName){
        System.out.println("Project with name " + projectName + " created!");
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }

    public void notValidTimeError() {
        System.out.println("The given time is not a valid time, please try again");
        createProject();
    }

    public void dueBeforeSystemTimeError() {
        System.out.println("The given due time is before the current system time, please try again");
        createProject();
    }

    public void projectAlreadyInUseError(){
        System.out.println("The project name is already in use");
        createProject();
    }
}
