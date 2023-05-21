package Domain.TaskStates;

import Domain.IncorrectTaskStatusException;
import Domain.Status;

/**
 * Task state class governing the task transitions from the FAILED state
 */
public class FailedState implements TaskState {
    @Override
    public Status getStatus() {
        return Status.FAILED;
    }

    @Override
    public void replaceTask(Task toReplace, Task replacement) throws IncorrectTaskStatusException {
        for (Task prevTask : toReplace.getPrevTasks()) {
            prevTask.removeNextTaskDirectly(toReplace);
            toReplace.removePrevTaskDirectly(prevTask);

            prevTask.addNextTaskDirectly(replacement);
            replacement.addPrevTaskDirectly(prevTask);
        }
        for (Task nextTask : toReplace.getNextTasks()) {
            nextTask.removePrevTaskDirectly(toReplace);
            toReplace.removeNextTaskDirectly(nextTask);

            nextTask.addPrevTaskDirectly(replacement);
            replacement.addNextTaskDirectly(nextTask);
        }

        replacement.setProjectName(toReplace.getProjectName());

        toReplace.setReplacementTask(replacement);
        replacement.setReplacesTask(toReplace);

        replacement.updateAvailability();

        try {
            replacement.setRequiredRoles(toReplace.getRequiredRoles());
        } catch (IllegalTaskRolesException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void restart(Task task) {
        task.setState(new ExecutingState());
    }

    @Override
    public String toString() {
        return getStatus().toString();
    }
}
