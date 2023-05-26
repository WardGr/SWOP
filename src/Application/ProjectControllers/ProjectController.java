package Application.ProjectControllers;

import Application.IncorrectPermissionException;
import Application.Session.SessionProxy;
import Application.Command.CommandInterface;
import Application.Command.ProjectCommands.CreateProjectCommand;
import Application.Command.ProjectCommands.DeleteProjectCommand;
import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
import Domain.Project.ProjectNameAlreadyInUseException;
import Domain.TaskManSystem.DueBeforeSystemTimeException;
import Domain.TaskManSystem.ProjectNotFoundException;
import Domain.TaskManSystem.TaskManSystem;
import Domain.TaskManSystem.TaskManSystemData;
import Domain.User.Role;

/**
 * Separates domain from UI for the createproject use-case
 */
public class ProjectController {

    private final SessionProxy session;
    private final TaskManSystem taskManSystem;
    private final CommandInterface commandManager;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system object to set as current system
     */
    public ProjectController(
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
     * Checks the preconditions for the createProject use case
     *
     * @return whether the preconditions are satisfied
     */
    public boolean projectPreconditions() {
        return getSession().getRoles().contains(Role.PROJECTMANAGER);
    }

    /**
     * Creates the project with given name, description and due time
     *
     * @param projectName        project name given by the user
     * @param projectDescription project description given by the user
     * @param dueTime            due time given by the user
     * @throws IncorrectPermissionException     if the user is not logged in as a project manager
     * @throws ProjectNameAlreadyInUseException if the given project name is already in use
     * @throws DueBeforeSystemTimeException     if the due time is before the system time (the project should have been completed already)
     */
    public void createProject(String projectName, String projectDescription, Time dueTime) throws IncorrectPermissionException, ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        if (!projectPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        CreateProjectCommand createProjectCommand = new CreateProjectCommand(getTaskManSystem(), projectName, projectDescription, dueTime);
        createProjectCommand.execute();
        getCommandManager().addExecutedCommand(createProjectCommand, getSession().getCurrentUser());
    }

    public void deleteProject(String projectName) throws IncorrectPermissionException, ProjectNotFoundException {
        if (!projectPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        DeleteProjectCommand deleteProjectCommand = new DeleteProjectCommand(taskManSystem, projectName);
        deleteProjectCommand.execute();
        getCommandManager().addExecutedCommand(deleteProjectCommand, getSession().getCurrentUser());
    }


    /**
     * Returns a read-only data object that contains information about the current task manager system, if the user is
     * a project manager
     *
     * @return  A read-only data object
     * @throws IncorrectPermissionException if the current user is not a project manager
     */
    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {
        if (!projectPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
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
        if (!projectPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        return getTaskManSystem().getProjectData(projectName);
    }
}
