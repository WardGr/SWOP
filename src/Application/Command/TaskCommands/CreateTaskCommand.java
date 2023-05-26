package Application.Command.TaskCommands;

 import Application.Command.Command;
 import Domain.DataClasses.Time;
 import Domain.DataClasses.Tuple;
 import Domain.Project.ProjectNotOngoingException;
 import Domain.Project.TaskNotFoundException;
 import Domain.Task.IncorrectTaskStatusException;
 import Domain.Task.TaskNameAlreadyInUseException;
 import Domain.TaskManSystem.ProjectNotFoundException;
 import Domain.TaskManSystem.TaskManSystem;
 import Domain.Task.IllegalTaskRolesException;
 import Domain.Task.LoopDependencyGraphException;
 import Domain.User.Role;

 import java.util.*;

/**
 * Implements the Command interface and contains all the data needed to create a task.
 * This command is used to create a task and add it to a project.
 * This command can always be undone.
 */
public class CreateTaskCommand implements Command {
    private final TaskManSystem taskManSystem;
    private final String projectName;
    private final String taskName;
    private final String description;
    private final Time durationTime;
    private final double deviation;
    private final List<Role> roles;
    private final Set<Tuple<String,String>> previousTasks;
    private final Set<Tuple<String,String>> nextTasks;

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private String getProjectName() {
        return projectName;
    }

    private String getTaskName() {
        return taskName;
    }

    private String getDescription() {
        return description;
    }

    private Time getDurationTime() {
        return durationTime;
    }

    private double getDeviation() {
        return deviation;
    }

    private List<Role> getRoles() {
        return List.copyOf(roles);
    }

    private Set<Tuple<String,String>> getPreviousTasks() {
        return Set.copyOf(previousTasks);
    }

    private Set<Tuple<String,String>> getNextTasks() {
        return Set.copyOf(nextTasks);
    }

    // Ofwel zo, ofwel geven we een task en project mee, dan doen we rechtstreeks project.deletetask en project.addtask ipv nieuwe objecten te maken
    public CreateTaskCommand(TaskManSystem taskManSystem,
                         String projectName,
                         String taskName,
                         String description,
                         Time durationTime,
                         double deviation,
                         List<Role> roles,
                         Set<Tuple<String,String>> previousTasks,
                         Set<Tuple<String,String>> nextTasks) {
        this.taskManSystem = taskManSystem;
        this.projectName = projectName;
        this.taskName = taskName;
        this.description = description;
        this.durationTime = durationTime;
        this.deviation = deviation;
        this.roles = roles;
        this.previousTasks = previousTasks;
        this.nextTasks = nextTasks;
    }

    /**
     * Executes the command to add a next task to a task, using the data provided.
     *
     * @throws ProjectNotFoundException         if the project which to add the task to does not exist
     * @throws TaskNotFoundException            if any of the next or previous tasks do not exist
     * @throws IncorrectTaskStatusException     if any of the next or previous tasks are not in the correct state to have the newly created task added
     * @throws TaskNameAlreadyInUseException    if the task name is already in use in the project
     * @throws IllegalTaskRolesException        if the task roles are not valid
     * @throws ProjectNotOngoingException       if the project is not in the ongoing state
     * @throws LoopDependencyGraphException     if the task dependencies would create a loop in the dependency graph
     */
    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, TaskNameAlreadyInUseException, IllegalTaskRolesException, ProjectNotOngoingException, LoopDependencyGraphException {
        getTaskManSystem().addTaskToProject(getProjectName(), getTaskName(), getDescription(), getDurationTime(), getDeviation(), getRoles(), getPreviousTasks(), getNextTasks());
    }

    /**
     * Undoes the execution of the command to add a next task to a task.
     */
    @Override
    public void undo() {
        try {
            getTaskManSystem().deleteTask(getProjectName(), getTaskName());
        }
        catch (TaskNotFoundException | ProjectNotFoundException e) {
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
        return "Create task";
    }

    @Override
    public String getDetails(){
        return "Create task (" + getProjectName() + ", " + getTaskName() + ")";
    }

    @Override
    public Map<String,String> getArguments(){
        Map<String,String> arguments = new HashMap<>();
        arguments.put("projectName", getProjectName());
        arguments.put("taskName", getTaskName());
        arguments.put("description", getDescription());
        arguments.put("durationTime", getDurationTime().toString());
        arguments.put("deviation", Double.toString(getDeviation()));
        arguments.put("roles", getRoles().toString());
        arguments.put("previousTasks", getPreviousTasks().toString());
        arguments.put("nextTasks", getNextTasks().toString());
        return arguments;
    }

    @Override
    public List<String> getArgumentNames(){
        return new LinkedList<>(List.of("projectName", "taskName", "description", "durationTime", "deviation", "roles", "previousTasks", "nextTasks"));
    }
}