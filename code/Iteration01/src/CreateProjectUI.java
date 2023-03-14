import java.util.Scanner;

public class CreateProjectUI {

  private final CreateProjectController controller;

  public CreateProjectUI(Session session, TaskManSystem taskManSystem) {
    this.controller = new CreateProjectController(session, taskManSystem);
  }

  public void createProject() {
    if (controller.createProjectPreconditions()){
      createProjectForm();
    } else {
      permissionError();
    }
  }

  public void createProjectForm() {
    Scanner scanner = new Scanner(System.in);

    while(true) {

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
        } catch (NumberFormatException e) {
          System.out.println(
                  "Given due hour is not an integer, please input an integer and try again"
          );
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
        } catch (NumberFormatException e) {
          System.out.println(
                  "Given due minute is not an integer, please input an integer and try again"
          );
          dueMinuteString = scanner.nextLine();
        }
      }

      try {
        controller.createProject(projectName, projectDescription, dueHour, dueMinute);
        messageProjectCreation(projectName);
        return;
      } catch (ProjectNameAlreadyInUseException e) {
        projectAlreadyInUseError();
      } catch (InvalidTimeException e) {
        notValidTimeError();
      } catch (DueBeforeSystemTimeException e) {
        dueBeforeSystemTimeError();
      } catch (IncorrectPermissionException e) {
        permissionError();
      }
    }
  }

  private void messageProjectCreation(String projectName) {
    System.out.println("Project with name " + projectName + " created!");
  }

  private void permissionError() {
    System.out.println(
      "You must be logged in with the " +
      Role.PROJECTMANAGER +
      " role to call this function"
    );
  }

  public void notValidTimeError() {
    System.out.println("The given time is not a valid time, please try again");
  }

  private void dueBeforeSystemTimeError() {
    System.out.println(
      "The given due time is before the current system time, please try again"
    );
  }

  private void projectAlreadyInUseError() {
    System.out.println("The project name is already in use");
  }
}
