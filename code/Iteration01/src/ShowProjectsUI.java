import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles I/O for the showprojects use-case, requests necessary domain-level information from the ShowProjectsController
 */
public class ShowProjectsUI {

  private final ShowProjectsController controller;

  private ShowProjectsController getController() {
    return controller;
  }


  public ShowProjectsUI(Session session, TaskManSystem taskManSystem) {
    this.controller =
      new ShowProjectsController(session, taskManSystem);
  }

  /**
   * Initial showprojects request: shows all projects if user is logged in as a project manager
   */
  public void showProjects() {
    try {
      getController().showProjectsPreconditions();
      showProjectsForm();
    } catch (IncorrectPermissionException e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * Shows all projects, displays detailed information about a given project if the user requests it, and allows the
   * user to request information about said projects tasks. User can exit by typing "BACK" at any prompt.
   */
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

  /**
   * Shows detailed information about a given project, and allows the user to request details about any of its tasks
   * @param projectName name of project the user wants to see more details of
   *
   * @throws ProjectNotFoundException if projectName does not correspond to an existing projects' name
   * @throws IncorrectPermissionException if user is not a project manager
   */
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

  /**
   * Pretty-prints projects with their given statuses
   *
   * @throws IncorrectPermissionException if current session is not held by a project manager
   */
  private void showProjectsWithStatuses() throws IncorrectPermissionException {
    Map<String, String> statuses = getController().getProjectNamesWithStatus();
    statuses.forEach((project, status) -> System.out.println(project + ", status: " + status));
  }

  /**
   * Pretty-prints given project string
   * @param projectString String containing project details
   */
  private void showProject(String projectString) {
    System.out.println("******** PROJECT DETAILS ********");
    System.out.println(projectString);
  }

  /**
   * Pretty-prints given task string
   * @param taskString String containing task details
   */
  private void showTask(String taskString) {
    System.out.println("******** TASK DETAILS ********");
    System.out.println(taskString);
  }
}
