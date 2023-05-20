package Domain.Command;

import Domain.NewTimeBeforeSystemTimeException;
import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.Time;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public String getInformation(){
        return "Set new time";
    }

    @Override
    public String getExtendedInformation(){
        return "Set new time " + time.toString();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("newTime", time.toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("newTime"));
    }
}
