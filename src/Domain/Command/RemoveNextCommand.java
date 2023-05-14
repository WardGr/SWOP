package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemoveNextCommand implements Command {
     private final TaskManSystem taskManSystem;
     private final String projectName;
     private final String taskName;
     private final String nextTaskName;

     public RemoveNextCommand(TaskManSystem taskManSystem, String projectName, String taskName, String nextTaskName) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextTaskName = nextTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
         taskManSystem.addNextTaskToProject(projectName, taskName, nextTaskName);
     }

     @Override
     public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException {
         taskManSystem.removeNextTaskFromProject(projectName, taskName, nextTaskName);
     }

     @Override
     public String information() {
         return "Remove next task " + nextTaskName + " from task " + taskName;
     }
 }