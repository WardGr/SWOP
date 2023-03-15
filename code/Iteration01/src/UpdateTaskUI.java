import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UpdateTaskUI {

  private final UpdateTaskController controller;

  public UpdateTaskUI(Session session, TaskManSystem taskManSystem) {
    controller = new UpdateTaskController(session, taskManSystem);
  }

  private UpdateTaskController getController() {
    return controller;
  }

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

  public void chooseUpdateTask() throws IncorrectPermissionException {
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

    while(true) {

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
        taskNotFoundError();
      } catch (UserNotAllowedToChangeTaskException e) {
        userNotAllowedToUpdateTaskError();
      }
    }
  }

  public void showTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
    String taskDetails = controller.showTask(projectName, taskName);
    List<Status> nextStatuses = controller.getNextStatuses(projectName, taskName);

    System.out.println(taskDetails);
    System.out.println("-- Possible Next Statuses --");
    for (Status status : nextStatuses) {
      System.out.println("- " + status.toString());
    }
  }

  public void updateForm(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException, UserNotAllowedToChangeTaskException {
    Status status = controller.getStatus(projectName, taskName);
    int systemHour = controller.getSystemHour();
    int systemMinute = controller.getSystemMinute();

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
            controller.startTask(projectName, taskName, startHour, startMinute);
            System.out.println("Task " + taskName + " successfully updated");
            return;
          } catch (InvalidTimeException e) {
            invalidTimeError();
          } catch (IncorrectTaskStatusException e) {
            incorrectTaskStatusError();
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
            controller.endTask(projectName, taskName, newStatus, endHour, endMinute);
            System.out.println("Task " + taskName + " successfully updated");
            return;
          } catch (FailTimeAfterSystemTimeException e) {
            failTimeAfterSystemTimeError();
          } catch (InvalidTimeException e) {
            invalidTimeError();
          } catch (IncorrectTaskStatusException e) {
            incorrectTaskStatusError();
          }
        }
      }
    }
  }

  public void permissionError() {

  }

  public void taskNotFoundError() {
    System.out.println("ERROR: the given task could not be found");
  }

  public void invalidTimeError() {
    System.out.println("ERROR: the given time is not valid");
  }

  public void userNotAllowedToUpdateTaskError() {
    System.out.println("ERROR: you are not allowed to change this task");
  }

  public void failTimeAfterSystemTimeError() {
    System.out.println("ERROR: the fail time is after the system time");
  }

  public void incorrectTaskStatusError() {
    System.out.println("ERROR: the task has the wrong status for this update");
  }
}
