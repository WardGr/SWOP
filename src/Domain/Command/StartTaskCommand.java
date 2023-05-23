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

        TaskData prevTaskUser = getUser().getTaskData();
        if (prevTaskUser != null) {
            this.previousTaskRole = prevTaskUser.getUserNamesWithRole().get(getUser().getUsername());
            this.previousProjectName = prevTaskUser.getProjectName();
            this.previousTaskName = prevTaskUser.getName();
        }
        else {
            this.previousTaskRole = null;
            this.previousProjectName = null;
            this.previousTaskName = null;
        }
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private String getProjectName() {
        return projectName;
    }

    private String getTaskName() {
        return taskName;
    }

    private User getUser() {
        return user;
    }

    private Role getRole() {
        return role;
    }

    private Role getPreviousTaskRole() {
        return previousTaskRole;
    }

    private String getPreviousProjectName() {
        return previousProjectName;
    }

    private String getPreviousTaskName() {
        return previousTaskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        getTaskManSystem().startTask(getProjectName(), getTaskName(), getUser(), getRole());

    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        getTaskManSystem().stopTask(getProjectName(), getTaskName(), getUser());

        // Reinstate previous task
        if (getPreviousTaskRole() != null && getPreviousTaskName() != null && getPreviousProjectName() != null){
            getTaskManSystem().startTask(getPreviousProjectName(), getPreviousTaskName(), getUser(), getPreviousTaskRole());
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
        return "Start task " + getTaskName() + " in project " + getProjectName() + " with role " + getRole().toString();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("user", getUser().getUsername());
        arguments.put("role", getRole().toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "user", "role"));
    }
}
