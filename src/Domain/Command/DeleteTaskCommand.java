package Domain.Command;

import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;

public class DeleteTaskCommand implements Command {

    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;

    public DeleteTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
    }

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException {
        taskManSystem.deleteTask(projectName, taskName);
    }

    @Override
    public boolean undoPossible() {
        return false;
    }
    
}
