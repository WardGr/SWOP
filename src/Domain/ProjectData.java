package Domain;

import java.util.List;

/**
 * Read-only data interface for a project, containing specific details about this project
 */
public interface ProjectData {

    /**
     * @return the projectData object
     */
    default ProjectData getProjectData() {
        return this;
    }

    /**
     * @return A string containing the projects' name
     */
    String getName();

    /**
     * @return A string containing the projects' description
     */
    String getDescription();

    /**
     * @return A time object depicting the creation time of this project
     */
    Time getCreationTime();

    /**
     * @return A time object depicting the due time of this project
     */
    Time getDueTime();

    /**
     * @return A list of names of all tasks currently active (= not replaced) in the project
     */
    List<String> getActiveTasksNames();

    /**
     * @return A list of all tasks that have been replaced in the project
     */
    List<String> getReplacedTasksNames();

    /**
     * @return The projects' status (ONGOING or FINISHED)
     */
    ProjectStatus getStatus();
}
