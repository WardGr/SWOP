package Domain.User;

import Domain.Task.IncorrectTaskStatusException;
import Domain.Task.IncorrectRoleException;
import Domain.Task.Task;
import Domain.Task.TaskData;

import java.util.HashSet;
import java.util.Set;

/**
 * A user object, including a list of its roles
 */
public class User {

    private final String username;
    private final String password;
    private final Set<Role> roles;

    private Task task;

    /**
     * Creates a new User object with the given
     *
     * @param username Username of the new User
     * @param password Password of the new User
     * @param roles    Set of roles of the new User
     * @throws IllegalArgumentException if the given username, password or set of roles are null, or if the set of roles is empty
     */
    public User(String username, String password, Set<Role> roles) {
        if (username == null || password == null || roles == null || roles.size() == 0) {
            throw new IllegalArgumentException("Username, password and roles have to be initiated");
        }
        this.username = username;
        this.password = password;
        this.roles = new HashSet<>(roles);
    }

    /**
     * @return String containing this users' username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return String containing this users' password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return A set of all roles this user has
     */
    public Set<Role> getRoles() {
        return new HashSet<>(roles);
    }

    /**
     * @return Returns this users' assigned task
     */
    private Task getTask() {
        return task;
    }

    /**
     * @param task The task to set as this users' assigned task
     */
    private void setTask(Task task) {
        this.task = task;
    }

    /**
     * @return Retrives a read-only task proxy containing this users' assigned task details
     */
    public TaskData getTaskData() {
        if (getTask() == null) {
            return null;
        } else {
            return getTask().getTaskData();
        }
    }

    /**
     * Assigns a new task to this user
     *
     * @param task The task to assign to this user
     * @param role The role this user wants to use for the task
     * @throws IncorrectRoleException             if the user does not have the given role
     * @throws UserAlreadyAssignedToTaskException if the user is already assigned to the task
     */
    public void assignTask(Task task, Role role) throws IncorrectRoleException, UserAlreadyAssignedToTaskException {
        if (!getRoles().contains(role)) {
            throw new IncorrectRoleException("User does not have the given role");
        }
        if (getTask() != null) {
            try {
                getTask().unassignUser(this);
            } catch (IncorrectTaskStatusException e) {
                throw new UserAlreadyAssignedToTaskException();
            }
        }
        setTask(task);
    }

    /**
     * Unassigns this user from its task
     *
     * @post The user is not assigned to a task
     */
    public void endTask() {
        setTask(null);
    }
}