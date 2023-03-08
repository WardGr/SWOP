import java.util.LinkedList;
import java.util.List;

public class TaskManSystem {
    private List<Project> projects;

    public TaskManSystem() throws DueBeforeSystemTimeException {
        // Test Tasks for now :)

        Project project1 = new Project("Project x", "Cool project", new Time(0), new Time(1000));
        Project project2 = new Project("Project y", "Even cooler project", new Time(200), new Time(5000));
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
        return null;
    }

    //TODO: moet dit hier?? Nu doet de TaskManSystem de vertaling van project naar string?
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
        return task.toString();
    }

    public void createProject(String projectName, String projectDescription, Time systemTime, String dueTimeString) throws DueBeforeSystemTimeException {
        Time dueTime = new Time(Integer.parseInt(dueTimeString));
        Project newProject = new Project(projectName, projectDescription, systemTime, dueTime);
        projects.add(newProject);
    }
}
