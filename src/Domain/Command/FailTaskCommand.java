package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to fail a task.
 * This command is used to fail a task in a project.
 * This command can always be undone.
 */
public class FailTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final User user;

    public FailTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, User user){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.user = user;
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

    /**
     * Executes the command to fail a task.
     *
     * @throws ProjectNotFoundException             if the given projectName does not correspond to an existing project
     * @throws EndTimeBeforeStartTimeException      if the fail time of the task is before the start time
     * @throws TaskNotFoundException                if the given taskName does not correspond to an existing task
     * @throws IncorrectTaskStatusException         if the given task is not executing
     * @throws IncorrectUserException               if the given user is not assigned to the given task
     */
    @Override
    public void execute() throws ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException {
        getTaskManSystem().failTask(getProjectName(), getTaskName(), getUser());
    }

    /**
     * Undoes the command to fail a task.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().undoEndTask(getProjectName(), getTaskName());
        } catch (ProjectNotFoundException | TaskNotFoundException | IncorrectTaskStatusException | IncorrectRoleException | UserAlreadyAssignedToTaskException e) {
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
        return "Fail task";
    }

    @Override
    public String getDetails(){
        return "Fail task " + getTaskName() + " in project " + getProjectName();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("user", getUser().getUsername());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "user"));
    }
}
