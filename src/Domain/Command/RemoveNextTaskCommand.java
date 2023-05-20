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

public class RemoveNextTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final String nextProjectName;
    private final String nextTaskName;

    public RemoveNextTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, String nextProjectName, String nextTaskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.nextProjectName = nextProjectName;
        this.nextTaskName = nextTaskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        taskManSystem.removeNextTaskFromProject(projectName, taskName, nextProjectName, nextTaskName);
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskManSystem.addNextTaskToProject(projectName, taskName, nextProjectName, nextTaskName);
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Remove next task";
    }

    @Override
    public String getExtendedInformation(){
        return "Remove next task (" + nextProjectName + ", " + nextTaskName + ") from task (" + projectName + ", " + taskName + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", projectName);
        arguments.put("taskName", taskName);
        arguments.put("nextProjectName", nextProjectName);
        arguments.put("nextTaskName", nextTaskName);
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"));
    }
 }