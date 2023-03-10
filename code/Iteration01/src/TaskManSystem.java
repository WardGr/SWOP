import java.util.*;

public class TaskManSystem {
    private List<Project> projects;

    public TaskManSystem() throws DueBeforeSystemTimeException {
        // Test Tasks for now :)

        Project project1 = new Project("Project x", "Cool project", new Time(0), new Time(1000));
        Project project2 = new Project("Project y", "Even cooler project", new Time(200), new Time(5000));

        Task task1 = new Task("Cool task", "Do stuff", new Time(100), (float) 0.1, new ArrayList<>());
        Task task2 = new Task("Cooler task", "Do more stuff", new Time(1000), (float) 0.1, new ArrayList<>());

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
        return null; //TODO is het niet beter met exception?
    }

    //TODO: moet dit hier?? Nu doet de TaskManSystem de vertaling van project naar string?
    //        - Ik denk het ni, verander dit!
    public String showProject(String projectName) {
        Project project = getProject(projectName);
        if (project == null) {
            return "Project not found"; // Beter gewoon zoiets teruggeven dan een hele exception te throwen lijkt mij
        }
        return project.toString();
    }

    public String showTask(String projectName, String taskName) {
        Project project = getProject(projectName);
        if (project == null) {
            return "Project not found";
        }

        Task task = project.getTask(taskName);
        if (task == null) {
            return "Task not found";
        }
        return task.toString(); // TODO best geen dependency maken van TaskManSystem naar Task!
    }

    public void createProject(String projectName, String projectDescription, Time systemTime, String dueTimeString) throws DueBeforeSystemTimeException {
        // TODO: doe deze parsing in UI zodat duetime al een integer is!
        Time dueTime = new Time(Integer.parseInt(dueTimeString));
        Project newProject = new Project(projectName, projectDescription, systemTime, dueTime);
        projects.add(newProject);
    }

    public void addTaskToProject(String projectName, String taskName, String description, Time duration, float deviation, List<String> previousTasks){
        Project project = getProject(projectName);
        if (project == null) {
            return;// TODO
        }
        project.addTask(taskName, description, duration, deviation, previousTasks);
    }

    public Task getTask(String projectName, String taskName) {
        Project project = getProject(projectName);
        if (project == null) {
            return null; // TODO
        }
        else {
            return project.getTask(taskName);
        }
    }


    public void addAlternativeTaskToProject(String projectName, String taskName, String description, Time duration, float deviation, String replaces) {
        Project project = getProject(projectName);
        if (project == null) {
            return; // TODO
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
