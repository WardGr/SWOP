package Application;

import Domain.*;
import Domain.TaskStates.TaskProxy;

public class UpdateDependenciesController {
    private final SessionWrapper session;
    private final TaskManSystem taskManSystem;

    public UpdateDependenciesController(SessionWrapper session, TaskManSystem taskManSystem){
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    private SessionWrapper getSession(){
        return session;
    }

    private TaskManSystem getTaskManSystem(){
        return taskManSystem;
    }

    public boolean updateDependenciesPreconditions(){
        return (getSession().getRoles().contains(Role.PROJECTMANAGER));
    }



    public TaskManSystemProxy getTaskManSystemData() throws IncorrectPermissionException {
        if (!updateDependenciesPreconditions()){
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
        if (!updateDependenciesPreconditions()){
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getProjectData(projectName);
    }

    public TaskProxy getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException, IncorrectPermissionException {
        if (!updateDependenciesPreconditions()){
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getTaskData(projectName, taskName);
    }

}
