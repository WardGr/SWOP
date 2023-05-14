package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemNextCommand extends Command {
     private TaskManSystem taskManSystem;
     private String projectName;
     private String taskName;
     private String nextTaskName;

     public RemNextCommand(TaskManSystem taskManSystem, String projectName, String taskName, String nextTaskName) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextTaskName = nextTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         taskManSystem.addNextTaskToProject(projectName, taskName, nextTaskName);

     }

     @Override
     public void redo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         taskManSystem.removeNextTaskFromProject(projectName, taskName, nextTaskName);
     }

     @Override
     public String information() {
         return "Remove next task " + nextTaskName + " from task " + taskName;
     }
 }