import java.util.List;
import java.util.Scanner;

public class ShowProjectsUI {

  private final ShowProjectsController controller;

  private ShowProjectsController getController() {
    return controller;
  }


  public ShowProjectsUI(Session session, TaskManSystem taskManSystem) {
    this.controller =
      new ShowProjectsController(session, taskManSystem);
  }

  public void showProjects() {
    if (controller.createProjectPreconditions()){
      showProjectsForm();
    } else {
      printPermissionError();
    }
  }


  public void showProjectsForm() {
    Scanner scanner = new Scanner(System.in);
    try {
      while (true) {
        System.out.println("Type \"BACK\" to cancel");
        System.out.println("********* PROJECTS *********");
        printProjects(getController().getProjectNames(), getController().getStatuses());

        System.out.println("Type the name of a project to see more details:");
        String response = scanner.nextLine();
        if (response.equals("BACK")) {
          return;
        }
        try {
          chooseProject(response);
        }
        catch (ProjectNotFoundException e) {
          printProjectNotFoundError();
        }
      }
    }
    catch (IncorrectPermissionException e) {
      printPermissionError();
    }
  }

  private void chooseProject(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
    Scanner scanner = new Scanner(System.in);

    printProjectDetails(getController().showProject(projectName));

    System.out.println("Type the name of a task to see more details, or type \"BACK\" to choose another project:");
    String response = scanner.nextLine();

    if (response.equals("BACK")) {
      return;
    }

    while (true) {
      try {
        showTask(getController().showTask(projectName, response));
        System.out.println("Type the name of another task to see more details, or type \"BACK\" to choose another project:");
        response = scanner.nextLine();
        if (response.equals("BACK")) {
          return;
        }
      }
      catch (TaskNotFoundException e) {
        printTaskNotFoundError();
      }
    }
  }

  private void printTaskNotFoundError() {
    System.out.println("Task not found");
  }

  private void printProjectNotFoundError() {
    System.out.println("Project not found");
  }

  private void printPermissionError() {
    System.out.println(
            "You must be logged in with the " +
                    Role.PROJECTMANAGER +
                    " role to call this function"
    );
  }


  public void printProjects(List<String> names, List<String> statuses) {
    int i = 0;
    for (String name : names) {
      System.out.print(i + 1);
      System.out.print(".");
      System.out.println(name + ", status: " + statuses.get(i));
      i++;
    }
  }

  public void printProjectDetails(String projectString) {
    System.out.println("******** PROJECT DETAILS ********");
    System.out.println(projectString);
  }

  private void showTask(String taskString) {
    System.out.println("******** TASK DETAILS ********");
    System.out.println(taskString);
  }
}
