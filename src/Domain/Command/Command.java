package Domain.Command;

 import Domain.IncorrectTaskStatusException;
 import Domain.ProjectNotFoundException;
 import Domain.TaskNotFoundException;
 import Domain.TaskStates.LoopDependencyGraphException;
 import Domain.User;

 public interface Command extends CommandData {

     void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException;

     void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException;

 }