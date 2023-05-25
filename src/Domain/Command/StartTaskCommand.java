package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.TaskData;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to start a task.
 * This command is used to start a task in a project.
 * This command can always be undone.
 */
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

    /**
     * Executes the command to start a task.
     *
     * @throws ProjectNotFoundException             if the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException                if the given taskName does not correspond to an existing task
     * @throws IncorrectTaskStatusException         if the given task is not pending or available
     * @throws UserAlreadyAssignedToTaskException   if the given user is already assigned to the given task
     * @throws IncorrectRoleException               if the given user does not have the given role
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        getTaskManSystem().startTask(getProjectName(), getTaskName(), getUser(), getRole());

    }

    /**
     * Undoes the command to start a task.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().undoStartTask(getProjectName(), getTaskName(), getUser());
            // Reinstate previous task if it was pending
            if (getPreviousTaskRole() != null && getPreviousTaskName() != null && getPreviousProjectName() != null){
                getTaskManSystem().startTask(getPreviousProjectName(), getPreviousTaskName(), getUser(), getPreviousTaskRole());
            }
        }
        catch (ProjectNotFoundException | TaskNotFoundException | IncorrectTaskStatusException | IncorrectUserException | UserAlreadyAssignedToTaskException | IncorrectRoleException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getName(){
        return "Start task";
    }

    @Override
    public String getDetails() {
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
