package Domain.Command;

import Domain.TaskManSystem;
import Domain.Time;

public class ReplaceTaskCommand extends Command {

    private TaskManSystem taskManSystem;
    private String projectName;
    private Time durationTime;
    private double deviation;
    private String replaces;


    public ReplaceTaskCommand(TaskManSystem taskManSystem,
                        String projectName,
                        String taskName,
                        String description,
                        Time durationTime,
                        double deviation,
                        String replaces) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.durationTime = durationTime;
        this.deviation = deviation;
        this.replaces = replaces;
        
    }

    @Override
    public boolean reversePossible() {
        return false;
    }

    @Override
    public void execute() throws Exception {
        taskManSystem.replaceTaskInProject(projectName, projectName, projectName, durationTime, deviation, replaces);
    }
    
}
