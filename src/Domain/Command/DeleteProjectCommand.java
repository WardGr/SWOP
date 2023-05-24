package Domain.Command;

import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to delete a project.
 * This command is used to delete a project.
 * This command can never be undone.
 */
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

    /**
     * Deletes the project with the given projectName.
     *
     * @throws ProjectNotFoundException   If the given projectName does not correspond to an existing project
     */
    @Override
    public void execute() throws ProjectNotFoundException {
        getTaskManSystem().deleteProject(getProjectName());
    }

    @Override
    public String getName(){
        return "Delete project";
    }

    @Override
    public String getDetails(){
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
