package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;

public class FailTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final User user;

    public FailTaskCommand(TaskManSystem taskManSystem, String taskName, String projectName, User user){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.user = user;
    }

    @Override
    public void execute() throws ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException {
        taskManSystem.failTask(projectName, taskName, user);
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        taskManSystem.restartTask(projectName, taskName);
    }

    @Override
    public String information() {
        return "Fail task " + taskName + " in project " + projectName;
    }

    @Override
    public boolean undoPossible(){
        return true;
    }
}
