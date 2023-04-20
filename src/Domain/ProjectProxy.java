package Domain;

import java.util.List;

/**
 * Read-only proxy for a project, containing specific details about this project
 */
public class ProjectProxy {
    private final Project project;

    /**
     * @param project The project to create a proxy for
     */
    public ProjectProxy(Project project) {
        this.project = project;
    }

    /**
     * @return the project this object is a proxy of
     */
    private Project getProject() {
        return project;
    }

    /**
     * @return A string containing the projects' name
     */
    public String getName() {
        return getProject().getName();
    }

    /**
     * @return A string containing the projects' description
     */
    public String getDescription() {
        return getProject().getDescription();
    }

    /**
     * @return A time object depicting the creation time of this project
     */
    public Time getCreationTime() {
        return getProject().getCreationTime();
    }

    /**
     * @return A time object depicting the due time of this project
     */
    public Time getDueTime() {
        return getProject().getDueTime();
    }

    /**
     * @return A list of names of all tasks currently active (= not replaced) in the project
     */
    public List<String> getActiveTasksNames() {
        return getProject().getActiveTasksNames();
    }

    /**
     * @return A list of all tasks that have been replaced in the project
     */
    public List<String> getReplacedTasksNames() {
        return getProject().getReplacedTasksNames();
    }

    /**
     * @return The projects' status (ONGOING or FINISHED)
     */
    public ProjectStatus getStatus() {
        return getProject().getStatus();
    }
}
