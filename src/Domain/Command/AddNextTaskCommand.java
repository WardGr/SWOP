package Domain.Command;

 import Application.*;
 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
import Domain.TaskManSystem;
import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;

 public class AddNextTaskCommand implements Command {
     private final TaskManSystem taskmansystem;
     private final String projectName;
     private final String taskName;
     private final String nextProjectName;
     private final String nextTaskName;

     public AddNextTaskCommand(TaskManSystem taskmansystem, String projectName, String taskName, String nextProjectName, String nextTaskName) {
         this.taskmansystem = taskmansystem;
         this.projectName = projectName;
         this.taskName = taskName;
         this.nextProjectName = nextProjectName;
         this.nextTaskName = nextTaskName;
     }
     @Override
     public void undo() throws ProjectNotFoundException, TaskNotFoundException {
         taskmansystem.removeNextTaskFromProject(projectName, taskName, nextProjectName, nextTaskName);

     }

     @Override
     public void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
         taskmansystem.addNextTaskToProject(projectName, taskName, nextProjectName, nextTaskName);
     }

     @Override
     public String information() {
         return "Add next task " + nextTaskName + " to task " + taskName;
     }
 }