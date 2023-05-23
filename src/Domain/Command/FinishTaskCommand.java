package Domain.Command;

import Domain.*;
import Domain.TaskStates.IncorrectRoleException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FinishTaskCommand implements Command {

    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final User user;

    public FinishTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, User user){
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


    @Override
    public void execute() throws ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException {
        getTaskManSystem().finishTask(getProjectName(), getTaskName(), getUser());
    }

    @Override
    public void undo() throws ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        getTaskManSystem().restartTask(getProjectName(), getTaskName());
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Finish task";
    }

    @Override
    public String getExtendedInformation(){
        return "Finish task " + getTaskName() + " in project " + getProjectName();
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
