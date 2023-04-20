package Application;import Domain.*;import Domain.TaskStates.TaskData;/** * Separates domain from UI for the showprojects use-case */public class ShowProjectsController {    private final SessionProxy session;    private final TaskManSystem taskManSystem;    public ShowProjectsController(SessionProxy session, TaskManSystem taskManSystem) {        this.session = session;        this.taskManSystem = taskManSystem;    }    private SessionProxy getSession() {        return session;    }    private TaskManSystem getTaskManSystem() {        return taskManSystem;    }    public boolean showProjectsPreconditions() {        return getSession().getRoles() != null && getSession().getRoles().contains(Role.PROJECTMANAGER);    }    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {        if (!showProjectsPreconditions()) {            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");        }        return getTaskManSystem().getTaskManSystemData();    }    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {        if (!showProjectsPreconditions()) {            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");        }        return getTaskManSystem().getProjectData(projectName);    }    public TaskData getTaskData(String projectName, String taskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException {        if (!showProjectsPreconditions()) {            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");        }        return getTaskManSystem().getTaskData(projectName, taskName);    }}