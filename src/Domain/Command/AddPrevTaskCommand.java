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

public class AddPrevTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final String prevProjectName;
    private final String prevTaskName;

    public AddPrevTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, String prevProjectName, String prevTaskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.prevProjectName = prevProjectName;
        this.prevTaskName = prevTaskName;
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

    private String getPrevProjectName() {
        return prevProjectName;
    }

    private String getPrevTaskName() {
        return prevTaskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        getTaskManSystem().addPrevTaskToProject(getProjectName(), getTaskName(), getPrevProjectName(), getPrevTaskName());
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().removePrevTaskFromProject(getProjectName(), getTaskName(), getPrevProjectName(), getPrevTaskName());
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Add previous task";
    }

    @Override
    public String getExtendedInformation(){
        return "Add previous task (" + getPrevProjectName() + ", " + getPrevTaskName() + ") to task (" + getProjectName() + ", " + getTaskName() + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("previousProjectName", getPrevProjectName());
        arguments.put("previousTaskName", getPrevTaskName());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "previousProjectName", "previousTaskName"));
    }
 }