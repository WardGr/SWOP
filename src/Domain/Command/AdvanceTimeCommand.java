package Domain.Command;

import Domain.NewTimeBeforeSystemTimeException;
import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.Time;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to advance the time of the system.
 * This command is used to advance the time of the system with a given amount of minutes.
 * This command can not be undone.
 */
public class AdvanceTimeCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final int minutes;

    public AdvanceTimeCommand(TaskManSystem taskManSystem, int minutes){
        this.taskManSystem = taskManSystem;
        this.minutes = minutes;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private int getMinutes() {
        return minutes;
    }

    /**
     * Advances the time of the system with the given amount of minutes.
     * This command can not be undone.
     *
     * @post the time of the system is advanced with the given amount of minutes
     * @throws NewTimeBeforeSystemTimeException if the new time is before the current time of the system
     */
    @Override
    public void execute() throws NewTimeBeforeSystemTimeException {
        getTaskManSystem().advanceTime(getMinutes());
    }

    @Override
    public String getName(){
        return "Advance time";
    }

    @Override
    public String getDetails(){
        return "Advance time with " + getMinutes() + " minutes";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("minutes", Integer.toString(getMinutes()));
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("minutes"));
    }
}
