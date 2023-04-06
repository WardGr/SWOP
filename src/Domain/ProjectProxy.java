package Domain;

import java.util.List;

public class ProjectProxy {
    private final Project project;

    public ProjectProxy(Project project){
        this.project = project;
    }

    private Project getProject(){
        return project;
    }

    public String getName(){
        return getProject().getName();
    }

    public String getDescription(){
        return getProject().getDescription();
    }

    public Time getCreationTime(){
        return getProject().getCreationTime();
    }
    public Time getDueTime(){
        return getProject().getDueTime();
    }

    public List<String> getTasksNames(){
        return getProject().getTasksNames();
    }

    public ProjectStatus getStatus(){
        return getProject().getStatus();
    }
}
