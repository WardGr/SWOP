import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UpdateTaskUI {

  private final UpdateTaskController controller;

  public UpdateTaskUI(Session session, TaskManSystem taskManSystem) {
    controller = new UpdateTaskController(this, session, taskManSystem);
  }

  public void updateTaskStatus() {
    controller.showAvailableAndExecuting();
  }

  public void printAvailableAndExecuting(
    List<Map.Entry<String, String>> availableTasks,
    List<Map.Entry<String, String>> executingTasks
  ) {
    System.out.println("*** AVAILABLE TASKS ***");
    for (Map.Entry<String, String> entry : availableTasks) {
      System.out.println(
        "Project: " + entry.getKey() + " Task: " + entry.getValue()
      );
    }
    System.out.println();
    System.out.println("*** EXECUTING TASKS ***");
    for (Map.Entry<String, String> entry : executingTasks) {
      System.out.println(
        "Project: " + entry.getKey() + "  ---  Task: " + entry.getValue()
      );
    }
    chooseUpdateTask();
  }

  public void chooseUpdateTask() {
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
    controller.updateTaskForm(projectName, taskName);
  }

  public void showTask(String taskDetails, List<Status> nextStatuses) {
    System.out.println(taskDetails);
    System.out.println("-- Possible Next Statuses --");
    for (Status status : nextStatuses) {
      System.out.println("- " + status.toString());
    }
  }

  public void updateForm(
    String projectName,
    String taskName,
    Status status,
    int systemHour,
    int systemMinute
  ) {
    Scanner scanner = new Scanner(System.in);
    switch (status) {
      case AVAILABLE -> {
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
          controller.startTask(projectName, taskName, systemHour, systemMinute);
        } else {
          System.out.println("Give start hour: ");
          String startHourString = scanner.nextLine();

          int startHour;
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

          int startMinute;
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
          controller.startTask(projectName, taskName, startHour, startMinute);
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

        controller.endTask(
          projectName,
          taskName,
          newStatus,
          endHour,
          endMinute
        );
      }
    }
  }

  public void printAccessError(Role role) {
    System.out.println(
      "You must be logged in with the " +
      role.toString() +
      " role to call this function"
    );
  }

  public void taskNotFoundError() {
    System.out.println("ERROR: the given task could not be found");
    chooseUpdateTask();
  }

  public void printNotValidTimeError(String projectName, String taskName) {
    System.out.println("ERROR: the given time is not valid");
    controller.updateTaskForm(projectName, taskName);
  }

  public void userNotAllowedToUpdateTaskError() {
    System.out.println("ERROR: you are not allowed to change this task");
    chooseUpdateTask();
  }

  public void failTimeAfterSystemTime(String projectName, String taskName) {
    System.out.println("ERROR: the fail time is after the system time");
    controller.updateTaskForm(projectName, taskName);
  }

  public void wrongTaskStatusException(String projectName, String taskName) {
    System.out.println("ERROR: the task has the wrong status for this update");
    controller.updateTaskForm(projectName, taskName);
  }
}
