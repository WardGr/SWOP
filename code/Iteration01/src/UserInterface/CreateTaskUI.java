package UserInterface;

import Application.CreateTaskController;
import Application.IncorrectPermissionException;
import Application.Session;
import Domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles user input for the createtask use-case, requests necessary domain-level information from the Application.CreateTaskController
 */
public class CreateTaskUI {

  private final CreateTaskController controller;

  public CreateTaskUI(
    Session session,
    TaskManSystem taskManSystem,
    UserManager userManager
  ) {
    this.controller =
      new CreateTaskController(session, taskManSystem, userManager);
  }

  private CreateTaskController getController() {
    return controller;
  }

  /**
   * Initial task creation request, checks the user's role before giving the prompt
   */
  public void createTask() {
    if (getController().createTaskPreconditions()){
      try{
        createTaskForm();
      }
      catch (IncorrectPermissionException e) {
        System.out.println(e.getMessage());
      }
    }
    else {
      System.out.println("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
    }
  }

  /**
   * Shows the task creation prompt, creates the task if the given information is correct
   * @throws IncorrectPermissionException if the user is not logged in as a project manager
   */
  private void createTaskForm() throws IncorrectPermissionException {
    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.println("Type BACK to cancel task creation at any time");

      System.out.println("*********** TASK CREATION FORM ***********");
      System.out.println("Domain.Project name of which to add the task to:");
      String projectName = scanner.nextLine();
      if (projectName.equals("BACK")) {
        System.out.println("Cancelled task creation");
        return;
      }

      System.out.println("Domain.Task name:");
      String taskName = scanner.nextLine();
      if (taskName.equals("BACK")) {
        System.out.println("Cancelled task creation");
        return;
      }

      System.out.println("Domain.Task description:");
      String description = scanner.nextLine();
      if (description.equals("BACK")) {
        System.out.println("Cancelled task creation");
        return;
      }

      // TODO de estimated duration halen uit de duration van een andere task?

      System.out.println("Domain.Task duration hours:");
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

      System.out.println("Domain.Task duration minutes:");
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

      System.out.println("Domain.Task deviation:");
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
        System.out.println("This task is a replacement for task:");
        String replaces = scanner.nextLine();
        if (replaces.equals("BACK")) {
          System.out.println("Cancelled task creation");
          return;
        }
        try {
          getController().replaceTask(
                  projectName,
                  taskName,
                  description,
                  durationHour,
                  durationMinutes,
                  deviation,
                  replaces
          );
          System.out.println("Domain.Task " + taskName + " successfully added to project " + projectName + " as a replacement for task " + replaces);
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
        }
      } else {
        System.out.println("Give developer performing this task:");
        String developer = scanner.nextLine();
        if (developer.equals("BACK")) {
          System.out.println("Cancelled task creation");
          return;
        }

        System.out.println("Tasks that should be completed before this task, enter '.' to stop adding new tasks:");
        String previousTask = scanner.nextLine();
        if (previousTask.equals("BACK")) {
          System.out.println("Cancelled task creation");
          return;
        }

        List<String> previousTasks = new ArrayList<>();
        while (!previousTask.equals(".")) {
          previousTasks.add(previousTask);
          previousTask = scanner.nextLine();
          if (previousTask.equals("BACK")) {
            System.out.println("Cancelled task creation");
            return;
          }
        }


        try {
          getController().createTask(
                  projectName,
                  taskName,
                  description,
                  durationHour,
                  durationMinutes,
                  deviation,
                  developer,
                  previousTasks
          );
          System.out.println("Domain.Task " + taskName + " successfully added to project " + projectName);
          return;
        } catch (UserNotFoundException e) {
          System.out.println("ERROR: Given user does not exist or is not a developer\n");
        } catch (ProjectNotFoundException e) {
          System.out.println("ERROR: Given project does not exist\n");
        } catch (InvalidTimeException e) {
          System.out.println("ERROR: The given minutes are not of a valid format (0-59)\n");
        } catch (TaskNotFoundException e) {
          System.out.println("ERROR: Given task does not exist\n");
        } catch (TaskNameAlreadyInUseException e) {
          System.out.println("ERROR: the given task name is already in use\n");
        }
      }
    }
  }
}
