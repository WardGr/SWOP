package Domain.Command;

 import Application.*;
 import Domain.*;

 public class CreateProjectCommand implements Command {

     final TaskManSystem taskManSystem;
     final String projectName;
     final String projectDescription;
     final Time dueTime;

     public CreateProjectCommand(
        TaskManSystem taskManSystem,
         String projectName,
         String projectDescription,
         Time dueTime) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.projectDescription = projectDescription;
         this.dueTime = dueTime;
     }

     @Override
     public void execute() throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
         taskManSystem.createProject(projectName, projectDescription, dueTime);
     }

     @Override
     public void undo() throws ProjectNotFoundException {
         taskManSystem.deleteProject(projectName);
     }

     @Override
     public String information() {
         return "Create project " + projectName;
     }

     @Override
     public boolean undoPossible(){
         return true;
     }

 }