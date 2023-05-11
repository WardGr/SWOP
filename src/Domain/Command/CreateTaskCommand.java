package Domain.Command;

import Application.CreateTaskController;
import Application.IncorrectPermissionException;
import Domain.*;
import Domain.TaskStates.LoopDependencyGraphException;
import Domain.TaskStates.NonDeveloperRoleException;

import java.util.List;
import java.util.Set;

public class CreateTaskCommand extends Command {
        private CreateTaskController controller;
        private String projectName;
        private String taskName;
        private String description;
        private Time durationTime;
        private double deviation;
        private List<Role> roles;
        private Set<String> previousTasks;
        private Set<String> nextTasks;

        // Ofwel zo, ofwel geven we een task en project mee, dan doen we rechtstreeks project.deletetask en project.addtask ipv nieuwe objecten te maken
        public CreateTaskCommand(CreateTaskController controller,
                                String projectName,
                                String taskName,
                                String description,
                                Time durationTime,
                                double deviation,
                                List<Role> roles,
                                Set<String> previousTasks,
                                Set<String> nextTasks) {
            this.controller = controller;
            this.projectName = projectName;
            this.taskName = taskName;
            this.description = description;
            this.durationTime = durationTime;
            this.deviation = deviation;
            this.roles = roles;
            this.previousTasks = previousTasks;
            this.nextTasks = nextTasks;
        }

        @Override
        public void undo() {
            controller.deleteTask(projectName, taskName);
        }

        @Override
        public void redo() throws UserNotFoundException, ProjectNotFoundException, InvalidTimeException, TaskNameAlreadyInUseException, TaskNotFoundException, IncorrectTaskStatusException, IncorrectPermissionException, LoopDependencyGraphException, NonDeveloperRoleException {
            controller.createTask(projectName, taskName, description, durationTime, deviation, roles, previousTasks, nextTasks);
        }

        @Override
        public String information() {
            return "create task " + taskName;
        }
}
