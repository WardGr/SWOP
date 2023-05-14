package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class AddNextCommand extends Command {
     private TaskManSystem taskmansystem;
     private String projectName;
     private String taskName;
     private String nextTaskName;

     public AddNextCommand(TaskManSystem taskmansystem, String projectName, String taskName, String nextTaskName) {
         this.taskmansystem = taskmansystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextTaskName = nextTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         taskmansystem.removeNextTaskFromProject(projectName, taskName, nextTaskName);

     }

     @Override
     public void redo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         taskmansystem.addNextTaskToProject(projectName, taskName, nextTaskName);
     }

     @Override
     public String information() {
         return "Add next task " + nextTaskName + " to task " + taskName;
     }
 }