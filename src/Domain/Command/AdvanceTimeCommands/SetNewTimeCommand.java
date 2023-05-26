package Domain.Command.AdvanceTimeCommands;

import Domain.Command.Command;
import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.DataClasses.Time;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to set a new time.
 * This command is used to set a new time in the system.
 * This command can never be undone.
 */
public class SetNewTimeCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final Time time;

    public SetNewTimeCommand(TaskManSystem taskManSystem, Time newTime){
        this.taskManSystem = taskManSystem;
        this.time = newTime;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private Time getTime() {
        return time;
    }

    /**
     * Executes the command to set a new time.
     *
     * @throws NewTimeBeforeSystemTimeException if getTime() < getTaskManSystem().getTime()
     */
    @Override
    public void execute() throws NewTimeBeforeSystemTimeException {
        getTaskManSystem().advanceTime(getTime());
    }

    @Override
    public String getName(){
        return "Set new time";
    }

    @Override
    public String getDetails(){
        return "Set new time " + getTime().toString();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("newTime", getTime().toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("newTime"));
    }
}
