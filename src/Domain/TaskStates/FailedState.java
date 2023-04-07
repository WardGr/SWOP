package Domain.TaskStates;

import Domain.IncorrectTaskStatusException;
import Domain.ReplacedTaskNotFailedException;
import Domain.Status;
import Domain.Time;

import java.util.LinkedList;

public class FailedState implements TaskState {

    public Task replaceTask(Task task, String taskName, String description, Time duration, double deviation) {
        /*
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
        */

        return null;
    }

    @Override
    public Status getStatus() {
        return Status.FAILED;
    }

    @Override
    public String toString() {
        return "failed";
    }

    @Override
    public void updateNextTaskState(Task task) {
        task.setState(new UnavailableState());
    }

    public void replaceTask(Task replaces, Task replacementTask) {
        for (Task prevTask : replaces.getPreviousTasks()){
            prevTask.removeNextTask(replaces);
            replaces.removePreviousTask(prevTask);

            prevTask.addNextTask(replacementTask);
            replacementTask.addPreviousTask(prevTask);
        }
        for (Task nextTask : replaces.getNextTasks()){
            nextTask.removePreviousTask(replaces);
            replaces.removeNextTask(nextTask);

            nextTask.addPreviousTask(replacementTask);
            replacementTask.addNextTask(nextTask);
        }

        replaces.setReplacementTask(replacementTask);
        replacementTask.setReplacesTask(replaces);

        //TODO availability van replacement task? normaal staat die op available en is dat in orde

        try{
            replacementTask.setRequiredRoles(replaces.getRequiredRoles());
        } catch (NonDeveloperRoleException e) {
            throw new RuntimeException(e); // TODO het zou echt een grote fout zijn als dit niet klopt, RTE goed?
        }
    }
}
