package Domain.Command;

 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemoveNextTaskCommand implements Command {
     private final TaskManSystem taskManSystem;
     private final String projectName;
     private final String taskName;
     private final String nextProjectName;
     private final String nextTaskName;

     public RemoveNextTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, String nextProjectName, String nextTaskName) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextProjectName = nextProjectName;
         this.nextTaskName = nextTaskName;
     }

     @Override
     public void execute() throws ProjectNotFoundException, TaskNotFoundException {
         taskManSystem.removeNextTaskFromProject(projectName, taskName, nextProjectName, nextTaskName);
     }

     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
         taskManSystem.addNextTaskToProject(projectName, taskName, nextProjectName, nextTaskName);
     }

     @Override
     public String information() {
         return "Remove next task " + nextTaskName + " from task " + taskName;
     }
 }