package Domain.Project;

import Domain.DataClasses.EndTimeBeforeStartTimeException;
import Domain.DataClasses.Time;
import Domain.DataClasses.Tuple;
import Domain.Task.*;
import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.LoopDependencyGraphException;
import Domain.Task.Status;
import Domain.User.IncorrectUserException;
import Domain.User.Role;
import Domain.User.User;
import Domain.User.UserAlreadyAssignedToTaskException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A project currently registered, including a list of tasks that this project requires to be finished
 */
public class Project implements ProjectData {

    private final List<Task> tasks;
    private final List<Task> replacedTasks;
    private final String name;
    private final String description;
    private final Time creationTime;
    private final Time dueTime;
    private ProjectStatus status;

    public Project(String name, String description, Time creationTime, Time dueTime)
            throws DueTimeBeforeCreationTimeException {
        if (dueTime.before(creationTime)) {
            throw new DueTimeBeforeCreationTimeException();
        }
        this.tasks = new LinkedList<>();
        this.replacedTasks = new LinkedList<>();
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
        this.dueTime = dueTime;
        this.status = ProjectStatus.ONGOING;
    }

    /**
     * @return A string containing the description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return A string containing the name of the project
     */
    public String getName() {
        return name;
    }

    /**
     * @return The time this project was created (as a Time object)
     */
    public Time getCreationTime() {
        return creationTime;
    }

    /**
     * @return The time this project is due (as a Time object)
     */
    public Time getDueTime() {
        return dueTime;
    }

    /**
     * @return an IMMUTABLE list of the current project tasks
     */
    private List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    @Override
    public List<TaskData> getTasksData() {
        return getTasks().stream().map(Task::getTaskData).toList();
    }

    @Override
    public List<TaskData> getReplacedTasksData() {
        return getReplacedTasks().stream().map(Task::getTaskData).toList();
    }

    /**
     * @return an IMMUTABLE list of all tasks that have been replaced
     */
    private List<Task> getReplacedTasks() {
        return List.copyOf(replacedTasks);
    }

    /**
     * @param taskName The name of the task of which to get the task proxy
     * @return A read-only task proxy that contains task data and getters
     * @throws TaskNotFoundException if the taskname does not correspond to a task inside this project
     */
    public TaskData getTaskData(String taskName) throws TaskNotFoundException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        return task.getTaskData();
    }

    /**
     * @return Status of the current project, finished if all tasks are finished, ongoing otherwise
     */
    public ProjectStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of this project to the given status
     *
     * @param status The status this project should be changed into
     */
    private void setStatus(ProjectStatus status) {
        this.status = status;
    }

    /**
     * Passes the user input taskname on to the taskManager to fetch the corresponding task
     *
     * @param selectedTaskName User input, may correspond to a task name
     * @return The (unique) task corresponding with selectedTaskName, or null
     */
    private Task getTask(String selectedTaskName) {
        for (Task task : getTasks()) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        for (Task task : getReplacedTasks()) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Creates a task with the given information and adds it to this project
     *
     * @param taskName    Name of the task to create and add
     * @param description Description of the task
     * @param duration    Duration of the task
     * @param deviation   Accepted deviation of the task
     * @param roles       List of roles that are required for this task to start
     * @throws TaskNotFoundException         if one of the given tasks to be completed before the new task does not exist
     * @throws TaskNameAlreadyInUseException if the given task name is already in use for this project
     */
    public void addNewTask(
            String taskName,
            String description,
            Time duration,
            double deviation,
            List<Role> roles,
            Set<Tuple<Project,String>> prevTasksNames,
            Set<Tuple<Project,String>> nextTasksNames
    ) throws TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, IllegalTaskRolesException, ProjectNotOngoingException {
        if (getTask(taskName) != null) {
            throw new TaskNameAlreadyInUseException();
        }

        if (getStatus() != ProjectStatus.ONGOING) {
            throw new ProjectNotOngoingException();
        }

        Set<Task> prevTasks = new HashSet<>();
        for (Tuple<Project,String> prevTask : prevTasksNames) {
            Project prevProject = prevTask.getFirst();
            String prevTaskName = prevTask.getSecond();

            Task task = prevProject.getTask(prevTaskName);
            if (task == null) {
                throw new TaskNotFoundException();
            }
            prevTasks.add(task);
        }
        Set<Task> nextTasks = new HashSet<>();
        for (Tuple<Project,String> nextTask : nextTasksNames) {
            Project nextProject = nextTask.getFirst();
            String nextTaskName = nextTask.getSecond();

            Task task = nextProject.getTask(nextTaskName);
            if (task == null) {
                throw new TaskNotFoundException();
            }
            nextTasks.add(task);
        }

        addTask(new Task(taskName, description, duration, deviation, roles, prevTasks, nextTasks, getName()));
    }

    /**
     * @param task The task to be added to the list of active tasks
     * @post The task is added to the list of active tasks
     */
    private void addTask(Task task) {
        tasks.add(task);
    }


    /**
     * Deletes the given task from the list of active tasks
     *
     * @param task  Task to be removed to the list of active tasks
     */
    private void deleteTask(Task task){
        if (task.getReplacesTaskName() != null){
            Task replaces = getTask(task.getReplacesTaskName());
            removeReplacedTask(replaces);
            addTask(replaces);
        }
        task.clearTask();
        removeActiveTask(task);
        removeReplacedTask(task);
    }

    /**
     * Deletes the given task from the list of active tasks
     *
     * @param taskName                  Name of the task to be removed to the list of active tasks
     * @throws TaskNotFoundException    if the taskname does not correspond to a task inside this project
     */
    public void deleteTask(String taskName) throws TaskNotFoundException {
        Task task = getTask(taskName);
        if (task == null){
            throw new TaskNotFoundException();
        }
        deleteTask(task);
    }

    /**
     * Clears all tasks from the project
     */
    public void clearTasks(){
        for (Task task : getReplacedTasks()){
            deleteTask(task);
        }
        for (Task task : getTasks()){
            deleteTask(task);
        }
        updateProjectStatus();
    }

    /**
     * @param task The task to be removed to the list of active tasks
     * @post The task is removed from the list of active tasks
     */
    private void removeActiveTask(Task task) {
        tasks.remove(task);
    }

    /**
     * @param task The task to be added to the list of replaced tasks
     * @post The task is added to the list of replaced tasks
     */
    private void addReplacedTask(Task task) {
        replacedTasks.add(task);
    }

    /**
     * @param task The task to be removed to the list of replaced tasks
     * @post The task is removed from the list of replaced tasks
     */
    private void removeReplacedTask(Task task) {
        replacedTasks.remove(task);
    }

    /**
     * Replaces the given (FAILED) task by a newly created task created with the given information
     *
     * @param taskName    Name of the newly created task
     * @param description Description of the new task
     * @param duration    Duration of the new task
     * @param deviation   Accepted deviation of the new task
     * @param replaces    Name of the task to replace
     * @throws TaskNotFoundException         if the task corresponding to "replaces" does not exist
     * @throws TaskNameAlreadyInUseException if the given taskName is already in use in this project
     * @throws IncorrectTaskStatusException  if the task to replace is not failed
     */
    public void replaceTask(
            String taskName,
            String description,
            Time duration,
            double deviation,
            String replaces
    )
            throws TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        if (getTask(taskName) != null) {
            throw new TaskNameAlreadyInUseException();
        }

        Task replacesTask = getTask(replaces);
        if (replacesTask == null) {
            throw new TaskNotFoundException();
        }
        Task replacement = new Task(taskName, description, duration, deviation);
        replacesTask.replaceTask(replacement);
        addTask(replacement);
        removeActiveTask(replacesTask);
        addReplacedTask(replacesTask);
    }

    /**
     * Sets the start time of the given task, and changes its status according to the given system time
     *
     * @param taskName    Name of the status of which to change the status
     * @param startTime   Start time of the given task
     * @param currentUser User currently logged in
     * @throws TaskNotFoundException              if the given task name does not correspond to an existing task within this project
     * @throws IncorrectTaskStatusException       if the given task status is not currently AVAILABLE or PENDING
     * @throws UserAlreadyAssignedToTaskException if currentuser is already assigned to this task
     * @throws IncorrectRoleException             if this role is not necessary for the given task OR
     *                                            the given user does not have the given role
     */
    public void startTask(
            String taskName,
            Time startTime,
            User currentUser,
            Role role
    )
            throws TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.start(startTime, currentUser, role);
    }

    /**
     * Stops the given task //TODO: ik stop voor vandaag
     *
     * @param taskName
     * @param currentUser
     * @throws TaskNotFoundException
     * @throws IncorrectTaskStatusException
     * @throws IncorrectUserException
     */
    public void undoStartTask(
            String taskName,
            User currentUser
    )
            throws TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.undoStart(currentUser);
    }

    public void undoEndTask(
            String taskName
    )
            throws TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyAssignedToTaskException, IncorrectRoleException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.undoEnd();
        updateProjectStatus();
    }


    /**
     * Ends the given task and sets its status to FINISHED
     *
     * @param taskName Name of the status to end
     * @param endTime  Time at which the task was/will be finished/failed
     * @param user     User currently logged in
     * @throws TaskNotFoundException           if taskName does not correspond to an existing task
     * @throws IncorrectUserException          if currentUser is not the user assigned to this task
     * @throws IncorrectTaskStatusException    if the given task status is not EXECUTING
     * @throws EndTimeBeforeStartTimeException if the given endTime is before the tasks' start time
     */
    public void finishTask(String taskName, User user, Time endTime) throws TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.finish(user, endTime);

        updateProjectStatus();
    }

    /**
     * Ends the given task and sets its status to FAILED
     *
     * @param taskName Name of the status to end
     * @param endTime  Time at which the task was/will be finished/failed
     * @param user     User currently logged in
     * @throws TaskNotFoundException           if taskName does not correspond to an existing task
     * @throws IncorrectUserException          if currentUser is not the user assigned to this task
     * @throws IncorrectTaskStatusException    if the given task status is not EXECUTING
     * @throws EndTimeBeforeStartTimeException if the given endTime is before the tasks' start time
     */
    public void failTask(String taskName, User user, Time endTime) throws TaskNotFoundException, IncorrectTaskStatusException, IncorrectUserException, EndTimeBeforeStartTimeException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.fail(user, endTime);
    }

    /**
     * Updates this projects' status
     *
     * @post if all tasks are finished then set this' status to FINISHED,
     * else set this' status to ONGOING
     */
    private void updateProjectStatus() {
        setStatus(ProjectStatus.FINISHED);
        if (getTasks().size() == 0){
            setStatus(ProjectStatus.ONGOING);
        }
        for (Task task : getTasks()) {
            if (task.getStatus() != Status.FINISHED) {
                setStatus(ProjectStatus.ONGOING);
            }
        }
    }

    /**
     * Adds a task to the prevTasks list of a given task in the project, respecting the rules of the dependency graph
     *
     * @param taskName     Name of the task to add a previous task to
     * @param prevProject  Project to which the previous task belongs
     * @param prevTaskName Name of the task to add to the list of prevTasks
     * @throws TaskNotFoundException        if taskName or prevTaskName do not correspond to an existing task in this project
     * @throws IncorrectTaskStatusException if taskName does not correspond to a task that is AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException if adding this previous task would create a loop in the dependency graph of this projects' tasks
     * @post if the task corresponding to taskName is AVAILABLE, then sets taskName's status to UNAVAILABLE
     */
    public void addPrevTask(String taskName, Project prevProject, String prevTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        Task task = getTask(taskName);
        Task prevTask = prevProject.getTask(prevTaskName);
        if (task == null || prevTask == null) {
            throw new TaskNotFoundException();
        }
        task.addPrevTask(prevTask);
    }

    /**
     * Adds a task to the nextTasks list of a given task in the project, respecting the rules of the dependency graph
     *
     * @param taskName     Name of the task to add a next task to
     * @param nextProject  Project to which the next task belongs
     * @param nextTaskName Name of the task to add to the list of nextTasks
     * @throws TaskNotFoundException        if taskName or nextTaskName do not correspond to an existing task in this project
     * @throws IncorrectTaskStatusException if nextTaskName does not correspond to a task that is AVAILABLE or UNAVAILABLE
     * @throws LoopDependencyGraphException if adding this next task would create a loop in the dependency graph of this projects' tasks
     * @post if the task corresponding to nextTaskName is AVAILABLE, then sets taskName's status to UNAVAILABLE
     */
    public void addNextTask(String taskName, Project nextProject, String nextTaskName) throws TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException {
        Task task = getTask(taskName);
        Task nextTask = nextProject.getTask(nextTaskName);
        if (task == null || nextTask == null) {
            throw new TaskNotFoundException();
        }
        task.addNextTask(nextTask);
    }

    /**
     * Removes a task from the prevTasks list of a given task in the project, respecting the rules of the dependency graph
     *
     * @param taskName     Name of the task to remove the previous task from
     * @param prevProject  Project to which the previous task belongs
     * @param prevTaskName Name of the previous task to add
     * @throws TaskNotFoundException        if taskName or prevTaskName do not correspond to an existing task within this project
     * @post if prevTaskName is the last previous task in taskName, then sets the status of taskName to AVAILABLE
     */
    public void removePrevTask(String taskName, Project prevProject, String prevTaskName) throws TaskNotFoundException {
        Task task = getTask(taskName);
        Task prevTask = prevProject.getTask(prevTaskName);
        if (task == null || prevTask == null) {
            throw new TaskNotFoundException();
        }
        task.removePrevTask(prevTask);
    }

    /**
     * Removes a task from the nextTasks list of a given task in the project, respecting the rules of the dependency graph
     *
     * @param taskName     Name of the task to remove the next task from
     * @param nextProject  Project to which the next task belongs
     * @param nextTaskName Name of the next task to add
     * @throws TaskNotFoundException        if taskName or nextTaskName do not correspond to an existing task within this project
     * @post if taskName is the last previous task of nextTaskName, then sets the status of nextTask to AVAILABLE
     */
    public void removeNextTask(String taskName, Project nextProject, String nextTaskName) throws TaskNotFoundException {
        Task task = getTask(taskName);
        Task nextTask = nextProject.getTask(nextTaskName);
        if (task == null || nextTask == null) {
            throw new TaskNotFoundException();
        }
        task.removeNextTask(nextTask);
    }
}
