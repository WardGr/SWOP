import java.util.List;
import java.util.Map;
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
    try {
      getController().showProjectsPreconditions();
      showProjectsForm();
    } catch (IncorrectPermissionException e){
      System.out.println(e.getMessage());
    }
  }


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
        chooseProject(response);
      }
      catch (ProjectNotFoundException e) {
        System.out.println("The given project could not be found.");
      }
    }
  }

  private void chooseProject(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
    Scanner scanner = new Scanner(System.in);

    showProject(getController().showProject(projectName));

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
        System.out.println("The given task could not be found.");
      }
    }
  }

  public void showProjectsWithStatuses() throws IncorrectPermissionException {
    List<Map.Entry<String, String>> statuses = getController().getProjectNamesWithStatus();
    int i = 0;
    for (Map.Entry<String, String> entry : statuses) {
      System.out.print(i + 1);
      System.out.print(".");
      System.out.println(entry.getKey() + ", status: " + entry.getValue());
      i++;
    }
  }

  public void showProject(String projectString) {
    System.out.println("******** PROJECT DETAILS ********");
    System.out.println(projectString);
  }

  private void showTask(String taskString) {
    System.out.println("******** TASK DETAILS ********");
    System.out.println(taskString);
  }
}
