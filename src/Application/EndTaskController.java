package Application;

import Domain.*;
import Domain.TaskStates.TaskProxy;

import java.util.Set;

public class EndTaskController {
    private final SessionWrapper session;
    private final TaskManSystem taskManSystem;

    public EndTaskController(
            SessionWrapper session,
            TaskManSystem taskManSystem
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    private SessionWrapper getSession() {
        return session;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    public boolean endTaskPreconditions() {
        return (getSession().getRoles().contains(Role.PYTHONPROGRAMMER) ||
                getSession().getRoles().contains(Role.JAVAPROGRAMMER) ||
                getSession().getRoles().contains(Role.SYSADMIN));
    }

    public TaskProxy getUserTaskData() {
        return getSession().getCurrentUser().getTaskData();
    }

    public void finishCurrentTask() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        TaskProxy userTaskData = getUserTaskData();
        if (userTaskData == null || userTaskData.getStatus() != Status.EXECUTING) {
            throw new IncorrectPermissionException("You are not executing a task");
        }
        getTaskManSystem().finishTask(userTaskData.getProjectName(), userTaskData.getName(), getSession().getCurrentUser());
    }

    public void failCurrentTask() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        TaskProxy userTaskData = getUserTaskData();
        if (userTaskData == null || userTaskData.getStatus() != Status.EXECUTING) {
            throw new IncorrectPermissionException("You are not executing a task");
        }
        getTaskManSystem().failTask(userTaskData.getProjectName(), userTaskData.getName(), getSession().getCurrentUser());
    }

    public TaskManSystemProxy getTaskManSystemData() throws IncorrectPermissionException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getProjectData(projectName);
    }

    public TaskProxy getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        if (!endTaskPreconditions()) {
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

    public Set<Role> getUserRoles() {
        return getSession().getRoles();
    }
}
