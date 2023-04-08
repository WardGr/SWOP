package Application;

import Domain.*;
import Domain.TaskStates.TaskProxy;
import Domain.TaskStates.UserAlreadyExecutingTaskException;

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

    private SessionWrapper getSession(){
        return session;
    }

    private TaskManSystem getTaskManSystem(){
        return taskManSystem;
    }

    public boolean endTaskPreconditions(){
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

    public void finishCurrentTask() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException {
        if (!endTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        TaskProxy executingTaskData = getUserExecutingTaskData();
        if (executingTaskData == null){
            throw new RuntimeException(); // TODO
        }
        getTaskManSystem().finishTask(executingTaskData.getProjectName(), executingTaskData.getName(), getSession().getCurrentUser());
    }

    public void failCurrentTask() throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException {
        if (!endTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        TaskProxy executingTaskData = getUserExecutingTaskData();
        if (executingTaskData == null){
            throw new RuntimeException(); // TODO
        }
        getTaskManSystem().failTask(executingTaskData.getProjectName(), executingTaskData.getName(), getSession().getCurrentUser());
    }









    public TaskManSystemProxy getTaskManSystemData() throws IncorrectPermissionException {
        if (!endTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        if (!endTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getProjectData(projectName);
    }

    public TaskProxy getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        if (!endTaskPreconditions()){
            throw new IncorrectPermissionException("You need a developer role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

    public Set<Role> getUserRoles(){
        return getSession().getRoles();
    }
}
