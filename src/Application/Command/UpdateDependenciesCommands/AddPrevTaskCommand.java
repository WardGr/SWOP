package Application.Command.UpdateDependenciesCommands;

 import Application.Command.Command;
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
 * Implements the Command interface and contains all the data needed to add a previous task to a task.
 * This command is used to add a previous task to a task in a project.
 * This command can always be undone.
 *
 */
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

    /**
     * Adds a previous task to a task in a project.
     * This command can always be undone.
     *
     * @throws ProjectNotFoundException     If the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException        If taskName or prevTaskName do not correspond to a task within the given project
     * @throws IncorrectTaskStatusException if the status of the taskName task is not available or unavailable
     * @throws LoopDependencyGraphException if adding this previous task create a loop in the dependency graph
     * @post if the task corresponding to taskName is AVAILABLE, then sets taskName's status to UNAVAILABLE
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        getTaskManSystem().addPrevTaskToProject(getProjectName(), getTaskName(), getPrevProjectName(), getPrevTaskName());
    }

    /**
     * Undoes the addition of a previous task to a task in a project, returning the system to its previous state.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().removePrevTaskFromProject(getProjectName(), getTaskName(), getPrevProjectName(), getPrevTaskName());
        }
        catch (ProjectNotFoundException | TaskNotFoundException e) {
            // This should never happen
            throw new RuntimeException();
        }
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getName(){
        return "Add previous task";
    }

    @Override
    public String getDetails(){
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