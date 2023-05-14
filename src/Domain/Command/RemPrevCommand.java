package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
 import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemPrevCommand extends Command {
     private UpdateDependenciesController controller;
     private String projectName;
     private String taskName;
     private String nextTaskName;

     public RemPrevCommand(UpdateDependenciesController controller, String projectName, String taskName, String nextTaskName) {
         this.controller = controller;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextTaskName = nextTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         controller.addPreviousTask(projectName, taskName, nextTaskName);

     }

     @Override
     public void redo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException {
         controller.removePreviousTask(projectName, taskName, nextTaskName);
     }

     @Override
     public String information() {
         return "Remove previous task " + nextTaskName + " from task " + taskName;
     }
 }