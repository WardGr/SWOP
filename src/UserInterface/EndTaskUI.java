package UserInterface;

import Application.EndTaskController;
import Application.IncorrectPermissionException;
import Domain.*;
import Domain.TaskStates.TaskData;

import java.util.Scanner;

/**
 * Handles user input for the endtask use-case, requests necessary domain-level information from the EndTaskController
 */
public class EndTaskUI {
    private final EndTaskController controller;

    /**
     * Creates a new UI object
     *
     * @param controller Controller with which this UI should communicate to access the domain
     */
    public EndTaskUI(EndTaskController controller) {
        this.controller = controller;
    }

    /**
     * @return This UI's controller
     */
    private EndTaskController getController() {
        return controller;
    }

    /**
     * Prints the end task form, asking the user if they want to fail or finish their current task
     */
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

    /**
     * Prints the task the current user is working on, or a message if the user is not working on any
     */
    private void printExecutingTask() {
        System.out.print("You are currently working on task: ");
        TaskData executingTaskData = getController().getUserTaskData();
        if (executingTaskData != null) {
            System.out.println(executingTaskData.getName() + ", belonging to project: " + executingTaskData.getProjectName());
        } else {
            System.out.println("You are currently not executing a task.");
        }
        System.out.println();
    }
}
