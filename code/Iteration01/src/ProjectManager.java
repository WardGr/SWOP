import java.util.LinkedList;
import java.util.List;

public class ProjectManager {
    private List<Project> projects;

    public ProjectManager() {
        // Test Tasks for now :)

        Project project1 = new Project("Project x", "Cool project", 0, 1000);
        Project project2 = new Project("Project y", "Even cooler project", 200, 5000);
        LinkedList<Project> projects = new LinkedList<>();
        projects.add(project1);
        projects.add(project2);

        this.projects = projects;
    }

    public void addProject(String projectName, String projectDescription, int dueTime, int systemTime) {
        if(dueTime <= systemTime) {
            throw new RuntimeException();
        }
        Project newProject = new Project(projectName, projectDescription, systemTime, dueTime);
        projects.add(newProject);

    }


    /**
     * Returns the project corresponding to the given project name, if no project matches, returns null.
     * @param selectedProjectName user input that may correspond to an existing project
     * @return if the given project name corresponds to a project, this project, else null
     */
    public Project getProject(String selectedProjectName) {
        for (Project project : projects) {
            if (project.getName().equals(selectedProjectName)) {
                return project;
            }
        }
        return null;
        /* TODO: FOR THIS TO WORK ALL PROJECTS MUST HAVE A DIFFERENT NAME, OTHERWISE SOME PROJECTS MAY NEVER BE SELECTED,
         *  CHECK THIS ON PROJECT CREATION?
         */
    }

    /**
     * @return A copy of all projects.
     */
    public List<Project> getProjects() {
        return List.copyOf(projects); // copy want reference exposure x
    }

    /**
     * Adds a project to the project linked list.
     * @param project The project to be added
     */
    public void addProject(Project project) {
        projects.add(project);
    }

    public void addTaskToProject(String projectName, String taskName, String description, int duration, int deviation) {
        Project project = getProject(projectName);
        if (project == null) { // TODO: we zijn door elkaar met exceptions en null-checks aan het werken, we zouden best 1 van de twee beslissen (mijn voorkeur gaat naar custom exceptions, bijvoorbeeld een "InvalidProjectName" exception enzo, die dan gehandelt wordt door de UI)
            throw new RuntimeException();
        }
        project.addTask(taskName, description, duration, deviation);
        // Ge hoeft ni te catchen en dan weer te throwen, ge kunt die error gewoon laten gebeuren aangezien de controller toch al een try - catch heeft wanneer die deze functie oproept
    }
}
