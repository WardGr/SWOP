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
    private final TaskManSystem taskmansystem;
    private final String projectName;
    private final String taskName;
    private final String nextProjectName;
    private final String nextTaskName;

    public AddNextTaskCommand(TaskManSystem taskmansystem, String projectName, String taskName, String nextProjectName, String nextTaskName) {
        this.taskmansystem = taskmansystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.nextProjectName = nextProjectName;
        this.nextTaskName = nextTaskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        taskmansystem.addNextTaskToProject(projectName, taskName, nextProjectName, nextTaskName);
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException {
        taskmansystem.removeNextTaskFromProject(projectName, taskName, nextProjectName, nextTaskName);
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
        return "Add next task (" + nextProjectName + ", " + nextTaskName + ") to task (" + projectName + ", " + taskName + ")";
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