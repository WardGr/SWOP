package Application.Command.ProjectCommands;

 import Application.Command.Command;
 import Domain.DataClasses.Time;
 import Domain.Project.ProjectNameAlreadyInUseException;
 import Domain.TaskManSystem.DueBeforeSystemTimeException;
 import Domain.TaskManSystem.ProjectNotFoundException;
 import Domain.TaskManSystem.TaskManSystem;

 import java.util.HashMap;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;

/**
 * Implements the Command interface and contains all the data needed to create a project.
 * This command is used to create a project and add it to the system.
 * This command can always be undone.
 */
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

    /**
     * Executes the command, creating a new project with the saved name, description and due time.
     *
     * @throws ProjectNameAlreadyInUseException  if the given project name is already in use
     * @throws DueBeforeSystemTimeException      if the given due time is before the current time of the system
     */
    @Override
    public void execute() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        getTaskManSystem().createProject(getProjectName(), getProjectDescription(), getDueTime());
    }

    /**
     * Undoes the command, deleting the project with the saved name, description and due time and restoring the previous state.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().deleteProject(getProjectName());
        }
        catch (ProjectNotFoundException e) {
            // This should never happen
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getName(){
        return "Create project";
    }

    @Override
    public String getDetails(){
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