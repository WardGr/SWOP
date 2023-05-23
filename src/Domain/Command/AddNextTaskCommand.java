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

/**
 * Implements the Command interface and contains all the data needed to add a next task to a task.
 * This command is used to add a next task to a task in a project.
 * This command can always be undone.
 */
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

    /**
     * @return current TaskManSystem instance
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    /**
     * @return string containing the name of the project
     */
    private String getProjectName() {
        return projectName;
    }

    /**
     * @return string containing the name of the task
     */
    private String getTaskName() {
        return taskName;
    }

    /**
     * @return string containing the name of the project of the next task
     */
    private String getNextProjectName() {
        return nextProjectName;
    }

    /**
     * @return string containing the name of the next task
     */
    private String getNextTaskName() {
        return nextTaskName;
    }

    /**
     * Adds a next task to a task.
     * This command can always be undone.
     *
     * @throws TaskNotFoundException        if taskName or nextTaskName do not correspond to an existing task within the given project
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project
     * @throws IncorrectTaskStatusException if the task corresponding to nextTaskName is not available or unavailable
     * @throws LoopDependencyGraphException if adding this task causes a loop in the dependency graph of the given project
     * @post if the task corresponding to nextTaskName is AVAILABLE, then sets taskName's status to UNAVAILABLE
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        getTaskManSystem().addNextTaskToProject(getProjectName(), getTaskName(), getNextProjectName(), getNextTaskName());
    }

    /**
     * Removes the next task from a task.
     *
     * @post the next task is removed from the given task and the system returns to the state before the execution of this command
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().removeNextTaskFromProject(getProjectName(), getTaskName(), getNextProjectName(), getNextTaskName());
        }
        catch( ProjectNotFoundException | TaskNotFoundException e){
            // this should never happen
            throw new RuntimeException();
        }
    }

    @Override
    public boolean undoPossible(){
        return true;
    }


    @Override
    public String getName() {
        return "Add next task";
    }


    @Override
    public String getDetails(){
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