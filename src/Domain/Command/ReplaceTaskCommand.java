package Domain.Command;

import Domain.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        getTaskManSystem().replaceTaskInProject(getProjectName(), getTaskName(), getDescription(), getDurationTime(), getDeviation(), replaces);
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().deleteTask(getProjectName(), getTaskName());
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Replace task";
    }

    @Override
    public String getExtendedInformation(){
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
