package Domain.Command;

 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;

public class RemovePrevTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final String prevProjectName;
    private final String prevTaskName;

    public RemovePrevTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, String prevProjectName, String prevTaskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.prevProjectName = prevProjectName;
        this.prevTaskName = prevTaskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        taskManSystem.removePrevTaskFromProject(projectName, taskName, prevProjectName, prevTaskName);
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskManSystem.addPrevTaskToProject(projectName, taskName, prevProjectName, prevTaskName);
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Remove previous task";
    }

    @Override
    public String getExtendedInformation(){
        return "Remove previous task (" + prevProjectName + ", " + prevTaskName + ") from task (" + projectName + ", " + taskName + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", projectName);
        arguments.put("taskName", taskName);
        arguments.put("previousProjectName", prevProjectName);
        arguments.put("previousTaskName", prevTaskName);
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "previousProjectName", "previousTaskName"));
    }
 }