package Domain.Command;

 import Application.IncorrectPermissionException;
 import Application.SystemControllers.InvalidFileException;
 import Domain.DataClasses.EndTimeBeforeStartTimeException;
 import Domain.Project.ProjectNameAlreadyInUseException;
 import Domain.Project.ProjectNotOngoingException;
 import Domain.Project.TaskNotFoundException;
 import Domain.Task.*;
 import Domain.Task.IncorrectTaskStatusException;
 import Domain.Task.LoopDependencyGraphException;
 import Domain.TaskManSystem.DueBeforeSystemTimeException;
 import Domain.TaskManSystem.NewTimeBeforeSystemTimeException;
 import Domain.TaskManSystem.ProjectNotFoundException;
 import Domain.User.IncorrectUserException;
 import Domain.User.UserAlreadyAssignedToTaskException;

/**
 * Command interface, used to execute commands and undo them. Based on the Command design pattern.
 */
public interface Command extends CommandData {

    /**
     * Executes the command, changing the state of the system accordingly.
     *
     * @throws ProjectNotFoundException             if a project with the given name does not exist
     * @throws TaskNotFoundException                if a task with the given name does not exist
     * @throws IncorrectTaskStatusException         if a task is not in the correct status to execute the command
     * @throws ProjectNameAlreadyInUseException     if a project with the given name already exists
     * @throws DueBeforeSystemTimeException         if the due time of a task is before the system time
     * @throws TaskNameAlreadyInUseException        if a task with one of the given names already exists
     * @throws IllegalTaskRolesException            if one of the given roles are not valid for a task
     * @throws ProjectNotOngoingException           if one of the given projects is not in the ongoing state
     * @throws LoopDependencyGraphException         if the dependency graph of the system contains a loop
     * @throws NewTimeBeforeSystemTimeException     if the new time of the system is before the system time
     * @throws EndTimeBeforeStartTimeException      if the end time of a task is before the start time
     * @throws IncorrectUserException               if one of the given users does not exist
     * @throws UserAlreadyAssignedToTaskException   if one of the given users is already assigned to the task
     * @throws IncorrectRoleException               if one of the given users does not have its corresponding role
     */
     void execute() throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException, TaskNameAlreadyInUseException, IllegalTaskRolesException, ProjectNotOngoingException, LoopDependencyGraphException, NewTimeBeforeSystemTimeException, EndTimeBeforeStartTimeException, IncorrectUserException, UserAlreadyAssignedToTaskException, IncorrectRoleException, InvalidFileException, IncorrectPermissionException;
    /**
     * Undoes the command, changing the state of the system back to the state before the command was executed.
     */
     default void undo() throws UndoNotPossibleException {
         throw new UndoNotPossibleException();
     }

 }