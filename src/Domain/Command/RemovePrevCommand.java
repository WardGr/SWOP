package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemovePrevCommand implements Command {
     private final TaskManSystem taskManSystem;
     private final String projectName;
     private final String taskName;
     private final String prevTaskName;

     public RemovePrevCommand(TaskManSystem taskManSystem, String projectName, String taskName, String prevTaskName) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.prevTaskName = prevTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
         taskManSystem.addPrevTaskToProject(projectName, taskName, prevTaskName);
     }

     @Override
     public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException {
         taskManSystem.removePrevTaskFromProject(projectName, taskName, prevTaskName);
     }

     @Override
     public String information() {
         return "Remove previous task " + prevTaskName + " from task " + taskName;
     }
 }