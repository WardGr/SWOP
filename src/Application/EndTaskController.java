package Application;

import Domain.Command.CommandInterface;
import Domain.Command.TaskCommands.FailTaskCommand;
import Domain.Command.TaskCommands.FinishTaskCommand;
import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.Project.ProjectData;
import Domain.Project.TaskNotFoundException;
import Domain.Task.IncorrectTaskStatusException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.TaskManSystem.TaskManSystemData;
import Domain.Task.Status;
import Domain.Task.TaskData;
import Domain.User.IncorrectUserException;
import Domain.User.Role;

import java.util.Set;

/**
 * Separates domain from UI for the endtask use-case
 */
public class EndTaskController {
    private final SessionProxy session;
    private final TaskManSystem taskManSystem;
    private final CommandInterface commandManager;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system object to set as current system
     */
    public EndTaskController(
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

    public boolean endTaskPreconditions() {
        return (getSession().getRoles().contains(Role.PYTHONPROGRAMMER) ||
                getSession().getRoles().contains(Role.JAVAPROGRAMMER) ||
                getSession().getRoles().contains(Role.SYSADMIN));
    }

    /**
     * @return A read-only data object containing information about the currently logged-in users' task
     */
    public TaskData getUserTaskData() {
        return getSession().getCurrentUser().getTaskData();
    }

    /**
     * Finishes the task assigned to the currently logged-in user, setting its status to FINISHED
     *
     * @throws IncorrectPermissionException         If the currently logged-in user is not a developer
     * @throws ProjectNotFoundException             If the task currently assigned to the logged-in user does not have a valid project name
     * @throws TaskNotFoundException                If the task currently assigned to the logged-in user does not have a valid task name
     * @throws IncorrectTaskStatusException         If the task currently assigned to the logged-in user is not EXECUTING
     * @throws IncorrectUserException               If the user is not properly assigned to this task
     * @throws EndTimeBeforeStartTimeException      If the current system time is somehow before the start time of this task
     */
    public void finishCurrentTask() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        TaskData userTaskData = getUserTaskData();
        if (userTaskData == null || userTaskData.getStatus() != Status.EXECUTING) {
            throw new IncorrectPermissionException("You are not executing a task");
        }
        FinishTaskCommand cmd = new FinishTaskCommand(getTaskManSystem(), userTaskData.getProjectName(), userTaskData.getName(), getSession().getCurrentUser());
        cmd.execute();
        getCommandManager().addExecutedCommand(cmd, getSession().getCurrentUser());
    }

    /**
     * Fails the task assigned to the currently logged-in user, setting its status to FAILED
     *
     * @throws IncorrectPermissionException         If the currently logged-in user is not a developer
     * @throws ProjectNotFoundException             If the task currently assigned to the logged-in user does not have a valid project name
     * @throws TaskNotFoundException                If the task currently assigned to the logged-in user does not have a valid task name
     * @throws IncorrectTaskStatusException         If the task currently assigned to the logged-in user is not EXECUTING
     * @throws IncorrectUserException               If the user is not properly assigned to this task
     * @throws EndTimeBeforeStartTimeException      If the current system time is somehow before the start time of this task
     */
    public void failCurrentTask() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        TaskData userTaskData = getUserTaskData();
        if (userTaskData == null || userTaskData.getStatus() != Status.EXECUTING) {
            throw new IncorrectPermissionException("You are not executing a task");
        }
        FailTaskCommand cmd = new FailTaskCommand(getTaskManSystem(), userTaskData.getProjectName(), userTaskData.getName(), getSession().getCurrentUser());
        cmd.execute();
        getCommandManager().addExecutedCommand(cmd, getSession().getCurrentUser());
    }

    /**
     *
     *
     * @return  A read-only data object containing information about the current task manager system
     * @throws  IncorrectPermissionException    if the user does not have a developer role
     */
    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    /**
     * Gets a read-only data object containing information about the project corresponding to the given project name
     *
     * @param projectName                   Name of the project to get the data from
     * @return                              Read-only ProjectData object containing specific information about the project
     * @throws ProjectNotFoundException     If projectName does not correspond to an existing project in the current system
     */
    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
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
    public TaskData getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

    /**
     * @return Set of roles the currently logged-in user has
     */
    public Set<Role> getUserRoles() {
        return getSession().getRoles();
    }
}
