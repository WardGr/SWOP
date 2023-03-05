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
}
