package Domain.Command;

import Domain.NewTimeBeforeSystemTimeException;
import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.Time;

public class AdvanceTimeCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final int minutes;

    public AdvanceTimeCommand(TaskManSystem taskManSystem, int minutes){
        this.taskManSystem = taskManSystem;
        this.minutes = minutes;
    }

    @Override
    public void execute() throws NewTimeBeforeSystemTimeException {
        taskManSystem.advanceTime(minutes);
    }

    @Override
    public void undo() throws UndoNotPossibleException {
        throw new UndoNotPossibleException();
    }

    @Override
    public String information() {
        return "Advance time " + minutes + " minutes";
    }

    @Override
    public boolean undoPossible(){
        return false;
    }
}
