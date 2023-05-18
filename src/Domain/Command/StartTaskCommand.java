package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;

public class StartTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final User user;
    private final Role role;

    public StartTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, User user, Role role){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.user = user;
        this.role = role;

    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask(projectName, taskName, user, role);

    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException {
        taskManSystem.stopTask(projectName, taskName, user);
    }

    @Override
    public String information() {
        return "Start task " + taskName + " in project " + projectName + " with role " + role.toString();
    }

    @Override
    public boolean undoPossible(){
        return true;
    }
}
