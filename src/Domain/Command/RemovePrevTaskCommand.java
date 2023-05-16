package Domain.Command;

 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class RemovePrevTaskCommand implements Command {
     private final TaskManSystem taskManSystem;
     private final String projectName;
     private final String taskName;
     private final String prevProjectName;
     private final String prevTaskName;

     public RemovePrevTaskCommand(TaskManSystem taskManSystem, String projectName, String taskName, String prevProjectName, String prevTaskName) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.prevProjectName = prevProjectName;
         this.prevTaskName = prevTaskName;
     }

     @Override
     public void execute() throws ProjectNotFoundException, TaskNotFoundException {
         taskManSystem.removePrevTaskFromProject(projectName, taskName, prevProjectName, prevTaskName);
     }

     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
         taskManSystem.addPrevTaskToProject(projectName, taskName, prevProjectName, prevTaskName);
     }

     @Override
     public String information() {
         return "Remove previous task " + prevTaskName + " from task " + taskName;
     }
 }