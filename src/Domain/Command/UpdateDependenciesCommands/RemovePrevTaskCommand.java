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

    /**
     * Executes the command to remove a next task.
     *
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException        if the given taskName does not correspond to an existing task
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().removePrevTaskFromProject(getProjectName(), getTaskName(), getPrevProjectName(), getPrevTaskName());
    }

    /**
     * Undoes the command to remove a next task.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().addPrevTaskToProject(getProjectName(), getTaskName(), getPrevProjectName(), getPrevTaskName());
        } catch (ProjectNotFoundException | TaskNotFoundException | IncorrectTaskStatusException | LoopDependencyGraphException e) {
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
        return "Remove previous task";
    }

    @Override
    public String getDetails(){
        return "Remove previous task (" + getPrevProjectName() + ", " + getPrevTaskName() + ") from task (" + getProjectName() + ", " + getTaskName() + ")";
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