package Domain.Command;

 import Application.*;
 import Domain.*;

 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;

public class CreateProjectCommand implements Command {

    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String projectDescription;
    private final Time dueTime;

    public CreateProjectCommand(
    TaskManSystem taskManSystem,
            String projectName,
            String projectDescription,
            Time dueTime) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.dueTime = dueTime;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private String getProjectName() {
        return projectName;
    }

    private String getProjectDescription() {
        return projectDescription;
    }

    private Time getDueTime() {
        return dueTime;
    }

    @Override
    public void execute() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        getTaskManSystem().createProject(getProjectName(), getProjectDescription(), getDueTime());
    }

    @Override
    public void undo() throws ProjectNotFoundException {
        getTaskManSystem().deleteProject(getProjectName());
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Create project";
    }

    @Override
    public String getExtendedInformation(){
        return "Create project " + getProjectName();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("projectDescription", getProjectDescription());
        arguments.put("dueTime", getDueTime().toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "projectDescription", "dueTime"));
    }

 }