package Domain.Command;

 import Domain.*;
 import Domain.TaskStates.IllegalTaskRolesException;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.*;

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

    @Override
    public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, TaskNameAlreadyInUseException, IllegalTaskRolesException, ProjectNotOngoingException, LoopDependencyGraphException {
        getTaskManSystem().addTaskToProject(getProjectName(), getTaskName(), getDescription(), getDurationTime(), getDeviation(), getRoles(), getPreviousTasks(), getNextTasks());
    }

    @Override
    public void undo() throws TaskNotFoundException, ProjectNotFoundException {
        getTaskManSystem().deleteTask(getProjectName(), getTaskName());
    }

    @Override
    public boolean undoPossible(){
        return true;
    }

    @Override
    public String getInformation(){
        return "Create task";
    }

    @Override
    public String getExtendedInformation(){
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