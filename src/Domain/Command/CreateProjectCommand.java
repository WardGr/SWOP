package Domain.Command;

 import Application.*;
 import Domain.*;

 public class CreateProjectCommand extends Command {

     final TaskManSystem taskManSystem;
     final String projectName;
     final String projectDescription;
     final Time startTime;
     final Time dueTime;

     public CreateProjectCommand(
        TaskManSystem taskManSystem,
         String projectName,
         String projectDescription,
         Time startTime,
         Time dueTime) {
         this.taskManSystem = taskManSystem;
         this.projectName = projectName;
         this.projectDescription = projectDescription;
         this.startTime = startTime;
         this.dueTime = dueTime;
     }

     @Override
     public User getUser() {
         // TODO Auto-generated method stub
         throw new UnsupportedOperationException("Unimplemented method 'getUser'");
     }

     @Override
     public void undo() throws Exception {
         try {
             taskManSystem.deleteProject(projectName);
         } catch (Exception e) {
             throw new Exception(e);
         }
     }

     @Override
     public void redo() throws Exception {
         try {
             taskManSystem.createProject(projectName, projectDescription, dueTime);
         } catch (Exception e) {
             throw new Exception(e);
         } 
     }

     @Override
     public String information() {
         return "Create project " + projectName;
     }

 }