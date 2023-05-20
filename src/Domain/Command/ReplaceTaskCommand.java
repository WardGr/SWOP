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

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        getTaskManSystem().replaceTaskInProject(projectName, taskName, description, durationTime, deviation, replaces);
    }

    @Override
    public void undo() throws ProjectNotFoundException, TaskNotFoundException {
        getTaskManSystem().deleteTask(projectName, taskName);
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
        return "Replace task " + replaces + " by task " + taskName + " in project " + projectName;
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", projectName);
        arguments.put("taskName", taskName);
        arguments.put("description", description);
        arguments.put("durationTime", durationTime.toString());
        arguments.put("deviation", Double.toString(deviation));
        arguments.put("replaces", replaces);
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "description", "durationTime", "deviation", "replaces"));
    }
}
