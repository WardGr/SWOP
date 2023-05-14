package Application;

import Domain.*;
import Domain.Command.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;

public class UpdateDependenciesController {
    private final SessionWrapper session;
    private final TaskManSystem taskManSystem;
    private final CommandController cmdController;

    public UpdateDependenciesController(SessionWrapper session, TaskManSystem taskManSystem, CommandController commandController) {
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.cmdController = commandController;
    }

    private SessionWrapper getSession() {
        return session;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private CommandController getCommandController() {
        return cmdController;
    }

    public boolean updateDependenciesPreconditions() {
        return (getSession().getRoles().contains(Role.PROJECTMANAGER));
    }

    public void addPreviousTask(String projectName, String taskName, String prevTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().addPreviousTaskToProject(projectName, taskName, prevTaskName);
        Command cmd = new AddPrevCommand(this, projectName, taskName, prevTaskName);
        cmdController.addCommand(cmd);
    }

    public void addNextTask(String projectName, String taskName, String nextTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().addNextTaskToProject(projectName, taskName, nextTaskName);
        Command cmd = new AddNextCommand(this, projectName, taskName, nextTaskName);
        cmdController.addCommand(cmd);
    }

    public void removePreviousTask(String projectName, String taskName, String prevTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().removePreviousTaskFromProject(projectName, taskName, prevTaskName);
        Command cmd = new RemPrevCommand(this, projectName, taskName, prevTaskName);
        cmdController.addCommand(cmd);
    }

    public void removeNextTask(String projectName, String taskName, String nextTaskName) throws IncorrectPermissionException, ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        getTaskManSystem().removeNextTaskFromProject(projectName, taskName, nextTaskName);
        Command cmd = new RemNextCommand(this, projectName, taskName, nextTaskName);
        cmdController.addCommand(cmd);
    }

    public TaskManSystemProxy getTaskManSystemData() throws IncorrectPermissionException {
        if (!updateDependenciesPreconditions()) {
            throw new IncorrectPermissionException("You need a project manager role to call this function");
        }
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException, IncorrectPermissionException {
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
