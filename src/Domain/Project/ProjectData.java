package Domain.Project;

import Domain.DataClasses.Time;
import Domain.Task.Status;
import Domain.Task.TaskData;

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
     * @return The projects' status (ONGOING or FINISHED)
     */
    ProjectStatus getStatus();

    /**
     * @return  A list of data of all tasks in this project
     */
    List<TaskData> getTasksData();

    /**
     * @return  A list of data of all tasks in this project that have been replaced
     */
    List<TaskData> getReplacedTasksData();

    /**
     * @return  A list of data of all tasks in this project that are available or pending
     */
    default List<TaskData> getAvailableAndPendingTasksData() {
        return getTasksData().stream().filter(t -> t.getStatus() == Status.AVAILABLE || t.getStatus() == Status.PENDING).toList();
    }

    /**
     * @return   A list of data of all tasks in this project that are replaceable
     */
    default List<TaskData> getReplaceableTasksData() {
        return getTasksData().stream().filter(t -> t.getStatus() == Status.FAILED).toList();
    }

    /**
     * Returns a list of all tasks that can be added as a previous task to the given taskData object without creating a loop in the dependency graph
     * @param taskData  The taskData object to check
     */
    default List<TaskData> getPossiblePrevTasks(TaskData taskData) {
        return getTasksData().stream().filter(t -> !taskData.getPrevTasksData().contains(t) &&
                taskData.canSafelyAddPrevTask(t)).toList();
    }

    /**
     * Returns a list of all tasks that can be added as a next task to the given taskData object without creating a loop in the dependency graph
     * @param taskData  The taskData object to check
     */
    default List<TaskData> getPossibleNextTasks(TaskData taskData) {
        return getTasksData().stream().filter(t -> !taskData.getNextTasksData().contains(t) &&
                t.canSafelyAddPrevTask(taskData)).toList();
    }

    /**
     * @return  The total amount of tasks in this project
     */
    default int getTotalTaskCount() {
        return getTasksData().size() + getReplacedTasksData().size();
    }


}
