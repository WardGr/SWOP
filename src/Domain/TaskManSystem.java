package Domain;

import Domain.TaskStates.IllegalTaskRolesException;
import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.TaskData;
import UserInterface.TaskUI;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Central domain-level system class, keeps track of system time and all projects, first point of entry into the domain
 * layer
 */
public class TaskManSystem {

    private final TaskManSystemData taskManSystemData;
    private List<Project> projects;
    private Time systemTime;

    /**
     * Initialises the system, setting the system time, alongside an empty list of projects
     * and setting the TaskManSystemData
     *
     * @param systemTime The initial system time (as a Time object)
     */
    public TaskManSystem(Time systemTime) {
        this.systemTime = systemTime;
        projects = new LinkedList<>();
        taskManSystemData = new TaskManSystemData(this);
    }

    /**
     * @return An immutable proxy of this object, containing some of this objects' details
     */
    public TaskManSystemData getTaskManSystemData() {
        return taskManSystemData;
    }

    /**
     * @return The current system time as a Time object
     */
    public Time getSystemTime() {
        return systemTime;
    }

    /**
     * @param newTime The new time to set as systemtime, as a Time object
     */
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
     * @return List of all project names, as strings
     */
    List<String> getProjectNames() {
        List<String> names = new LinkedList<>();
        for (Project project : getProjects()) {
            names.add(project.getName());
        }
        return names;
    }

    /**
     * Returns an immutable project proxy of the project with the given projectname, if it exists in the current system
     *
     * @param projectName Name of the project
     * @return An immutable project proxy containing the projects' details
     * @throws ProjectNotFoundException if projectName does not correspond to an existing project within the systen
     */
    public ProjectData getProjectData(String projectName) throws ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.getProjectData();
    }

    /**
     * Returns an immutable task proxy of the task with the given name, inside the project of the given name
     *
     * @param projectName Project to which the given task belongs
     * @param taskName    Task of which to get the task proxy
     * @return An immutable task proxy containing details and getters of the given task
     * @throws TaskNotFoundException    if the given taskName does not correspond to an existing task within the given project
     * @throws ProjectNotFoundException if the given projectName does not correspond to an existing project within the system
     */
    public TaskData getTaskData(String projectName, String taskName) throws TaskNotFoundException, ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.getTaskData(taskName);
    }

    /**
     * @param newProject The project to add to the current list of projects
     */
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
        try {
            createProject(projectName, projectDescription, getSystemTime(), dueTime);
        } catch (DueTimeBeforeCreationTimeException e) {
            throw new DueBeforeSystemTimeException();
        }
    }

    public void deleteProject(String projectName) throws ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.clearTasks();
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
     * @throws DueTimeBeforeCreationTimeException if the given due time is before system time
     * @throws ProjectNameAlreadyInUseException   if the given project name is already in use
     */
    private void createProject(
            String projectName,
            String projectDescription,
            Time startTime,
            Time dueTime
    )
            throws ProjectNameAlreadyInUseException, DueTimeBeforeCreationTimeException {
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
     * @param roles        List of roles necessary for this task to start
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
            Set<Tuple<String,String>> prevTaskStrings,
            Set<Tuple<String,String>> nextTaskStrings
    )
            throws ProjectNotFoundException, TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, ProjectNotOngoingException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }

        Set<Tuple<Project,String>> prevTasks = new HashSet<>();
        for (Tuple<String,String> prevTask : prevTaskStrings){
            String prevProjectName = prevTask.getFirst();
            String prevTaskName = prevTask.getSecond();

            Project prevProject = getProject(prevProjectName);
            if (prevProject == null){
                throw new ProjectNotFoundException();
            }
            prevTasks.add(new Tuple<>(prevProject, prevTaskName));
        }
        Set<Tuple<Project,String>> nextTasks = new HashSet<>();
        for (Tuple<String,String> nextTask : nextTaskStrings){
            String nextProjectName = nextTask.getFirst();
            String nextTaskName = nextTask.getSecond();

            Project nextProject = getProject(nextProjectName);
            if (nextProject == null){
                throw new ProjectNotFoundException();
            }
            nextTasks.add(new Tuple<>(nextProject, nextTaskName));
        }


        project.addNewTask(
                taskName,
                description,
                durationTime,
                deviation,
                roles,
                prevTasks,
                nextTasks
        );
    }

    public void deleteTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null){
            throw new ProjectNotFoundException();
        }
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
     * @throws ProjectNotFoundException      if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException         if the given task name does not correspond to a task within the given project
     * @throws TaskNameAlreadyInUseException if the task name to use for the new task is already in use by another task within the project
     */
    public void replaceTaskInProject(
            String projectName,
            String taskName,
            String description,
            Time durationTime,
            double deviation,
            String replaces
    )
            throws ProjectNotFoundException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
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
     * Sets the start time of the given (AVAILABLE) task, and changes its status to EXECUTING if this time is after the system time
     *
     * @param projectName Name of the project to which the task to start is attached
     * @param taskName    Name of the task to start
     * @param currentUser User currently logged in
     * @throws ProjectNotFoundException           if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException              if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectTaskStatusException       if the task status is not AVAILABLE or PENDING
     * @throws UserAlreadyAssignedToTaskException if the given user is already assigned to the task
     * @throws IncorrectRoleException             if this role is not necessary for the given task OR
     *                                            the given user does not have the given role
     */
    public void startTask(
            String projectName,
            String taskName,
            User currentUser,
            Role role
    )
            throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
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
    public void advanceTime(Time newTime) throws NewTimeBeforeSystemTimeException {
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
        try {
            advanceTime(new Time(advanceMinutes).add(getSystemTime()));
        } catch (InvalidTimeException e) {
            throw new NewTimeBeforeSystemTimeException();
        }
    }

    /**
     * Resets the system, removing all projects and resetting the system time to 0
     *
     * @post systemTime.getTotalMinutes() == 0
     */
    public void reset() throws InvalidTimeException {
        projects = new LinkedList<>();
        systemTime = new Time(0);
    }


    /**
     * Sets the end time of the given (EXECUTING) task, and changes its status to FINISHED
     *
     * @param projectName Name of the project to which the task to end is attached
     * @param taskName    Name of the task to end
     * @param user        User currently logged in
     * @throws ProjectNotFoundException     if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException        if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectUserException       if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException if the task status is not EXECUTING
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
     * @throws ProjectNotFoundException     if the given project name does not correspond to an existing project
     * @throws TaskNotFoundException        if the given task name does not correspond to an existing task within the given project
     * @throws IncorrectUserException       if currentUser is not the user assigned to the given task
     * @throws IncorrectTaskStatusException if the task status is not EXECUTING
     */
    public void failTask(String projectName, String taskName, User user) throws ProjectNotFoundException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.failTask(taskName, user, getSystemTime());
    }

    /**
     * Adds a previous task to a given task within a given project
     *
     * @param projectName     The name corresponding with the project
     * @param taskName        The name corresponding to the task which to add the previous task to
     * @param prevProjectName The name corresponding to the project the previous task belongs to
     * @param prevTaskName    The name corresponding to the task to add as a previous task
     * @throws ProjectNotFoundException     If the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException        If taskName or prevTaskName do not correspond to a task within the given project
     * @throws IncorrectTaskStatusException if the status of the taskName task is not available or unavailable
     * @throws LoopDependencyGraphException if adding this previous task create a loop in the dependency graph
     * @post if the task corresponding to taskName is AVAILABLE, then sets taskName's status to UNAVAILABLE
     */
    public void addPrevTaskToProject(String projectName, String taskName, String prevProjectName, String prevTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNotFoundException {
        Project project = getProject(projectName);
        Project prevProject = getProject(prevProjectName);
        if (project == null || prevProject == null) {
            throw new ProjectNotFoundException();
        }

        project.addPrevTask(taskName, prevProject, prevTaskName);
    }

    /**
     * Adds a task as a next task to the given task in the given project
     *
     * @param projectName     The name of the project
     * @param taskName        The name of the task to add the next task to
     * @param nextProjectName The name of the project the next task belongs to
     * @param nextTaskName    The name of the next task
     * @throws TaskNotFoundException        if taskName or nextTaskName do not correspond to an existing task within the given project
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project
     * @throws IncorrectTaskStatusException if the task corresponding to taskName is not available or unavailable
     * @throws LoopDependencyGraphException if adding this task causes a loop in the dependency graph of the given project
     * @post if the task corresponding to nextTaskName is AVAILABLE, then sets taskName's status to UNAVAILABLE
     */
    public void addNextTaskToProject(String projectName, String taskName, String nextProjectName, String nextTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, ProjectNotFoundException {
        Project project = getProject(projectName);
        Project nextProject = getProject(nextProjectName);
        if (project == null || nextProject == null) {
            throw new ProjectNotFoundException();
        }
        project.addNextTask(taskName, nextProject, nextTaskName);
    }

    /**
     * Removes a task from the prevTasks list of a given task in the project, respecting the rules of the dependency graph
     *
     * @param projectName     Name of the project to add the task to
     * @param taskName        Name of the task to remove the previous task from
     * @param prevProjectName Name of the project to which the previous task belongs
     * @param prevTaskName    Name of the previous task to add
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project
     * @throws TaskNotFoundException        if taskName or prevTaskName do not correspond to an existing task within this project
     * @post if prevTaskName is the last previous task in taskName, then sets the status of taskName to AVAILABLE
     */
    public void removePrevTaskFromProject(String projectName, String taskName, String prevProjectName, String prevTaskName) throws TaskNotFoundException, ProjectNotFoundException {
        Project project = getProject(projectName);
        Project prevProject = getProject(prevProjectName);
        if (project == null || prevProject == null) {
            throw new ProjectNotFoundException();
        }
        project.removePrevTask(taskName, prevProject, prevTaskName);
    }

    /**
     * Removes a task from the nextTasks list of a given task in a given project, respecting the rules of the dependency graph
     *
     * @param projectName     Name of the project to add the next task to
     * @param taskName        Name of the task to remove the next task from
     * @param nextProjectName Name of the project the next task belongs to
     * @param nextTaskName    Name of the next task to add
     * @throws TaskNotFoundException        if taskName or nextTaskName do not correspond to an existing task within this project
     * @throws ProjectNotFoundException     if the given projectName does not correspond to an existing project
     * @post if taskName is the last prevtask of nextTask, then set nextTask to AVAILABLE
     */
    public void removeNextTaskFromProject(String projectName, String taskName, String nextProjectName, String nextTaskName) throws TaskNotFoundException, ProjectNotFoundException {
        Project project = getProject(projectName);
        Project nextProject = getProject(nextProjectName);
        if (project == null || nextProject == null) {
            throw new ProjectNotFoundException();
        }
        project.removeNextTask(taskName, nextProject, nextTaskName);
    }
}
