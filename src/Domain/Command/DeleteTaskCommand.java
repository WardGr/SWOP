package Domain.Command;

import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeleteTaskCommand implements Command {

    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;

    public DeleteTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        taskManSystem.deleteTask(projectName, taskName);
    }

    @Override
    public String getInformation(){
        return "Delete task";
    }

    @Override
    public String getExtendedInformation(){
        return "Delete task (" + projectName + ", " + taskName + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", projectName);
        arguments.put("taskName", taskName);
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName"));
    }
    
}
