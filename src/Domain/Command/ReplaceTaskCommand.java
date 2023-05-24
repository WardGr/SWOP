package Domain.Command;

import Domain.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to replace a task.
 * This command is used to replace a task in a project.
 * This command can always be undone.
 */
public class ReplaceTaskCommand implements Command {

    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final String description;
    private final Time durationTime;
    private final double deviation;
    private final String replaces;


    public ReplaceTaskCommand(TaskManSystem taskManSystem,
                        String projectName,
                        String taskName,
                        String description,
                        Time durationTime,
                        double deviation,
                        String replaces) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.description = description;
        this.durationTime = durationTime;
        this.deviation = deviation;
        this.replaces = replaces;
        
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

    private String getDescription() {
        return description;
    }

    private Time getDurationTime() {
        return durationTime;
    }

    private double getDeviation() {
        return deviation;
    }

    private String getReplaces() {
        return replaces;
    }

    /**
     * Executes the command to replace a task.
     *
     * @throws ProjectNotFoundException         if the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException            if replaces does not correspond to the name of an existing task
     * @throws TaskNameAlreadyInUseException    if the given taskName is already in use in the project
     * @throws IncorrectTaskStatusException     if the task is failed
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        getTaskManSystem().replaceTaskInProject(getProjectName(), getTaskName(), getDescription(), getDurationTime(), getDeviation(), getReplaces());
    }

    /**
     * Undoes the command to replace a task.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().deleteTask(getProjectName(), getTaskName());
        }
        catch (ProjectNotFoundException | TaskNotFoundException e) {
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
        return "Replace task";
    }

    @Override
    public String getDetails(){
        return "Replace task " + getReplaces() + " by task " + getTaskName() + " in project " + getProjectName();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("description", getDescription());
        arguments.put("durationTime", getDurationTime().toString());
        arguments.put("deviation", Double.toString(getDeviation()));
        arguments.put("replaces", getReplaces());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "description", "durationTime", "deviation", "replaces"));
    }
}
