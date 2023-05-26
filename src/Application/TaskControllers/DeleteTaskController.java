package Application.TaskControllers;

import Application.IncorrectPermissionException;
import Application.Session.SessionProxy;
import Application.Command.CommandInterface;
import Application.Command.TaskCommands.CreateTaskCommand;
import Application.Command.TaskCommands.DeleteTaskCommand;
import Application.Command.TaskCommands.ReplaceTaskCommand;
import Domain.DataClasses.InvalidTimeException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.Project.ProjectData;
import Domain.Project.ProjectNotOngoingException;
import Domain.Project.TaskNotFoundException;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.Task.Status;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.TaskManSystem.TaskManSystemData;
import Domain.User.Role;

import java.util.List;
import java.util.Set;

/**
 * Separates domain from UI for the createtask use-case
 */
public class DeleteTaskController {

    private final SessionProxy session;
    private final TaskManSystem taskManSystem;
    private final CommandInterface commandManager;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system object to set as current system
     * @param commandManager    The object that manages the commands in the system
     */
    public DeleteTaskController(
            SessionProxy session,
            TaskManSystem taskManSystem,
            CommandInterface commandManager
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.commandManager = commandManager;
    }

    /**
     * @return  The session data object with currently logged-in user
     */
    private SessionProxy getSession() {
        return session;
    }

    /**
     * @return  The object containing the current taskmanager system
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    /**
     * @return  The object containing the current command manager
     */
    private CommandInterface getCommandManager() {
        return commandManager;
    }

    /**
     * @return whether the preconditions for the createtask use-case are met
     */
    public boolean taskPreconditions() {
        return getSession().getRoles().contains(Role.PROJECTMANAGER);
    }


    /**
     * Returns whether the given task needs confirmation to be deleted according to how we defined it
     *
     * @param projectName       Name of project the task is in
     * @param taskName          Name of task to be deleted
     * @return  Whether the task needs confirmation to be deleted
     * @throws ProjectNotFoundException  if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException     if the given task name does not correspond to an existing task within the given project
     */
    public boolean needDeleteConfirmation(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException {
        TaskData taskData = getTaskData(projectName, taskName);
        return taskData.getStatus() == Status.PENDING || taskData.getStatus() == Status.EXECUTING;
    }

    /**
     * Creates the delete task command and executes it, handing it to the command manager as an executed command
     *
     * @param projectName   Name of project the task is in
     * @param taskName      Name of task to be deleted
     * @param confirmation  Whether the user confirmed the deletion of the task
     * @throws IncorrectPermissionException     if the user is not logged in with the project manager role
     * @throws ProjectNotFoundException         if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException            if the given task name does not correspond to an existing task within the given project
     * @throws UnconfirmedActionException       if the user did not confirm the deletion of the task
     */
    public void deleteTask(String projectName, String taskName, boolean confirmation) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, UnconfirmedActionException {
        if (!taskPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        } else if (needDeleteConfirmation(projectName, taskName) && !confirmation){
            throw new UnconfirmedActionException("Deleting a Pending or Executing task is not confirmed.");
        }
        DeleteTaskCommand deleteTaskCommand = new DeleteTaskCommand(getTaskManSystem(), projectName, taskName);
        deleteTaskCommand.execute();
        getCommandManager().addExecutedCommand(deleteTaskCommand, getSession().getCurrentUser());
    }

    /**
     * @return  A read-only data object containing information about the current task manager system
     */
    public TaskManSystemData getTaskManSystemData() {
        return getTaskManSystem().getTaskManSystemData();
    }

    /**
     * Gets a read-only data object containing information about the project corresponding to the given project name
     *
     * @param projectName                   Name of the project to get the data from
     * @return                              Read-only ProjectData object containing specific information about the project
     * @throws ProjectNotFoundException     If projectName does not correspond to an existing project in the current system
     */
    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException {
        return getTaskManSystem().getProjectData(projectName);
    }

    /**
     * Gets a read-only data object containing information about the task corresponding to the given task name, in the
     * project of the given project name
     *
     * @param projectName   Name of the project that contains this task
     * @param taskName      Name of the task to get the data object from
     * @return              Read-only data object containing specific information about the task
     * @throws ProjectNotFoundException If projectName does not correspond to an existing project
     * @throws TaskNotFoundException    If taskName does not correspond to an existing task within the given project
     */
    public TaskData getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException {
        return getTaskManSystem().getTaskData(projectName, taskName);
    }
}
