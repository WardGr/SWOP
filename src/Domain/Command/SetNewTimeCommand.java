package Domain.Command;

import Domain.NewTimeBeforeSystemTimeException;
import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.Time;

public class SetNewTimeCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final Time time;

    public SetNewTimeCommand(TaskManSystem taskManSystem, Time newTime){
        this.taskManSystem = taskManSystem;
        this.time = newTime;
    }

    @Override
    public void execute() throws NewTimeBeforeSystemTimeException {
        taskManSystem.advanceTime(time);
    }

    @Override
    public void undo() throws UndoNotPossibleException {
        throw new UndoNotPossibleException();
    }

    @Override
    public String information() {
        return "Set new time " + time.toString();
    }

    @Override
    public boolean undoPossible(){
        return false;
    }
}
