package Domain.TaskManSystem;

import Domain.DataClasses.Time;
import Domain.Project.ProjectData;
import Domain.Project.ProjectStatus;

import java.util.List;

/**
 * Read-only data interface for TaskManSystem
 */
public interface TaskManSystemData {

    default TaskManSystemData getTaskManSystemData(){
        return this;
    }

    /**
     * @return The system time of the corresponding TaskManSystem
     */
    Time getSystemTime();

    /**
     * @return  A list of all projects data belonging to the current TaskManSystem
     */
    List<ProjectData> getProjectsData();

    /**
     * @return  A list of all projects data belonging to the current TaskManSystem
     */
    default List<ProjectData> getOngoingProjectsData() {
        return getProjectsData().stream().filter(p -> p.getStatus() == ProjectStatus.ONGOING).toList();
    }
}
