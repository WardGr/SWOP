package Domain.Command;

import Domain.*;

public class FinishTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String taskName;
    private final String projectName;
    private final User user;

    public FinishTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, User user){
        this.taskManSystem = taskManSystem;
        this.taskName = taskName;
        this.projectName = projectName;
        this.user = user;
    }

    @Override
    public void execute() throws ProjectNotFoundException, EndTimeBeforeStartTimeException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException {
        taskManSystem.failTask(taskName, projectName, user);
    }

    @Override
    public void undo() throws UndoNotPossibleException {
        throw new UndoNotPossibleException(); // TODO implement
    }

    @Override
    public String information() {
        return "Finish task " + taskName + " in project " + projectName;
    }

    @Override
    public boolean undoPossible(){
        return true;
    }
}
