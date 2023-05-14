package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemPrevCommand extends Command {
     private TaskManSystem taskManSystem;
     private String projectName;
     private String taskName;
     private String nextTaskName;

     public RemPrevCommand(TaskManSystem taskManSystem, String projectName, String taskName, String nextTaskName) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextTaskName = nextTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         taskManSystem.addPrevTaskToProject(projectName, taskName, nextTaskName);

     }

     @Override
     public void redo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         taskManSystem.removePrevTaskFromProject(projectName, taskName, nextTaskName);
     }

     @Override
     public String information() {
         return "Remove previous task " + nextTaskName + " from task " + taskName;
     }
 }