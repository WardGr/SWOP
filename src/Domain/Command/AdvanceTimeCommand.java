package Domain.Command;

import Domain.NewTimeBeforeSystemTimeException;
import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.Time;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public String getInformation(){
        return "Advance time";
    }

    @Override
    public String getExtendedInformation(){
        return "Advance time with " + minutes + " minutes";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("minutes", Integer.toString(minutes));
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("minutes"));
    }
}
