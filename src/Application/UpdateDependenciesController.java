package Application;

import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;

public class UpdateDependenciesController {
    private final SessionProxy session;
    private final TaskManSystem taskManSystem;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     Object managing the system
     */
    public UpdateDependenciesController(SessionProxy session, TaskManSystem taskManSystem) {
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
     * @return  The object containing the current taskmanager system
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    /**
     * @return true if the currently logged-in user is a project manager, false otherwise
     */
    public boolean updateDependenciesPreconditions() {
        return (getSession().getRoles().contains(Role.PROJECTMANAGER));
    }

    /**
     * Adds the task corresponding to prevTaskName to the given task in the given project
     *
     * @param projectName   The project to add the previous task to
     * @param taskName      The task to which to add the previous task
     * @param prevTaskName  The task to add as previous task to the task corresponding to taskName
     * @throws IncorrectPermissionException If the currently logged-in user is not a project manager
     * @throws ProjectNotFoundException     If the given projectName does not correspond to an existing project within the system
     * @throws TaskNotFoundException        If the given taskName or prevTaskName does not correspond to an existing task within the given project
     * @throws IncorrectTaskStatusException If the task corresponding to taskName is not AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException If adding the previous task would introduce a loop in the dependency graph
     * @post if taskName is available, then sets this task to unavailable
     */
    public void addPrevTask(String projectName, String taskName, String prevTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().addPrevTaskToProject(projectName, taskName, prevTaskName);
    }

    /**
     * Adds the task corresponding to nextTaskName as a previous task to the given task in the given project
     *
     * @param projectName       The project to add the next task to
     * @param taskName          The task to which to add the next task
     * @param nextTaskName      The task to add as next task to the task corresponding to taskName
     * @throws IncorrectPermissionException If the currently logged-in user does not have the project manager role
     * @throws ProjectNotFoundException     If given projectName does not correspond to an existing project within the system
     * @throws TaskNotFoundException        If the given taskName or nextTaskName does not correspond to an existing task within the given project
     * @throws IncorrectTaskStatusException If the task corresponding to nextTaskName is not AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException If adding this next task introduces a loop in the dependency graph
     * @post if nextTask is available, then sets this tasks' status to UNAVAILABLE
     */
    public void addNextTask(String projectName, String taskName, String nextTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().addNextTaskToProject(projectName, taskName, nextTaskName);
    }

    /**
     * Removes the task corresponding to prevTaskName from the given tasks' previous tasks list
     *
     * @param projectName       The project to remove the previous task from
     * @param taskName          The task from which to remove the previous task
     * @param prevTaskName      The task to remove as previous task from the given task
     * @throws IncorrectPermissionException If the currently logged-in user does not have a project manager role
     * @throws ProjectNotFoundException     If the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException        If the given taskName does not correspond to an existing task within the given project
     * @throws IncorrectTaskStatusException if the task corresponding to taskName is not available or unavailable
     * @post if prevTaskName is the last previous task in taskName, then sets the status of taskName to AVAILABLE
     */
    public void removePrevTask(String projectName, String taskName, String prevTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().removePrevTaskFromProject(projectName, taskName, prevTaskName);
    }

    /**
     * Removes a task from the nextTasks list of a given task in a given project, respecting the rules of the dependency graph
     *
     * @param taskName     Name of the task to remove the next task from
     * @param projectName  Name of the project to add the next task to
     * @param nextTaskName Name of the next task to add
     * @throws TaskNotFoundException        if taskName or nextTaskName do not correspond to an existing task within this project
     * @throws IncorrectTaskStatusException if the task corresponding to taskName is not available or unavailable
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project
     * @throws IncorrectPermissionException if the currently logged-in user does not have a project manager role
     * @post if nextTaskName is AVAILABLE then sets nextTaskName to UNAVAILABLE
     */
    public void removeNextTask(String projectName, String taskName, String nextTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().removeNextTaskFromProject(projectName, taskName, nextTaskName);
    }

    /**
     * Returns a read-only data object that contains information about the current task manager system, if the user is
     * a project manager
     *
     * @return                                   A read-only data object containing information about the current task manager system
     * @throws IncorrectPermissionException      if the current user is not logged in with a developer role
     */
    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
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
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
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
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

}
