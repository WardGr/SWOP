package Domain.Command;

 import Application.*;
 import Domain.*;

 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;

public class CreateProjectCommand implements Command {

    final TaskManSystem taskManSystem;
    final String projectName;
    final String projectDescription;
    final Time dueTime;

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

    @Override
    public void execute() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        taskManSystem.createProject(projectName, projectDescription, dueTime);
    }

    @Override
    public void undo() throws ProjectNotFoundException {
        taskManSystem.deleteProject(projectName);
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
        return "Create project " + projectName;
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", projectName);
        arguments.put("projectDescription", projectDescription);
        arguments.put("dueTime", dueTime.toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "projectDescription", "dueTime"));
    }

 }