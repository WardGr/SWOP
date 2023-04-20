package Application;

import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;

public class UpdateDependenciesController {
    private final SessionProxy session;
    private final TaskManSystem taskManSystem;

    public UpdateDependenciesController(SessionProxy session, TaskManSystem taskManSystem) {
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    private SessionProxy getSession() {
        return session;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    public boolean updateDependenciesPreconditions() {
        return (getSession().getRoles().contains(Role.PROJECTMANAGER));
    }

    public void addPrevTask(String projectName, String taskName, String prevTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().addprevTaskToProject(projectName, taskName, prevTaskName);
    }

    public void addNextTask(String projectName, String taskName, String nextTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().addNextTaskToProject(projectName, taskName, nextTaskName);
    }

    public void removePrevTask(String projectName, String taskName, String prevTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().removeprevTaskFromProject(projectName, taskName, prevTaskName);
    }

    public void removeNextTask(String projectName, String taskName, String nextTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().removeNextTaskFromProject(projectName, taskName, nextTaskName);
    }

    public TaskManSystemData getTaskManSystemData() throws IncorrectPermissionException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getProjectData(projectName);
    }

    public TaskData getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

}
