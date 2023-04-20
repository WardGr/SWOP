package Application;

import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.TaskProxy;

import java.util.List;
import java.util.Set;

/**
 * Separates domain from UI for the createtask use-case
 */
public class CreateTaskController {

    private final SessionWrapper session;
    private final TaskManSystem taskManSystem;
    private final UserManager userManager;
    // TODO deze niet meer nodig?

    public CreateTaskController(
            SessionWrapper session,
            TaskManSystem taskManSystem,
            UserManager userManager
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.userManager = userManager;
    }


    private SessionWrapper getSession() {
        return session;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private UserManager getUserManager() {
        return userManager;
    }

    /**
     * @return whether the preconditions for the createtask use-case are met
     */
    public boolean createTaskPreconditions() {
        return getSession().getRoles().contains(Role.PROJECTMANAGER);
    }

    /**
     * Creates a task attached to the given project, with task name, description, duration, acceptable deviation, assigned
     * user and tasks to be completed before this task all given by the user
     *
     * @param projectName    Name of project the task will be added to, given by the user
     * @param taskName       Name of new task, given by the user
     * @param description    Description of new task, given by the user
     * @param durationHour   Hours of the new tasks' duration, given by the user
     * @param durationMinute Minutes of the new tasks' duration, given by the user
     * @param deviation      Acceptable deviation from the given duration, given by the user as a percentage
     * @param user           Name of the user this task is allocated to
     * @param prevTasks  List of names of tasks that should be completed before this one, given by the user
     * @throws ProjectNotFoundException      If the given project name does not correspond to an existing project
     * @throws InvalidTimeException          If durationMinute > 59 or durationMinute < 0
     * @throws TaskNotFoundException         If any of the previous tasks do not correspond to an existing task
     * @throws TaskNameAlreadyInUseException If the given task name is already in use within the given project
     * @throws IncorrectPermissionException  If the user is not logged in as project manager
     * @throws UserNotFoundException         If the user attached to the new task does not exist
     */ // lord heavens above
    public void createTask(
            String projectName,
            String taskName,
            String description,
            Time durationTime,
            double deviation,
            List<Role> roles,
            Set<String> prevTasks,
            Set<String> nextTasks
    ) throws ProjectNotFoundException, InvalidTimeException, TaskNameAlreadyInUseException, IncorrectPermissionException, UserNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, ProjectNotOngoingException {
        if (!createTaskPreconditions()) {
            throw new IncorrectPermissionException("You must be logged in with the " + Role.PROJECTMANAGER + " role to call this function");
        }
        //User developer = getUserManager().getDeveloper(user);
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
     * @throws IncorrectTaskStatusException If the task to replace has not failed yet
     * @throws InvalidTimeException           If durationMinute > 59 or durationMinute < 0
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

    public TaskManSystemProxy getTaskManSystemData() {
        return getTaskManSystem().getTaskManSystemData();
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException {
        return getTaskManSystem().getProjectData(projectName);
    }

    public TaskProxy getTaskData(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException {
        return getTaskManSystem().getTaskData(projectName, taskName);
    }
}
