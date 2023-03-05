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

    // TODO: MOET DIT MISSCHIEN IN DE CONTROLLER? IS EIGENLIJK VERTALING...



    // TODO: FOR THIS TO WORK ALL PROJECTS MUST HAVE A DIFFERENT NAME, CHECK THIS ON PROJECT CREATION?
    public Project getProject(String selectedProjectName) {
        for (Project project : projects) {
            if (project.getName().equals(selectedProjectName)) {
                return project;
            }
        }
        return null;
    }

    public List<Project> getProjects() {
        return List.copyOf(projects); // copy want reference exposure x
    }
}
