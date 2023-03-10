import java.util.*;

public class TaskManSystem {
    private List<Project> projects;

    public TaskManSystem() throws DueBeforeSystemTimeException {
        // Test Tasks for now :)

        Project project1 = new Project("Project x", "Cool project", new Time(0), new Time(1000));
        Project project2 = new Project("Project y", "Even cooler project", new Time(200), new Time(5000));

        Task task1 = new Task("Cool task", "Do stuff", new Time(100), (float) 0.1, new ArrayList<>());
        Task task2 = new Task("Cooler task", "Do more stuff", new Time(1000), (float) 0.1, new ArrayList<>());

        task2.status = Status.FAILED;

        project1.addTask(task1);
        project1.addTask(task2);

        project2.addTask(task1); // TODO nu zijn die tasks van verschillende projecten verbonden hahah, mss ff een clone doen
        project2.addTask(task2);


        LinkedList<Project> projects = new LinkedList<>();
        projects.add(project1);
        projects.add(project2);

        this.projects = projects;
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

    private Project getProject(String projectName) {
        for (Project project : projects) {
            if (project.getName().equals(projectName)) {
                return project;
            }
        }
        return null; //TODO is het niet beter met exception? JA!!!!!!!!!!!!!!!!!!!!!
    }

    public String showProject(String projectName) throws ProjectNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.toString();
    }

    public String showTask(String projectName, String taskName) throws ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        return project.showTask(taskName);
    }

    public void createProject(String projectName, String projectDescription, Time systemTime, Time dueTime) throws DueBeforeSystemTimeException {
        Project newProject = new Project(projectName, projectDescription, systemTime, dueTime);
        projects.add(newProject);
    }

    public void addTaskToProject(String projectName, String taskName, String description, Time duration, double deviation, List<String> previousTasks) throws ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.addTask(taskName, description, duration, deviation, previousTasks);
    }


    public void addAlternativeTaskToProject(String projectName, String taskName, String description, Time duration, double deviation, String replaces) throws ReplacedTaskNotFailedException, ProjectNotFoundException, TaskNotFoundException {
        Project project = getProject(projectName);
        if (project == null) {
            throw new ProjectNotFoundException();
        }
        project.addAlternativeTask(taskName, description, duration, deviation, replaces);
    }

    public List<Map.Entry<String,String>> showAvailableTasks(){
        List<Map.Entry<String,String>> availableTasks = new ArrayList<>();
        for (Project project : projects){
            List<String> tasks = project.showAvailableTasks();
            String projectName = project.getName();
            for (String task : tasks){
                availableTasks.add(new AbstractMap.SimpleEntry<>(projectName,task));
            }
        }
        return availableTasks;
    }

    public List<Map.Entry<String,String>> showExecutingTasks(){
        List<Map.Entry<String,String>> executingTasks = new ArrayList<>();
        for (Project project : projects){
            List<String> tasks = project.showExecutingTasks();
            String projectName = project.getName();
            for (String task : tasks){
                executingTasks.add(new AbstractMap.SimpleEntry<>(projectName,task));
            }
        }
        return executingTasks;
    }
}
