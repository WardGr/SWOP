package Application;

import Domain.*;
import Domain.TaskStates.TaskProxy;
import Domain.TaskStates.UserAlreadyExecutingTaskException;

import java.util.Set;

public class StartTaskController {
    private final SessionWrapper session;
    private final TaskManSystem taskManSystem;

    public StartTaskController(
            SessionWrapper session,
            TaskManSystem taskManSystem
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    private SessionWrapper getSession(){
        return session;
    }

    private TaskManSystem getTaskManSystem(){
        return taskManSystem;
    }

    public boolean startTaskPreconditions(){
        return (getSession().getRoles().contains(Role.PYTHONPROGRAMMER) ||
            getSession().getRoles().contains(Role.JAVAPROGRAMMER) ||
            getSession().getRoles().contains(Role.SYSADMIN));
    }

    public TaskProxy getUserExecutingTaskData(){
        return getSession().getCurrentUser().getExecutingTaskData();
    }

    public TaskProxy getUserPendingTaskData(){
        return getSession().getCurrentUser().getPendingTaskData();
    }

    public void startTask(String projectName, String taskName, Role role) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyExecutingTaskException {
        if (!startTaskPreconditions()){
            throw new IncorrectPermissionException(""); // TODO
        }
        getTaskManSystem().startTask(projectName, taskName, getSession().getCurrentUser(), role);
    }







    public TaskManSystemProxy getTaskManSystemData() throws IncorrectPermissionException {
        if (!startTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        if (!startTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getProjectData(projectName);
    }

    public TaskProxy getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        if (!startTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

    public Set<Role> getUserRoles(){
        return getSession().getRoles();
    }
}
