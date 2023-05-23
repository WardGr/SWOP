package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;

public class AddNextTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final String nextProjectName;
    private final String nextTaskName;

    public AddNextTaskCommand(TaskManSystem taskmansystem, String projectName, String taskName, String nextProjectName, String nextTaskName) {
        this.taskManSystem = taskmansystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.nextProjectName = nextProjectName;
        this.nextTaskName = nextTaskName;
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

    private String getNextProjectName() {
        return nextProjectName;
    }

    private String getNextTaskName() {
        return nextTaskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        getTaskManSystem().addNextTaskToProject(getProjectName(), getTaskName(), getNextProjectName(), getNextTaskName());
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().removeNextTaskFromProject(getProjectName(), getTaskName(), getNextProjectName(), getNextTaskName());
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation() {
        return "Add next task";
    }

    @Override
    public String getExtendedInformation(){
        return "Add next task (" + getNextProjectName() + ", " + getNextTaskName() + ") to task (" + getProjectName() + ", " + getTaskName() + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("nextProjectName", getNextProjectName());
        arguments.put("nextTaskName", getNextTaskName());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"));
    }
}