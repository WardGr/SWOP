import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UpdateTaskUI {
    private UpdateTaskController controller;

    public UpdateTaskUI(Session session, TaskManSystem taskManSystem, Time systemTime){
        controller = new UpdateTaskController(this, session, taskManSystem, systemTime);
    }

    public void updateTaskStatus() {
        controller.showAvailableAndExecuting();
    }

    public void chooseUpdateTask(){
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

    public void printAvailableAndExecuting(List<Map.Entry<String,String>> availableTasks, List<Map.Entry<String,String>> executingTasks) {
        System.out.println("*** AVAILABLE TASKS ***");
        for (Map.Entry<String,String> entry : availableTasks) {
            System.out.println("Project: " + entry.getKey() + " Task: " + entry.getValue());
        }
        System.out.println();
        System.out.println("*** EXECUTING TASKS ***");
        for (Map.Entry<String,String> entry : executingTasks) {
            System.out.println("Project: " + entry.getKey() + "  ---  Task: " + entry.getValue());
        }
        chooseUpdateTask();
    }

    public void showTask(String taskDetails, List<Status> nextStatuses) {
        System.out.println(taskDetails);
        System.out.println("-- Possible Next Statuses --");
        for (Status status : nextStatuses) {
            System.out.println("- " + status.toString());
        }
    }

    public void updateForm(String projectName, String taskName, Status status, int systemTime) {
        Scanner scanner = new Scanner(System.in);
        switch (status) {
            case AVAILABLE -> {
                System.out.println("Give start time or type '.' to use current system time: ");
                String startTimeString = scanner.nextLine();
                if (startTimeString.equals("BACK")) {
                    return;
                }
                if (startTimeString.equals(".")) {
                    controller.startTask(projectName, taskName, systemTime);
                    return;
                }
                int startTime;
                while (true) {
                    try {
                        startTime = Integer.parseInt(startTimeString);
                        controller.startTask(projectName, taskName, startTime);
                        return;
                    } catch (NumberFormatException e) {
                        System.out.println("Given start time is not an integer or '.', please try again");
                        startTimeString = scanner.nextLine();
                        if (startTimeString.equals("BACK")) {
                            System.out.println("Cancelled task creation");
                            return;
                        }
                        if (startTimeString.equals(".")) {
                            controller.startTask(projectName, taskName, systemTime);
                            return;
                        }
                    }
                }
            }
            case EXECUTING -> {
                System.out.println("Give end time or type '.' to use current system time: ");
                String endTimeString = scanner.nextLine();
                if (endTimeString.equals("BACK")) {
                    return;
                }
                int endTime;
                while (true) {
                    try {
                        if (endTimeString.equals(".")) {
                            endTime = systemTime;
                            break;
                        }
                        endTime = Integer.parseInt(endTimeString);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Given end time is not an integer or '.', please try again");
                        endTimeString = scanner.nextLine();
                        if (endTimeString.equals("BACK")) {
                            System.out.println("Cancelled task creation");
                            return;
                        }
                    }
                }
                System.out.println("Do you want to finish or fail this task? (finish/fail)");
                String answer = scanner.nextLine();

                while (!answer.equals("finish") && !answer.equals("fail")) {
                    System.out.println("Do you want to finish or fail this task? (finish/fail)");
                    answer = scanner.nextLine();
                }
                Status newStatus;
                if (answer.equals("finish")) {
                    newStatus = Status.FINISHED;
                } else {
                    newStatus = Status.FAILED;
                }
                controller.endTask(projectName, taskName, newStatus, endTime);
            }
            default -> {
                System.out.println("Status of this task allows no updates");
            }
        }
    }

    public void updateAvailableTask(String projectName, String taskName){
        System.out.println();
    }

    public void printAccessError(Role role) {
        System.out.println("You must be logged in with the " + role.toString() + " role to call this function");
    }

    public void taskNotFoundError() {
        System.out.println("ERROR: the given task could not be found");

        chooseUpdateTask();
    }
}
