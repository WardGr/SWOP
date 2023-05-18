package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.TaskData;

public class StartTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final User user;
    private final Role role;
    private final TaskData prevTaskUser;

    public StartTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, User user, Role role){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.user = user;
        this.role = role;

        this.prevTaskUser = user.getTaskData();

    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask(projectName, taskName, user, role);

    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.stopTask(projectName, taskName, user);
        if (prevTaskUser != null){
            Role previousRole = prevTaskUser.getUserNamesWithRole().get(user.getUsername());
            taskManSystem.startTask(prevTaskUser.getProjectName(), prevTaskUser.getName(), user, previousRole);
        }
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
