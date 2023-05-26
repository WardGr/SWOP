package Domain.Command.UpdateDependenciesCommands;

 import Domain.Command.Command;
 import Domain.Task.IncorrectTaskStatusException;
 import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.Project.TaskNotFoundException;
 import Domain.Task.LoopDependencyGraphException;

 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to remove a next task.
 * This command is used to remove a next task from a task in a project.
 * This command can always be undone.
 */
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


    /**
     * Executes the command to remove a next task.
     *
     * @throws ProjectNotFoundException if the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException    if the given taskName does not correspond to an existing task
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().removeNextTaskFromProject(getProjectName(), getTaskName(), getNextProjectName() , getNextTaskName() );
    }

    /**
     * Undoes the command to remove a next task.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().addNextTaskToProject(getProjectName(), getTaskName(), getNextProjectName() , getNextTaskName() );
        }
        catch (ProjectNotFoundException | TaskNotFoundException | IncorrectTaskStatusException |
               LoopDependencyGraphException e) {
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
        return "Remove next task";
    }

    @Override
    public String getDetails(){
        return "Remove next task (" + getNextProjectName() + ", " + getNextTaskName()  + ") from task (" + getProjectName() + ", " + getTaskName() + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("nextProjectName", getNextProjectName());
        arguments.put("nextTaskName", getNextTaskName() );
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "nextProjectName", "nextTaskName"));
    }
 }