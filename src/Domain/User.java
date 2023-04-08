package Domain;

import Domain.TaskStates.IncorrectRoleException;
import Domain.TaskStates.Task;
import Domain.TaskStates.TaskObserver;
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

    private Task getTask(){
        return task;
    }

    private void setTask(Task task){
        this.task = task;
    }

    public TaskProxy getTaskData() {
        if (getTask() == null){
            return null;
        } else {
            return getTask().getTaskData();
        }
    }

    /*
    public void update(Task task){
        if (task.getStatus() == Status.PENDING){
            if (getPendingTask() != null && getPendingTask() != task) {
                try {
                    getPendingTask().stopPending(this);
                } catch (IncorrectTaskStatusException e) {
                    throw new RuntimeException(e); // TODO deze mag eigenlijk echt niet!
                }
            }
            setPendingTask(task);
            setExecutingTask(null);

        } else if (task.getStatus() == Status.EXECUTING) {
            setPendingTask(null);
            setExecutingTask(task);
        } else if (task.getStatus() == Status.FINISHED || task.getStatus() == Status.FAILED){
            setPendingTask(null);
            setExecutingTask(null);
        }
    }
    */

    public void startTask(Task task, Role role) throws IncorrectTaskStatusException, IncorrectRoleException {
        if (!getRoles().contains(role)){
            throw new IncorrectRoleException("User does not have the given role");
        }
        if (getTask() != null) {
            if (getTaskData().getStatus() == Status.PENDING) {
                getTask().stopPending(this);
            } else {
                throw new RuntimeException();
                // TODO exception
            }
        }
        setTask(task);
    }

    public void endTask(){
        setTask(null);
    }
}
