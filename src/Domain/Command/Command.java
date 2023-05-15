package Domain.Command;

 import Domain.*;
 import Domain.TaskStates.IllegalTaskRolesException;
 import Domain.TaskStates.LoopDependencyGraphException;

public interface Command extends CommandData {

     void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, TaskNameAlreadyInUseException, IllegalTaskRolesException, ProjectNotOngoingException, LoopDependencyGraphException;

     void undo() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, UndoNotPossibleException;

 }