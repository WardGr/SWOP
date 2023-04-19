package UserInterface;

import Application.EndTaskController;
import Application.IncorrectPermissionException;
import Domain.*;
import Domain.TaskStates.TaskProxy;

import java.util.Scanner;

public class EndTaskUI {
    private final EndTaskController controller;

    public EndTaskUI(EndTaskController controller) {
        this.controller = controller;
    }

    private EndTaskController getController() {
        return controller;
    }

    public void endTask() {
        if (!getController().endTaskPreconditions()) {
            System.out.println("ERROR: You need a developer role to call this function.");
            return;
        }
        if (getController().getUserTaskData() == null || getController().getUserTaskData().getStatus() == Status.PENDING) {
            System.out.println("ERROR: You are currently not working on an executing task.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        printExecutingTask();

        System.out.println("Do you want to finish or fail your current task? (finish/fail)");
        String answer = scanner.nextLine();
        if (answer.equals("BACK")) {
            System.out.println("Cancelled ending task");
            return;
        }

        while (!answer.equals("finish") && !answer.equals("fail")) {
            System.out.println("Do you want to finish or fail your current task? (finish/fail)");
            answer = scanner.nextLine();
            if (answer.equals("BACK")) {
                System.out.println("Cancelled ending task");
                return;
            }
        }
        System.out.println();

        try {

            if (answer.equals("finish")) {
                getController().finishCurrentTask();
                System.out.println("Successfully finished the current executing task");
            } else {
                getController().failCurrentTask();
                System.out.println("Successfully changed current executing task status to failed");
            }
        } catch (IncorrectPermissionException e) {
            System.out.println("ERROR: You need to have the developer role to call this function.");
        } catch (ProjectNotFoundException e) {
            System.out.println("ERROR: Project name could not be found.");
        } catch (TaskNotFoundException e) {
            System.out.println("ERROR: Task name could not be found.");
        } catch (IncorrectTaskStatusException | IncorrectUserException e) {
            System.out.println("ERROR: " + e.getMessage());
        } catch (EndTimeBeforeStartTimeException e) {
            System.out.println("ERROR: The end time is before the start time of the task.");
        }
    }


    private void printExecutingTask() {
        System.out.print("You are currently working on task: ");
        TaskProxy executingTaskData = getController().getUserTaskData();
        if (executingTaskData != null) {
            System.out.println(executingTaskData.getName() + ", belonging to project: " + executingTaskData.getProjectName());
        } else {
            System.out.println("You are currently not executing a task.");
        }
        System.out.println();
    }
}
