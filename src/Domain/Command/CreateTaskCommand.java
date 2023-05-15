package Domain.Command;

 import Domain.*;
 import Domain.TaskStates.IllegalTaskRolesException;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.List;
 import java.util.Set;

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
             getTaskManSystem().addTaskToProject(projectName, taskName, description, durationTime, deviation, roles, previousTasks, nextTasks);
         }

         @Override
         public void undo() throws TaskNotFoundException, ProjectNotFoundException {
             getTaskManSystem().deleteTask(projectName, taskName);
         }

         @Override
         public String information() {
             return "create task " + taskName;
         }
 }