package Application;
import Domain.CommandHandler;
import Domain.Role;
import Domain.Time;

import java.util.List;
import java.util.Set;

public class CommandController {
    private Session session;
    private CommandHandler cmdHandler;

    public CommandController(Session session) {
        this.session = session;
    }

    public void createProject(ProjectController controller, String projectName, String projectDescription, Time start, Time end) throws Exception { // Fixen dat we controller meegeven?
        cmdHandler.addCreateProject(controller, projectName, projectDescription, start, end);
    }

    public void createTask(CreateTaskController controller,
                           String projectName,
                           String taskName,
                           String description,
                           Time durationTime,
                           double deviation,
                           List<Role> roles,
                           Set<String> previousTasks,
                           Set<String> nextTasks) throws Exception {
        cmdHandler.addCreateTask(controller, projectName, taskName, description, durationTime, deviation, roles, previousTasks, nextTasks);
    }


}
