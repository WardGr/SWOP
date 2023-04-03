package Domain.TaskStates;

import Domain.ReplacedTaskNotFailedException;
import Domain.Status;
import Domain.Time;

import java.util.LinkedList;

public class FailedState implements TaskState {

    public Task replaceTask(Task task, String taskName, String description, Time duration, double deviation) {
        Task newTask = new Task(taskName, description, duration, deviation, new LinkedList<>(), task.getUser());

        for (Task prevTask : task.getPreviousTasks()) {
            prevTask.removeNextTask(task);
            task.removePreviousTask(prevTask);
            prevTask.addNextTask(newTask);
            newTask.addPreviousTask(prevTask);
        }

        for (Task nextTask : task.getNextTasks()) {
            nextTask.removePreviousTask(task);
            task.removeNextTask(nextTask);
            nextTask.addPreviousTask(newTask);
            newTask.addNextTask(nextTask);
        }

        task.setReplacementTask(newTask);
        newTask.setReplacesTask(task);

        return newTask;
    }

    @Override
    public Status getStatus() {
        return Status.FAILED;
    }

    @Override
    public String toString() {
        return "failed";
    }
}
