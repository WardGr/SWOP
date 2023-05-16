package Domain.Command;

import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;

public class DeleteProjectCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;

    public DeleteProjectCommand(TaskManSystem taskManSystem, String projectName){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
    }

    @Override
    public void execute() throws ProjectNotFoundException {
        taskManSystem.deleteProject(projectName);
    }

    @Override
    public void undo() throws UndoNotPossibleException {
        throw new UndoNotPossibleException();
    }

    @Override
    public String information() {
        return "Delete project " + projectName;
    }

    @Override
    public boolean undoPossible(){
        return false;
    }
}
