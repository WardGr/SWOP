package Application;

import Domain.*;
import Domain.Command.StartTaskCommand;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.TaskData;
import Domain.UserAlreadyAssignedToTaskException;

import java.util.Set;

public class StartTaskController {
    private final SessionProxy session;
    private final TaskManSystem taskManSystem;
    private final CommandInterface commandManager;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     Object managing the system
     * @param commandManager    Object managing the commands
     */
    public StartTaskController(
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

    public boolean startTaskPreconditions() {
        return (getSession().getRoles().contains(Role.PYTHONPROGRAMMER) ||
                getSession().getRoles().contains(Role.JAVAPROGRAMMER) ||
                getSession().getRoles().contains(Role.SYSADMIN));
    }

    /**
     * @return A data object encapsulating the users task
     */
    public TaskData getUserTaskData() {
        return getSession().getCurrentUser().getTaskData();
    }

    /**
     * Starts the ask with the given taskname in the given project, with the given role
     *
     * @param projectName   The project to start this task in
     * @param taskName      The task to start
     * @param role          The role to start the task with
     * @throws IncorrectPermissionException         If there is no user logged in or the user does not have a developer role
     * @throws ProjectNotFoundException             If the given projectName does not correspond to an existing project in the system
     * @throws TaskNotFoundException                If the given taskName does not correspond to an existing task within the given project
     * @throws IncorrectTaskStatusException         If the given task is not EXECUTING
     * @throws IncorrectRoleException               If the given role is not necessary for the given task or if the current user does not have this role
     * @throws UserAlreadyAssignedToTaskException   If the current user is already assigned to this task
     */
    public void startTask(String projectName, String taskName, Role role, boolean confirmation) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        if (!startTaskPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with a developer role to start a task");
        }
        if (startTaskNeedsConfirmation() && !confirmation){
            throw new IncorrectPermissionException("Starting of the task needs confirmation and isn't confirmed");
        }
        StartTaskCommand cmd = new StartTaskCommand(getTaskManSystem(), projectName, taskName, getSession().getCurrentUser(), role);
        cmd.execute();
        getCommandManager().addExecutedCommand(cmd, getSession().getCurrentUser());
    }

    public boolean startTaskNeedsConfirmation(){
        return getUserTaskData() != null && getUserTaskData().getStatus() == Status.PENDING;
    }


    /**
     * Returns a read-only data object that contains information about the current task manager system, if the user is
     * a developer
     *
     * @return                                   A read-only data object containing information about the current task manager system
     * @throws IncorrectPermissionException      if the current user is not logged in with a developer role
     */
    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {
        if (!startTaskPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with a developer role to call this function");
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
        if (!startTaskPreconditions()) {
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
        if (!startTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

    /**
     * @return A set of roles with the currently logged-in users' roles
     */
    public Set<Role> getUserRoles() {
        return getSession().getRoles();
    }
}
