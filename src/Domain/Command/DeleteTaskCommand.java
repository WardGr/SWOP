package Domain.Command;

import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to delete a task.
 * This command is used to delete a task.
 * This command can never be undone.
 */
public class DeleteTaskCommand implements Command {

    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;

    public DeleteTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
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


    /**
     * Deletes a task in a project.
     *
     * @throws ProjectNotFoundException    If the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException       If the given taskName does not correspond to an existing task
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().deleteTask(getProjectName(), getTaskName());
    }

    @Override
    public String getName(){
        return "Delete task";
    }

    @Override
    public String getDetails(){
        return "Delete task (" + getProjectName() + ", " + getTaskName() + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName"));
    }
    
}
