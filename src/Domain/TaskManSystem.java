package Domain;

import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;
import Domain.TaskStates.TaskData;

import java.util.*;

/**
 * Central domain-level system class, keeps track of system time and all projects, first point of entry into the domain
 * layer
 */
public class TaskManSystem {

    private final TaskManSystemProxy taskManSystemProxy;
    private List<Project> projects;
    private Time systemTime;

    public TaskManSystem(Time systemTime) {
        this.systemTime = systemTime;
        projects = new LinkedList<>();
        taskManSystemProxy = new TaskManSystemProxy(this);
    }

    public TaskManSystemProxy getTaskManSystemData() {
        return taskManSystemProxy;
    }

    public Time getSystemTime() {
        return systemTime;
    }

    private void setSystemTime(Time newTime) {
        this.systemTime = newTime;
    }

    /**
     * @return IMMUTABLE list of projects
     */
    private List<Project> getProjects() {
        return List.copyOf(projects);
    }

    /**
     * Returns the project corresponding to the given project name if no such project exists then returns null
     *
     * @param projectName Name of the project
     * @return Project corresponding to the given project name, null if no such project exists
     */
    private Project getProject(String projectName) {
        for (Project project : getProjects()) {
            if (project.getName().equals(projectName)) {
                return project;
            }
        }
        return null;
    }

    /**
     * @return List of all project names
     */
    public List<String> getProjectNames() { // todo: momenteel niet gebruikt
        List<String> names = new LinkedList<>();
        for (Project project : getProjects()) {
            names.add(project.getName());
        }
        return names;
    }

    public ProjectProxy getProjectData(String projectName) throws ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.getProjectData();
    }

    public TaskData getTaskData(String projectName, String taskName) throws TaskNotFoundException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.getTaskData(taskName);
    }

    /**
     * Returns a map which maps project names to their status
     */
    public Map<String, String> getProjectNamesWithStatus() {
        Map<String, String> statuses = new HashMap<>();
        for (Project project : getProjects()) {
            statuses.put(project.getName(), project.getStatus().toString());
        }
        return statuses;
    }

    /**
     * TODO: maybe it's cleaner to just return the empty string if no such project is found? Semantically this is logical and it removes an exception to be caught...
     * Returns detailed information about the given project
     *
     * @param projectName Name of the project of which to return the details
     * @return string containing detailed information about the project  + a list of its tasks
     * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
     */
    public String showProject(String projectName)
            throws ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.toString();
    }

    /**
     * TODO: maybe it's cleaner to just return the empty string if no such task is found? Semantically this is logical and it removes an exception to be caught...
     * Returns detailed information about the given task
     *
     * @param projectName Name of the project which the task belongs to
     * @param taskName    Name of the task of which to return detailed information
     * @return string containing detailed information about the task
     * @throws ProjectNotFoundException if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException    if the given task does not correspond to an existing task within the given project
     */
    public String showTask(String projectName, String taskName)
            throws ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.showTask(taskName);
    }

    private void addProject(Project newProject) {
        projects.add(newProject);
    }

    /**
     * Creates a project with given name, description and due time, using the system time as start time
     *
     * @param projectName        Name of the project to create
     * @param projectDescription Description of the project to create
     * @param dueTime            Time at which the project is due
     * @throws DueBeforeSystemTimeException     if the given due time is before system time
     * @throws ProjectNameAlreadyInUseException if the given project name is already in use
     */
    public void createProject(String projectName, String projectDescription, Time dueTime) throws ProjectNameAlreadyInUseException, DueBeforeSystemTimeException {
        createProject(projectName, projectDescription, getSystemTime(), dueTime);
    }

    public void deleteProject(String projectName) throws IllegalArgumentException {
        Project project = getProject(projectName);
        if (project != null) {
            throw new IllegalArgumentException();
        }
        deleteProject(project);
    }

    private void deleteProject(Project project) {
        projects.remove(project);
    }

    /**
     * Creates a project with given name, description and due time, using the given time as start time
     *
     * @param projectName        Name of the project to create
     * @param projectDescription Description of the project to create
     * @param startTime          Time at which the project is to start
     * @param dueTime            Time at which the project is due
     * @throws DueBeforeSystemTimeException     if the given due time is before system time
     * @throws ProjectNameAlreadyInUseException if the given project name is already in use
     */
    public void createProject(
            String projectName,
            String projectDescription,
            Time startTime,
            Time dueTime
    )
            throws DueBeforeSystemTimeException, ProjectNameAlreadyInUseException {
        if (getProject(projectName) == null) {
            Project newProject = new Project(
                    projectName,
                    projectDescription,
                    startTime,
                    dueTime
            );
            addProject(newProject);
        } else {
            throw new ProjectNameAlreadyInUseException();
        }
    }

    /**
     * Creates a task with the given information and adds it to the project corresponding to the given project name
     *
     * @param projectName  Project name of project which to add the created task to
     * @param taskName     Task name of the task to create and add to the project
     * @param description  Task description of the task
     * @param durationTime Duration of the task
     * @param deviation    Acceptable deviation of the task
     * @param roles        TODO
     * @throws ProjectNotFoundException      if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException         if one of the previous tasks does not correspond to an existing task
     * @throws TaskNameAlreadyInUseException if the given task name is already used by another task belonging to the given project
     */
    public void addTaskToProject(
            String projectName,
            String taskName,
            String description,
            Time durationTime,
            double deviation,
            List<Role> roles,
            Set<String> previousTasks,
            Set<String> nextTasks
    )
            throws ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.addNewTask(
                taskName,
                description,
                durationTime,
                deviation,
                roles,
                previousTasks,
                nextTasks
        );
    }

    public void deleteTask(String projectName, String taskName) throws TaskNotFoundException {
        Project project = getProject(projectName);
        project.deleteTask(taskName);
    }

    /**
     * Replaces a given (FAILED) task in a given project with a new task created from the given information
     *
     * @param replaces     Name of the task to replace
     * @param projectName  Name of the project in which to replace the task
     * @param taskName     Name of the task to create
     * @param description  Description of the task to create
     * @param durationTime Duration of the task to create
     * @param deviation    Accepted deviation of the task to create
     * @throws ReplacedTaskNotFailedException if the task to replace has not failed yet
     * @throws ProjectNotFoundException       if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException          if the given task name does not correspond to a task within the given project
     * @throws TaskNameAlreadyInUseException  if the task name to use for the new task is already in use by another task within the project
     */
    public void replaceTaskInProject(
            String projectName,
            String taskName,
            String description,
            Time durationTime,
            double deviation,
            String replaces
    )
            throws ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.replaceTask(
                taskName,
                description,
                durationTime,
                deviation,
                replaces
        );
    }

    /**
     * Gets the status of the given task within the given project
     *
     * @param projectName Name of the project to which the task is assigned
     * @param taskName    Name of the task of which to return the status
     * @return Status of the given task (AVAILABLE, UNAVAILABLE, EXECUTING, FINISHED, FAILED)
     * @throws ProjectNotFoundException if the given project does not correspond to an existing project
     * @throws TaskNotFoundException    if the given task does not correspond to an existing task within the given project
     */
    public Status getStatus(String projectName, String taskName)
            throws ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.getStatus(taskName);
    }

    /**
     * Sets the start time of the given (AVAILABLE) task, and changes its status to EXECUTING if this time is after the system time
     *
     * @param projectName Name of the project to which the task to start is attached
     * @param taskName    Name of the task to start
     * @param currentUser User currently logged in
     * @throws ProjectNotFoundException     if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException        if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectUserException       if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException if the task status is not AVAILABLE
     */
    public void startTask(
            String projectName,
            String taskName,
            User currentUser,
            Role role
    )
            throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.startTask(
                taskName,
                getSystemTime(),
                currentUser,
                role
        );
    }

    /**
     * Advances the system time
     *
     * @param newTime Time which to change the system time to
     * @throws NewTimeBeforeSystemTimeException if the given time is before the current system time
     */
    public void advanceTime(Time newTime)
            throws NewTimeBeforeSystemTimeException {
        if (newTime.before(getSystemTime())) {
            throw new NewTimeBeforeSystemTimeException();
        }
        setSystemTime(newTime);
    }

    /**
     * Advances the time with the given minutes
     *
     * @param advanceMinutes Amount of minutes to advance the system clock with
     * @throws NewTimeBeforeSystemTimeException if advanceMinutes < 0
     */
    public void advanceTime(int advanceMinutes) throws NewTimeBeforeSystemTimeException {
        if (advanceMinutes < 0) {
            throw new NewTimeBeforeSystemTimeException();
        }
        try {
            advanceTime(new Time(advanceMinutes).add(getSystemTime()));
        } catch (InvalidTimeException e) {
            throw new NewTimeBeforeSystemTimeException();
        }
    }


    public void clear() {
        projects = new LinkedList<>();
    }


    /**
     * Sets the end time of the given (EXECUTING) task, and changes its status to FINISHED
     *
     * @param projectName Name of the project to which the task to end is attached
     * @param taskName    Name of the task to end
     * @param user        User currently logged in
     * @throws ProjectNotFoundException         if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException            if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectUserException           if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException     if the task status is not EXECUTING
     */
    public void finishTask(String projectName, String taskName, User user) throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.finishTask(taskName, user, getSystemTime());
    }


    /**
     * Sets the end time of the given (EXECUTING) task, and changes its status to FAILED
     *
     * @param projectName Name of the project to which the task to end is attached
     * @param taskName    Name of the task to end
     * @param user        User currently logged in
     * @throws ProjectNotFoundException         if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException            if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectUserException           if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException     if the task status is not EXECUTING
     */
    public void failTask(String projectName, String taskName, User user) throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.failTask(taskName, user, getSystemTime());
    }

    public void addPreviousTaskToProject(String projectName, String taskName, String prevTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.addPreviousTask(taskName, prevTaskName);
    }

    public void addNextTaskToProject(String projectName, String taskName, String nextTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.addNextTask(taskName, nextTaskName);
    }

    public void removePreviousTaskFromProject(String projectName, String taskName, String prevTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.removePreviousTask(taskName, prevTaskName);
    }

    public void removeNextTaskFromProject(String projectName, String taskName, String nextTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.removeNextTask(taskName, nextTaskName);
    }
}
