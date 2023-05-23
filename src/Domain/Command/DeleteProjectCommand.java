package Domain.Command;

import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DeleteProjectCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;

    public DeleteProjectCommand(TaskManSystem taskManSystem, String projectName){
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private String getProjectName() {
        return projectName;
    }

    @Override
    public void execute() throws ProjectNotFoundException {
        getTaskManSystem().deleteProject(getProjectName());
    }

    @Override
    public String getInformation(){
        return "Delete project";
    }

    @Override
    public String getExtendedInformation(){
        return "Delete project " + getProjectName();
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName"));
    }
}
