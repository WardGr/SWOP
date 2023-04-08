package Domain;

import Domain.TaskStates.*;
import Domain.TaskStates.Task;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A project currently registered, including a list of tasks that this project requires
 */
public class  Project implements TaskObserver {

    private final List<Domain.TaskStates.Task> tasks;
    private final List<Domain.TaskStates.Task> replacedTasks;
    private final String name;
    private final String description;
    private final Time creationTime;
    private final Time dueTime;
    private final ProjectProxy projectProxy;
    private ProjectStatus status;

    public Project(String name, String description, Time creationTime, Time dueTime)
            throws DueBeforeSystemTimeException {
        if (!dueTime.after(creationTime)) {
            throw new DueBeforeSystemTimeException();
        }
        this.tasks = new LinkedList<>();
        this.replacedTasks = new LinkedList<>();
        this.name = name;
        this.description = description;
        this.creationTime = creationTime;
        this.dueTime = dueTime;
        this.projectProxy = new ProjectProxy(this);
        this.status = ProjectStatus.ONGOING;
    }

    @Override
    public String toString() {
        StringBuilder projectString = new StringBuilder();

        projectString.append("Project Name:  " + getName() + '\n' +
                "Description:   " + getDescription() + '\n' +
                "Creation Time: " + getCreationTime() + '\n' +
                "Due Time:      " + getDueTime() + '\n' +
                "Status:        " + getStatus() + '\n'
        );

        if (getTasks().size() > 0) {
            projectString.append("\nTasks:\n");
            int index = 1;
            for (Domain.TaskStates.Task task : getTasks()) {
                projectString.append(index++ + ". " + task.getName() + '\n');
            }
        }

        if (getReplacedTasks().size() > 0) {
            projectString.append("\nTasks that have been replaced:\n");

            int index = 1;
            for (Domain.TaskStates.Task task : getReplacedTasks()) {
                projectString.append(index++ + ". " + task.getName() + ", replaced by task: " + task.getReplacementTask().getName());
            }
            projectString.append('\n');
        }


        // TODO geef de totale uitvoeringstijd !!!

        return projectString.toString();
    }


    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Time getCreationTime() {
        return creationTime;
    }

    public Time getDueTime() {
        return dueTime;
    }

    /**
     * @return an IMMUTABLE list of the current project tasks
     */
    public List<Domain.TaskStates.Task> getTasks() {
        return List.copyOf(tasks);
    }

    public List<String> getActiveTasksNames() {
        List<String> names = new LinkedList<>();
        for (Task task : getTasks()) {
            names.add(task.getName());
        }
        return names;
    }

    public List<String> getReplacedTasksNames() {
        List<String> names = new LinkedList<>();
        for (Task task : getReplacedTasks()){
            names.add(task.getName());
        }
        return names;
    }

    /**
     * @return an IMMUTABLE list of all tasks that have been replaced
     */
    private List<Domain.TaskStates.Task> getReplacedTasks() {
        return List.copyOf(replacedTasks);
    }

    public ProjectProxy getProjectData(){
        return projectProxy;
    }

    public Domain.TaskStates.TaskProxy getTaskData(String taskName) throws TaskNotFoundException {
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
     * Passes the user input taskname on to the taskManager to fetch the corresponding task
     *
     * @param selectedTaskName User input, may correspond to a task name
     * @return The (unique) task corresponding with selectedTaskName, or null
     */
    public Domain.TaskStates.Task getTask(String selectedTaskName) {
        for (Domain.TaskStates.Task task : getTasks()) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        for (Domain.TaskStates.Task task : getReplacedTasks()) {
            if (task.getName().equals(selectedTaskName)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Creates a task with the given information and adds it to this project
     *
     * @param taskName          Name of the task to create and add
     * @param description       Description of the task
     * @param duration          Duration of the task
     * @param deviation         Accepted deviation of the task
     * @param roles             TODO
     * @throws TaskNotFoundException         if one of the given tasks to be completed before the new task does not exist
     * @throws TaskNameAlreadyInUseException if the given task name is already in use for this project
     */
    public void addNewTask(
            String taskName,
            String description,
            Time duration,
            double deviation,
            List<Role> roles,
            Set<String> previousTasksNames,
            Set<String> nextTasksNames
    ) throws TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, LoopDependencyGraphException, NonDeveloperRoleException {
        if (getTask(taskName) != null) {
            throw new TaskNameAlreadyInUseException();
        }

        // TODO check if project is not finished

        Set<Task> previousTasks = new HashSet<>();
        for (String previousTaskName : previousTasksNames) {
            Task task = getTask(previousTaskName);
            if (task == null) {
                throw new TaskNotFoundException();
            }
            previousTasks.add(task);
        }
        Set<Task> nextTasks = new HashSet<>();
        for (String nextTaskName : nextTasksNames) {
            Task task = getTask(nextTaskName);
            if (task == null) {
                throw new TaskNotFoundException();
            }
            nextTasks.add(task);
        }

        Task.NewActiveTask(taskName, description, duration, deviation, roles, previousTasks, nextTasks, this, new LinkedList<>());

        //addTask(new Domain.TaskStates.Task(taskName, description, duration, deviation, roles));
    }

    private void addTask(Domain.TaskStates.Task task) {
        tasks.add(task);
    }

    private void removeTask(Domain.TaskStates.Task task) {
        tasks.remove(task);
    }

    private void addReplacedTask(Domain.TaskStates.Task task) {
        replacedTasks.add(task);
    }

    /**
     * Replaces the given (FAILED) task by a newly created task created with the given information
     *
     * @param taskName    Name of the newly created task
     * @param description Description of the new task
     * @param duration    Duration of the new task
     * @param deviation   Accepted deviation of the new task
     * @param replaces    Name of the task to replace
     * @throws ReplacedTaskNotFailedException if the task corresponding to "replaces" has not failed
     * @throws TaskNotFoundException          if the task corresponding to "replaces" does not exist
     * @throws TaskNameAlreadyInUseException  if the given taskName is already in use in this project
     */
    public void replaceTask(
            String taskName,
            String description,
            Time duration,
            double deviation,
            String replaces
    )
            throws ReplacedTaskNotFailedException, TaskNotFoundException, TaskNameAlreadyInUseException, IncorrectTaskStatusException {
        if (getTask(taskName) != null) {
            throw new TaskNameAlreadyInUseException();
        }

        Task replacesTask = getTask(replaces);
        if (replacesTask == null) {
            throw new TaskNotFoundException();
        }
        Task.replaceTask(taskName, description, duration, deviation, replacesTask);
    }

    /**
     * @return A list of names of all tasks in this project that are available
     */
    public List<String> showAvailableTasks() {
        List<String> availableTasks = new LinkedList<>();
        for (Task task : getTasks()) {
            if (task.getStatus() == Status.AVAILABLE) { // TODO: wat met status checks buiten de Task class, we hebben nu een temporary status getter?
                availableTasks.add(task.getName());
            }
        }
        return availableTasks;
    }

    /**
     * @return A list of names of all tasks in this project that are executing
     */
    public List<String> showExecutingTasks() {
        List<String> executingTasks = new LinkedList<>();
        for (Domain.TaskStates.Task task : getTasks()) {
            if (task.getStatus() == Status.EXECUTING) {
                executingTasks.add(task.getName());
            }
        }
        return executingTasks;
    }

    /**
     * Gets detailed information about the task with the given task name
     *
     * @param taskName Name of the task of which to return
     * @return String containing detailed information about the given task
     * @throws TaskNotFoundException if taskName does not correspond to an existing task within this project
     */
    public String showTask(String taskName) throws TaskNotFoundException {
        Domain.TaskStates.Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        return task.toString();
    }

    /**
     * Gets a list of all possible next statuses the given task could be changed into by the assigned user
     *
     * @param taskName Name of the task of which to return the next statuses
     * @return List of all possible next statuses of the given task
     * @throws TaskNotFoundException if the given taskName does not correspond to an existing task within this project
     */
    public List<Status> getNextStatuses(String taskName)
            throws TaskNotFoundException {
        Domain.TaskStates.Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        return task.getNextStatuses();
    }

    /**
     * Gets the status of the task within this project corresponding to the given task name
     *
     * @param taskName Name of the task for which to retrieve the current status
     * @return Current status of the task corresponding to taskName
     * @throws TaskNotFoundException if taskName does not correspond to an existing task within this project
     */
    public Status getStatus(String taskName) throws TaskNotFoundException {
        Domain.TaskStates.Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        return task.getStatus();
    }

    private void setStatus(ProjectStatus status){
        this.status = status;
    }

    /**
     * Sets the start time of the given task, and changes its status according to the given system time
     *
     * @param taskName    Name of the status of which to change the status
     * @param startTime   Start time of the given task
     * @param currentUser User currently logged in
     * @throws TaskNotFoundException        if the given task name does not correspond to an existing task within this project
     * @throws IncorrectUserException       if currentUser is not assigned to the given task
     * @throws IncorrectTaskStatusException if the given task status is not currently AVAILABLE
     */
    public void startTask(
            String taskName,
            Time startTime,
            User currentUser,
            Role role
    )
            throws TaskNotFoundException, IncorrectTaskStatusException, UserAlreadyExecutingTaskException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.start(startTime, currentUser, role);
    }

    /**
     * Ends the given task and sets its status to FAILED or FINISHED according to input
     *
     * @param taskName    Name of the status to end
     * @param newStatus   New status of the task
     * @param endTime     Time at which the task was/will be finished/failed
     * @param systemTime  Current system-time
     * @param currentUser User currently logged in
     * @throws TaskNotFoundException            if taskName does not correspond to an existing task
     * @throws FailTimeAfterSystemTimeException if newStatus == FAILED and endTime > systemTime
     * @throws IncorrectUserException           if currentUser is not the user assigned to this task
     * @throws IncorrectTaskStatusException     if the given task status is not EXECUTING
     */
    public void endTask(
            String taskName,
            Status newStatus,
            Time endTime,
            Time systemTime,
            User currentUser
    )
            throws TaskNotFoundException, FailTimeAfterSystemTimeException, IncorrectUserException, EndTimeBeforeStartTimeException, IncorrectTaskStatusException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.end(newStatus, endTime, systemTime, currentUser);
    }

    public void update(Task updatedTask){
        if (getTasks().contains(updatedTask)) {
            if (updatedTask.getStatus() == Status.FINISHED){
                for (Task task : getTasks()){
                    if (task.getStatus() != Status.FINISHED){
                        setStatus(ProjectStatus.ONGOING);
                        return;
                    }
                }
                // TODO finished status instellen met te laat en te vroeg en alles? -> dat zit bij task zeker?
                setStatus(ProjectStatus.FINISHED);
            }
            if (updatedTask.getReplacementTask() != null){
                // TODO getters en setters
                if (getTasks().contains(updatedTask)) {
                    tasks.remove(updatedTask);
                    replacedTasks.add(updatedTask);
                }
            }
        } else if (!getReplacedTasks().contains(updatedTask)){
            addTask(updatedTask);
        }

    }

    public void finishTask(String taskName, User user, Time endTime) throws TaskNotFoundException, IncorrectTaskStatusException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.finish(user, endTime);
    }

    public void failTask(String taskName, User user, Time endTime) throws TaskNotFoundException, IncorrectTaskStatusException {
        Task task = getTask(taskName);
        if (task == null) {
            throw new TaskNotFoundException();
        }
        task.fail(user, endTime);
    }
}
