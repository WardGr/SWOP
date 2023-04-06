package Domain;

import java.util.*;

/**
 * Central domain-level system class, keeps track of system time and all projects, first point of entry into the domain
 * layer
 */
public class TaskManSystem {

    private List<Project> projects;
    private Time systemTime;

    private final TaskManSystemProxy taskManSystemProxy;

    public TaskManSystem(Time systemTime) {
        this.systemTime = systemTime;
        projects = new LinkedList<>();
        taskManSystemProxy = new TaskManSystemProxy(this);
    }

    public TaskManSystemProxy getTaskManSystemData(){
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

    public Domain.TaskStates.TaskProxy getTaskData(String projectName, String taskName) throws TaskNotFoundException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null){
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
     * @param projectName   Project name of project which to add the created task to
     * @param taskName      Task name of the task to create and add to the project
     * @param description   Task description of the task
     * @param durationTime  Duration of the task
     * @param deviation     Acceptable deviation of the task
     * @param roles     TODO
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
            List<Role> roles
    )
            throws ProjectNotFoundException, TaskNameAlreadyInUseException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.addNewTask(
                taskName,
                description,
                durationTime,
                deviation,
                roles
        );
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
     * @return A map with tuples (project, tasks) mapping all projects to a list of all its available tasks
     */
    public Map<String, List<String>> showAvailableTasks() {
        Map<String, List<String>> availableTasks = new HashMap<>();
        for (Project project : getProjects()) {
            availableTasks.put(project.getName(), project.showAvailableTasks());
        }
        return availableTasks;
    }

    /**
     * @return A map with tuples (project, tasks) mapping all projects to a list of all its executing tasks
     */
    public Map<String, List<String>> showExecutingTasks() {
        Map<String, List<String>> executingTasks = new HashMap<>();
        for (Project project : getProjects()) {
            executingTasks.put(project.getName(), project.showExecutingTasks());
        }
        return executingTasks;
    }

    /**
     * Gets the list of next possible statuses the given task in the given project can be changed into by the user assigned
     * to this task
     *
     * @param projectName Name of the project
     * @param taskName    Name of the task for which to return the next possible statuses
     * @return A list of statuses to which the given task can be changed by the assigned user
     * @throws ProjectNotFoundException if the given project does not correspond to an existing project
     * @throws TaskNotFoundException    if the given task does not correspond to an existing task within the given project
     */
    public List<Status> getNextStatuses(String projectName, String taskName)
            throws ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.getNextStatuses(taskName);
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
     * @param startTime   Time at which the task should start
     * @param currentUser User currently logged in
     * @throws ProjectNotFoundException     if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException        if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectUserException       if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException if the task status is not AVAILABLE
     */
    public void startTask(
            String projectName,
            String taskName,
            Time startTime,
            User currentUser
    )
            throws ProjectNotFoundException, TaskNotFoundException, IncorrectUserException, IncorrectTaskStatusException, StartTimeBeforeAvailableException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.startTask(
                taskName,
                startTime,
                getSystemTime(),
                currentUser
        );
    }

    /**
     * Sets the end time of the given (EXECUTING) task, and changes its status to the given status
     *
     * @param projectName Name of the project to which the task to end is attached
     * @param taskName    Name of the task to end
     * @param endTime     Time at which the task should end
     * @param newStatus   Status to change the given task into
     * @param currentUser User currently logged in
     * @throws ProjectNotFoundException         if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException            if the given task name does not correspond to an existing task within the given project
     * @throws FailTimeAfterSystemTimeException if newStatus == FAILED and the given end time is after the system time
     * @throws IncorrectUserException           if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException     if the task status is not EXECUTING
     */
    public void endTask(
            String projectName,
            String taskName,
            Status newStatus,
            Time endTime,
            User currentUser
    )
            throws ProjectNotFoundException, TaskNotFoundException, FailTimeAfterSystemTimeException, IncorrectUserException, IncorrectTaskStatusException, EndTimeBeforeStartTimeException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.endTask(
                taskName,
                newStatus,
                endTime,
                getSystemTime(),
                currentUser
        );
    }

    /**
     * Advances the system time
     *
     * @param newTime Time which to change the system time to
     * @throws NewTimeBeforeSystemTimeException if the given time is before the current system time
     * @throws InvalidTimeException             if newMinute < 0 or newMinute > 59
     */
    public void advanceTime(Time newTime)
            throws NewTimeBeforeSystemTimeException, InvalidTimeException {
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
     * @throws InvalidTimeException if an incorrect Time object is made (this would be a bug)
     */
    public void advanceTime(int advanceMinutes) throws NewTimeBeforeSystemTimeException, InvalidTimeException {
        if(advanceMinutes < 0) {
            throw new NewTimeBeforeSystemTimeException();
        }
        advanceTime(new Time(advanceMinutes).add(getSystemTime()));
    }


    public void clear(){
        projects = new LinkedList<>();
    }


    public List<String> showUnfinishedTasks(){
        List<String> projects = new LinkedList<>();
        for (Project project : getProjects()){
            // TODO met die enum status checken of het finished is!
            projects.add(project.getName());
        }
        return projects;
    }
}
