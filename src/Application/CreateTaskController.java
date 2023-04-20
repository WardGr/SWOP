package Application;

import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.TaskData;

import java.util.List;
import java.util.Set;

/**
 * Separates domain from UI for the createtask use-case
 */
public class CreateTaskController {

    private final SessionProxy session;
    private final TaskManSystem taskManSystem;

    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system object to set as current system
     */
    public CreateTaskController(
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
     * @return  The object containing the current taskmanager system
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    /**
     * @return whether the preconditions for the createtask use-case are met
     */
    public boolean createTaskPreconditions() {
        return getSession().getRoles().contains(Role.PROJECTMANAGER);
    }

    /**
     * Creates a task attached to the given project, with task name, description, duration, acceptable deviation,
     * tasks to be completed before this task, tasks this task should be completed before and roles that this task needs
     * to start executing
     *
     * @param projectName    Name of project the task will be added to
     * @param taskName       Name of new task
     * @param description    Description of new task
     * @param durationTime   Time this task is estimated to take
     * @param deviation      Acceptable deviation from the given duration, given by the user as a percentage
     * @param roles          List of roles this task needs for it to execute
     * @param nextTasks      List of names of tasks that this task should be completed before
     * @param prevTasks      List of names of tasks that should be completed before this one
     * @throws ProjectNotFoundException      If the given project name does not correspond to an existing project
     * @throws ProjectNotOngoingException    If the project corresponding to the given project name is not ongoing
     * @throws TaskNotFoundException         If one of the given next or previous task names does not correspond to an existing task within the given project
     * @throws IncorrectTaskStatusException  If one of the given next tasks is not AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException  If adding this task would create a loop in the dependency graph of this project
     * @throws IllegalTaskRolesException     If the list of roles is empty and/or contains a non-developer role
     * @throws TaskNameAlreadyInUseException If the given task name is already in use within the given project
     * @throws IncorrectPermissionException  If the user is not logged in as project manager
     */
    public void createTask(
            String projectName,
            String taskName,
            String description,
            Time durationTime,
            double deviation,
            List<Role> roles,
            Set<String> prevTasks,
            Set<String> nextTasks
    ) throws ProjectNotFoundException, InvalidTimeException, TaskNameAlreadyInUseException, IncorrectPermissionException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, ProjectNotOngoingException {
        if (!createTaskPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        getTaskManSystem().addTaskToProject(
                projectName,
                taskName,
                description,
                durationTime,
                deviation,
                roles,
                prevTasks,
                nextTasks
        );
    }

    /**
     * Creates a new task with the given task information, replacing a failed task
     *
     * @param projectName    Project name corresponding to the project to which both tasks belong, given by the user
     * @param taskName       Task name of the replacement task, given by the user
     * @param description    Task description of the replacement task, given by the user
     * @param durationTime   Duration the user gave for the task
     * @param deviation      Acceptable deviation from the given duration, given by the user
     * @param replaces       Task name of the task that the new task would replace, given by the user
     * @throws TaskNotFoundException          If the given task name of the task to replace does not correspond to an existing task
     * @throws ProjectNotFoundException       If the given project name does not correspond to an existing project
     * @throws TaskNameAlreadyInUseException  If the given task name is already in use as a task name within the given project
     * @throws IncorrectPermissionException   If the user is not logged in as project manager
     * @throws IncorrectTaskStatusException   If the task to replace has not failed yet
     */
    public void replaceTask(
            String projectName,
            String taskName,
            String description,
            Time durationTime,
            double deviation,
            String replaces
    ) throws IncorrectPermissionException, ProjectNotFoundException, InvalidTimeException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        if (!createTaskPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        getTaskManSystem().replaceTaskInProject(
                projectName,
                taskName,
                description,
                durationTime,
                deviation,
                replaces
        );
    }

    /**
     * @return  A read-only data object containing information about the current task manager system
     */
    public TaskManSystemData getTaskManSystemData() {
        return getTaskManSystem().getTaskManSystemData();
    }

    /**
     * Gets a read-only data object containing information about the project corresponding to the given project name
     *
     * @param projectName                   Name of the project to get the data from
     * @return                              Read-only ProjectData object containing specific information about the project
     * @throws ProjectNotFoundException     If projectName does not correspond to an existing project in the current system
     */
    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException {
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
    public TaskData getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException {
        return getTaskManSystem().getTaskData(projectName, taskName);
    }
}
