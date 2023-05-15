package Domain.Command;

import Domain.*;

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
    public void undo() throws TaskNotFoundException, IncorrectTaskStatusException {
        getTaskManSystem().deleteTask(projectName, taskName);
    }

    @Override
    public boolean undoPossible() {
        return true;
    }
}
