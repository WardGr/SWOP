package Domain.Command;

 import Application.CreateTaskController;
 import Application.IncorrectPermissionException;
 import Domain.*;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.List;
 import java.util.Set;

 public class CreateTaskCommand extends Command {
         private TaskManSystem taskManSystem;
         private String projectName;
         private String taskName;
         private String description;
         private Time durationTime;
         private double deviation;
         private List<Role> roles;
         private Set<String> previousTasks;
         private Set<String> nextTasks;

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
                                 Set<String> previousTasks,
                                 Set<String> nextTasks) {
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
         public void undo() throws TaskNotFoundException, IncorrectTaskStatusException {
             getTaskManSystem().deleteTask(projectName, taskName);
         }

         @Override
         public void execute() throws UserNotFoundException, ProjectNotFoundException, InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException, NonDeveloperRoleException {
             getTaskManSystem().addTaskToProject(projectName, taskName, description, durationTime, deviation, roles, previousTasks, nextTasks);
         }

         @Override
         public String information() {
             return "create task " + taskName;
         }
 }