package Domain;

import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.Task;
import Domain.TaskStates.TaskProxy;

import java.util.HashSet;
import java.util.Set;

/**
 * A user currently registered within the system
 */
public class User {

    private final String username;
    private final String password;
    private final Set<Role> roles;

    private Task task;

    public User(String username, String password, Set<Role> roles) {
        if (username == null || password == null || roles == null || roles.size() == 0) {
            throw new IllegalArgumentException("Username, password and roles have to be initiated");
        }
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>(roles);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }

    private Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public TaskProxy getTaskData() {
        if (getTask() == null) {
            return null;
        } else {
            return getTask().getTaskProxy();
        }
    }

    public void assignTask(Task task, Role role) throws IncorrectTaskStatusException, IncorrectRoleException, UserAlreadyAssignedToTaskException {
        if (!getRoles().contains(role)) {
            throw new IncorrectRoleException("User does not have the given role");
        }
        if (getTask() != null) {
            if (getTaskData().getStatus() == Status.PENDING) {
                // We will now switch tasks
                getTask().unassignUser(this);
            } else {
                throw new UserAlreadyAssignedToTaskException();
            }
        }
        setTask(task);
    }

    public void endTask() {
        setTask(null);
    }
}
