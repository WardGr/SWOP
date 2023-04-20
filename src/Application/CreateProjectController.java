package Application;

import Domain.*;

/**
 * Separates domain from UI for the createproject use-case
 */
public class CreateProjectController {

    private final SessionProxy session;
    private final TaskManSystem taskManSystem;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system object to set as current system
     */
    public CreateProjectController(
            SessionProxy session,
            TaskManSystem taskManSystem
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    /**
     * @return  The session data object with currently logged-in user
     */
    private SessionProxy getSession() {
        return session;
    }


    /**
     *
     * @return  The object containing the current taskmanager system
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    /**
     * Checks the preconditions for the createProject use case
     *
     * @return whether the preconditions are satisfied
     */
    public boolean createProjectPreconditions() {
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
        if (!createProjectPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        getTaskManSystem().createProject(projectName, projectDescription, dueTime);
    }
}
