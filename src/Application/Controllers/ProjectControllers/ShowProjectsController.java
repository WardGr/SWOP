package Application.Controllers.ProjectControllers;import Application.IncorrectPermissionException;import Application.Session.SessionProxy;import Domain.Project.ProjectData;import Domain.Project.TaskNotFoundException;import Domain.TaskManSystem.ProjectNotFoundException;import Domain.TaskManSystem.TaskManSystem;import Domain.TaskManSystem.TaskManSystemData;import Domain.Task.TaskData;import Domain.User.Role;/** * Separates domain from UI for the showprojects use-case */public class ShowProjectsController {    private final SessionProxy session;    private final TaskManSystem taskManSystem;    /**     * Creates this controller object     *     * @param session           The current session to set as active session     * @param taskManSystem     Object managing the system     */    public ShowProjectsController(SessionProxy session, TaskManSystem taskManSystem) {        this.session = session;        this.taskManSystem = taskManSystem;    }    /**     * @return  The session data object with currently logged-in user     */    private SessionProxy getSession() {        return session;    }    /**     * @return  The object containing the current taskmanager system     */    private TaskManSystem getTaskManSystem() {        return taskManSystem;    }    public boolean showProjectsPreconditions() {        return getSession().getRoles() != null && getSession().getRoles().contains(Role.PROJECTMANAGER);    }    /**     * Returns a read-only data object that contains information about the current task manager system, if the user is     * a project manager     *     * @return  A read-only data object     * @throws IncorrectPermissionException if the current user is not a project manager     */    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {        if (!showProjectsPreconditions()) {            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");        }        return getTaskManSystem().getTaskManSystemData();    }    /**     * Gets a read-only data object containing information about the project corresponding to the given project name     *     * @param projectName                   Name of the project to get the data from     * @return                              Read-only ProjectData object containing specific information about the project     * @throws ProjectNotFoundException     If projectName does not correspond to an existing project in the current system     */    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {        if (!showProjectsPreconditions()) {            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");        }        return getTaskManSystem().getProjectData(projectName);    }    /**     * Gets a read-only data object containing information about the task corresponding to the given task name, in the     * project of the given project name     *     * @param projectName   Name of the project that contains this task     * @param taskName      Name of the task to get the data object from     * @return              Read-only data object containing specific information about the task     * @throws ProjectNotFoundException If projectName does not correspond to an existing project     * @throws TaskNotFoundException    If taskName does not correspond to an existing task within the given project     */    public TaskData getTaskData(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {        if (!showProjectsPreconditions()) {            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");        }        return getTaskManSystem().getTaskData(projectName, taskName);    }}