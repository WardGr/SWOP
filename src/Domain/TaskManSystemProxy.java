package Domain;

import java.util.List;

public class TaskManSystemProxy {
    private final TaskManSystem taskManSystem;

    public TaskManSystemProxy (TaskManSystem tms){
        taskManSystem = tms;
    }

    public List<String> getProjectNames(){
        return taskManSystem.getProjectNames();
    }

    public Time getSystemTime(){
        return taskManSystem.getSystemTime();
    }
}
