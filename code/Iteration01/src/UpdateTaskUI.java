import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles user input for the updatetask use-case, requests necessary domain-level information from the UpdateTaskController
 */
public class UpdateTaskUI {

  private final UpdateTaskController controller;

  public UpdateTaskUI(Session session, TaskManSystem taskManSystem) {
    controller = new UpdateTaskController(session, taskManSystem);
  }

  private UpdateTaskController getController() {
    return controller;
  }

  /**
   * Handles initial request to updatetask, checks if permission is correct
   */
  public void updateTaskStatus() {
    if (controller.updateTaskPreconditions()){
      try{
        chooseUpdateTask();
      } catch (IncorrectPermissionException e) {
        System.out.println(e.getMessage());
      }
    } else {
      System.out.println("You must be logged in with the " + Role.DEVELOPER + " role to call this function");
    }
  }

  /**
   * Shows all available and executing tasks (these can be updated by the user)
   * Then allows the user to choose a task to update, and updates this task to the given status
   *
   * @throws IncorrectPermissionException if the user is not logged in as a developer
   */
  public void chooseUpdateTask() throws IncorrectPermissionException {
    while(true) {

      Map<String, String> availableTasks = getController().availableTasksNames();
      Map<String, String> executingTasks = getController().executingTasksNames();

      System.out.println("*** AVAILABLE TASKS ***");
      availableTasks.forEach(
              (project, task) -> System.out.println("Task: " + task + ", belonging to project: " + project)
      );
      System.out.println();
      System.out.println("*** EXECUTING TASKS ***");
      executingTasks.forEach(
              (project, task) -> System.out.println("Task: " + task + ", belonging to project: " + project)
      );

      Scanner scanner = new Scanner(System.in);

      System.out.println("Type BACK to cancel updating the task any time");
      System.out.println("Name of the project you want to update:");
      String projectName = scanner.nextLine();
      if (projectName.equals("BACK")) {
        return;
      }
      System.out.println("Name of the task you want to update:");
      String taskName = scanner.nextLine();
      if (taskName.equals("BACK")) {
        return;
      }

      try {
        showTask(projectName, taskName);
        updateForm(projectName, taskName);
        return;
      } catch (ProjectNotFoundException | TaskNotFoundException e) {
        System.out.println("ERROR: the given task could not be found");
      } catch (IncorrectUserException e) {
        System.out.println("ERROR: you are not allowed to change this task");
      }
    }
  }

  /**
   * Shows the task with name taskName, related to the project with name projectName
   *
   * @param projectName Name of the project to which the task is attached
   * @param taskName Name of the task which should be showed
   * @throws ProjectNotFoundException if given projectName does not correspond to an existing project
   * @throws TaskNotFoundException if given taskName does not correspond to an existing task
   * @throws IncorrectPermissionException if user is not logged in as a developer
   */
  public void showTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
    String taskDetails = getController().showTask(projectName, taskName);
    List<Status> nextStatuses = getController().getNextStatuses(projectName, taskName);

    System.out.println(taskDetails);
    System.out.println("-- Possible Next Statuses --");
    for (Status status : nextStatuses) {
      System.out.println("- " + status.toString());
    }
  }

  /**
   * Prompts the user to update the selected task and updates the task status and information
   *
   * @param projectName name of the project given
   * @param taskName name of the task given
   * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
   * @throws TaskNotFoundException if the given task name does not correspond to an existing task
   * @throws IncorrectPermissionException if the user is not logged in as a developer
   * @throws IncorrectUserException if the user is not assigned to this task
   */
  public void updateForm(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException, IncorrectUserException {
    Status status = getController().getStatus(projectName, taskName);
    int systemHour = getController().getSystemHour();
    int systemMinute = getController().getSystemMinute();

    Scanner scanner = new Scanner(System.in);

    while(true) {
      switch (status) {
        case AVAILABLE -> {
          int startHour;
          int startMinute;

          System.out.println("Give the start time for the task.");
          System.out.println(
                  "Do you want to use system time (" +
                          systemHour +
                          ":" +
                          systemMinute +
                          ")? (y/n)"
          );
          String answer = scanner.nextLine();
          if (answer.equals("BACK")) {
            System.out.println("Cancelled updating task");
            return;
          }

          while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println(
                    "Do you want to use system time (" +
                            systemHour +
                            ":" +
                            systemMinute +
                            ")? (y/n)"
            );
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
              System.out.println("Cancelled updating task");
              return;
            }
          }

          if (answer.equals("y")) {
            startHour = systemHour;
            startMinute = systemMinute;
          } else {
            System.out.println("Give start hour: ");
            String startHourString = scanner.nextLine();

            while (true) {
              try {
                if (startHourString.equals("BACK")) {
                  System.out.println("Cancelled updating task");
                  return;
                }
                startHour = Integer.parseInt(startHourString);
                break;
              } catch (NumberFormatException e) {
                System.out.println(
                        "Given start hour is not an integer or '.', please try again"
                );
                startHourString = scanner.nextLine();
              }
            }

            System.out.println("Give start minute: ");
            String startMinuteString = scanner.nextLine();

            while (true) {
              try {
                if (startMinuteString.equals("BACK")) {
                  System.out.println("Cancelled updating task");
                  return;
                }
                startMinute = Integer.parseInt(startMinuteString);
                break;
              } catch (NumberFormatException e) {
                System.out.println(
                        "Given start minute is not an integer or '.', please try again"
                );
                startMinuteString = scanner.nextLine();
              }
            }
          }
          try{
            getController().startTask(projectName, taskName, startHour, startMinute);
            System.out.println("Task " + taskName + " successfully updated");
            return;
          } catch (InvalidTimeException e) {
            System.out.println("ERROR: the given time is not valid");
          } catch (IncorrectTaskStatusException e) {
            System.out.println("ERROR: the task has the wrong status for this update");
          }
        }
        case EXECUTING -> {
          int endHour;
          int endMinute;

          System.out.println("Give the end time for the task.");
          System.out.println(
                  "Do you want to use system time (" +
                          systemHour +
                          ":" +
                          systemMinute +
                          ")? (y/n)"
          );
          String answer = scanner.nextLine();
          if (answer.equals("BACK")) {
            System.out.println("Cancelled updating task");
            return;
          }

          while (!answer.equals("y") && !answer.equals("n")) {
            System.out.println(
                    "Do you want to use system time (" +
                            systemHour +
                            ":" +
                            systemMinute +
                            ")? (y/n)"
            );
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
              System.out.println("Cancelled updating task");
              return;
            }
          }

          if (answer.equals("y")) {
            endHour = systemHour;
            endMinute = systemMinute;
          } else {
            System.out.println("Give end hour: ");
            String endHourString = scanner.nextLine();

            while (true) {
              try {
                if (endHourString.equals("BACK")) {
                  System.out.println("Cancelled updating task");
                  return;
                }
                endHour = Integer.parseInt(endHourString);
                break;
              } catch (NumberFormatException e) {
                System.out.println(
                        "Given end hour is not an integer or '.', please try again"
                );
                endHourString = scanner.nextLine();
              }
            }

            System.out.println("Give end minute: ");
            String endMinuteString = scanner.nextLine();

            while (true) {
              try {
                if (endMinuteString.equals("BACK")) {
                  System.out.println("Cancelled updating task");
                  return;
                }
                endMinute = Integer.parseInt(endMinuteString);
                break;
              } catch (NumberFormatException e) {
                System.out.println(
                        "Given end minute is not an integer or '.', please try again"
                );
                endMinuteString = scanner.nextLine();
              }
            }
          }

          System.out.println(
                  "Do you want to finish or fail this task? (finish/fail)"
          );
          answer = scanner.nextLine();
          if (answer.equals("BACK")) {
            System.out.println("Cancelled updating task");
            return;
          }

          while (!answer.equals("finish") && !answer.equals("fail")) {
            System.out.println(
                    "Do you want to finish or fail this task? (finish/fail)"
            );
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
              System.out.println("Cancelled updating task");
              return;
            }
          }
          Status newStatus;
          if (answer.equals("finish")) {
            newStatus = Status.FINISHED;
          } else {
            newStatus = Status.FAILED;
          }

          try{
            getController().endTask(projectName, taskName, newStatus, endHour, endMinute);
            System.out.println("Task " + taskName + " successfully updated");
            return;
          } catch (FailTimeAfterSystemTimeException e) {
            System.out.println("ERROR: the fail time is after the system time");
          } catch (InvalidTimeException e) {
            System.out.println("ERROR: the given time is not valid");
          } catch (IncorrectTaskStatusException e) {
            System.out.println("ERROR: the task has the wrong status for this update");
          }
        }
        default ->
          System.out.println("ERROR: Task status doesn't allow an update.");
      }
    }
  }
}
