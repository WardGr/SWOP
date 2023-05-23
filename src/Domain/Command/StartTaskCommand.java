package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.TaskData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StartTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final User user;
    private final Role role;
    private final Role previousTaskRole;
    private final String previousProjectName;
    private final String previousTaskName;


    public StartTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, User user, Role role){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.user = user;
        this.role = role;

        TaskData prevTaskUser = user.getTaskData();
        if (prevTaskUser != null) {
            this.previousTaskRole = prevTaskUser.getUserNamesWithRole().get(user.getUsername());
            this.previousProjectName = prevTaskUser.getProjectName();
            this.previousTaskName = prevTaskUser.getName();
        }
        else {
            this.previousTaskRole = null;
            this.previousProjectName = null;
            this.previousTaskName = null;
        }
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.startTask(projectName, taskName, user, role);

    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        taskManSystem.stopTask(projectName, taskName, user);

        // Reinstate previous task
        if (previousTaskRole != null && previousTaskName != null && previousProjectName != null){
            taskManSystem.startTask(previousProjectName, previousTaskName, user, previousTaskRole);
        }
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Start task";
    }

    @Override
    public String getExtendedInformation() {
        return "Start task " + taskName + " in project " + projectName + " with role " + role.toString();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", projectName);
        arguments.put("taskName", taskName);
        arguments.put("user", user.getUsername());
        arguments.put("role", role.toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "user", "role"));
    }
}
