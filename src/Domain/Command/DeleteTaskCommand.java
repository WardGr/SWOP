package Domain.Command;

import Domain.TaskManSystem;

public class DeleteTaskCommand extends Command {

    private TaskManSystem taskManSystem;
    private String projectName;
    private String taskName;

    public DeleteTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
    }

    @Override
    public boolean reversePossible() {
        return false;
    }

    @Override
    public void execute() throws Exception {
        taskManSystem.deleteTask(projectName, taskName);
    }
    
}
