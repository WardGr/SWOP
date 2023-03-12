import java.util.*;

public class TaskManSystem {

  private List<Project> projects;
  private Time systemTime;

  public TaskManSystem(Time systemTime) {
    this.systemTime = systemTime;
    // TODO deze werken niet meer door het toevoegen van user in task !!!
    /* Test Tasks for now :)

        Project project1 = new Project("Project x", "Cool project", new Time(0), new Time(1000));
        Project project2 = new Project("Project y", "Even cooler project", new Time(200), new Time(5000));

        Task task1 = new Task("Cool task", "Do stuff", new Time(100), (float) 0.1, new ArrayList<>());
        Task task2 = new Task("Cooler task", "Do more stuff", new Time(1000), (float) 0.1, new ArrayList<>());

        //task2.status = Status.FAILED;
        task2.status = Status.EXECUTING;
        task2.timeSpan = new TimeSpan(new Time(0));
        task1.status = Status.AVAILABLE;

        project1.addTask(task1);
        project1.addTask(task2);

        project2.addTask(task1);
        project2.addTask(task2);
        */

    projects = new LinkedList<>();
    /* h
        projects.add(project1);
        projects.add(project2);

        this.projects = projects;
        */
  }

  private Project getProject(String projectName) {
    for (Project project : projects) {
      if (project.getName().equals(projectName)) {
        return project;
      }
    }
    return null;
  }

  public int getSystemHour() {
    return getSystemTime().getHour();
  }

  public int getSystemMinute() {
    return getSystemTime().getMinute();
  }

  private Time getSystemTime() {
    return systemTime;
  }

  public List<String> getProjectNames() {
    List<String> names = new LinkedList<>();
    for (Project project : projects) {
      names.add(project.getName());
    }
    return names;
  }

  public List<String> getStatuses() {
    List<String> statuses = new LinkedList<>();
    for (Project project : projects) {
      statuses.add(project.getStatus());
    }
    return statuses;
  }

  public String showProject(String projectName)
    throws ProjectNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.toString();
  }

  public String showTask(String projectName, String taskName)
    throws ProjectNotFoundException, TaskNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.showTask(taskName);
  }

  public void createProject(
    String projectName,
    String projectDescription,
    int dueHour,
    int dueMinute
  )
    throws DueBeforeSystemTimeException, NotValidTimeException, ProjectNameAlreadyInUseException {
    if (getProject(projectName) == null) {
      Project newProject = new Project(
        projectName,
        projectDescription,
        getSystemTime(),
        new Time(dueHour, dueMinute)
      );
      projects.add(newProject);
    } else {
      throw new ProjectNameAlreadyInUseException();
    }
  }

  public void addTaskToProject(
    String projectName,
    String taskName,
    String description,
    int durationHour,
    int durationMinute,
    double deviation,
    List<String> previousTasks,
    User currentUser
  )
    throws ProjectNotFoundException, TaskNotFoundException, NotValidTimeException, TaskNameAlreadyInUseException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.addTask(
      taskName,
      description,
      new Time(durationHour, durationMinute),
      deviation,
      previousTasks,
      currentUser
    );
  }

  public void addAlternativeTaskToProject(
    String projectName,
    String taskName,
    String description,
    int durationHour,
    int durationMinute,
    double deviation,
    String replaces
  )
    throws ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException, NotValidTimeException, TaskNameAlreadyInUseException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.addAlternativeTask(
      taskName,
      description,
      new Time(durationHour, durationMinute),
      deviation,
      replaces
    );
  }

  public List<Map.Entry<String, String>> showAvailableTasks() {
    List<Map.Entry<String, String>> availableTasks = new ArrayList<>();
    for (Project project : projects) {
      List<String> tasks = project.showAvailableTasks();
      String projectName = project.getName();
      for (String task : tasks) {
        availableTasks.add(new AbstractMap.SimpleEntry<>(projectName, task));
      }
    }
    return availableTasks;
  }

  public List<Map.Entry<String, String>> showExecutingTasks() {
    List<Map.Entry<String, String>> executingTasks = new ArrayList<>();
    for (Project project : projects) {
      List<String> tasks = project.showExecutingTasks();
      String projectName = project.getName();
      for (String task : tasks) {
        executingTasks.add(new AbstractMap.SimpleEntry<>(projectName, task));
      }
    }
    return executingTasks;
  }

  public List<Status> getNextStatuses(String projectName, String taskName)
    throws ProjectNotFoundException, TaskNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.getNextStatuses(taskName);
  }

  public Status getStatus(String projectName, String taskName)
    throws ProjectNotFoundException, TaskNotFoundException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    return project.getStatus(taskName);
  }

  public void startTask(
    String projectName,
    String taskName,
    int startHour,
    int startMinute,
    User currentUser
  )
    throws ProjectNotFoundException, TaskNotFoundException, NotValidTimeException, UserNotAllowedToChangeTaskException, WrongTaskStatusException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.startTask(
      taskName,
      new Time(startHour, startMinute),
      getSystemTime(),
      currentUser
    );
  }

  public void endTask(
    String projectName,
    String taskName,
    Status newStatus,
    int endHour,
    int endMinute,
    User currentUser
  )
    throws ProjectNotFoundException, TaskNotFoundException, NotValidTimeException, FailTimeAfterSystemTimeException, UserNotAllowedToChangeTaskException, WrongTaskStatusException {
    Project project = getProject(projectName);
    if (project == null) {
      throw new ProjectNotFoundException();
    }
    project.endTask(
      taskName,
      newStatus,
      new Time(endHour, endMinute),
      getSystemTime(),
      currentUser
    );
  }

  public void advanceTime(int newHour, int newMinute)
    throws NewTimeBeforeSystemTimeException, NotValidTimeException {
    Time newTime = new Time(newHour, newMinute);
    if (newTime.before(systemTime)) {
      throw new NewTimeBeforeSystemTimeException();
    }
    for (Project project : projects) {
      project.advanceTime(newTime);
    }
    systemTime = newTime;
  }
}
