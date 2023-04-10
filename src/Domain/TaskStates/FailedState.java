package Domain.TaskStates;

import Domain.IncorrectTaskStatusException;
import Domain.Status;
import Domain.Time;

public class FailedState implements TaskState {
    @Override
    public Status getStatus() {
        return Status.FAILED;
    }

    @Override
    public String toString() {
        return "failed";
    }

    public void replaceTask(Task replaces, Task replacement) throws IncorrectTaskStatusException {
        for (Task prevTask : replaces.getPreviousTasks()) {
            prevTask.removeNextTaskDirectly(replaces);
            replaces.removePreviousTaskDirectly(prevTask);

            prevTask.addNextTaskDirectly(replacement);
            replacement.addPreviousTaskDirectly(prevTask);
        }
        for (Task nextTask : replaces.getNextTasks()) {
            nextTask.removePreviousTaskDirectly(replaces);
            replaces.removeNextTaskDirectly(nextTask);

            nextTask.addPreviousTaskDirectly(replacement);
            replacement.addNextTaskDirectly(nextTask);
        }

        replacement.setProject(replaces.getProject());

        replaces.setReplacementTask(replacement);
        replacement.setReplacesTask(replaces);

        replacement.updateAvailability();

        try {
            replacement.setRequiredRoles(replaces.getRequiredRoles());
        } catch (NonDeveloperRoleException e) {
            throw new RuntimeException(e); // TODO het zou echt een grote fout zijn als dit niet klopt, RTE goed?
        }
    }
}
