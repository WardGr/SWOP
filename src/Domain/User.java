package Domain;

import Domain.TaskStates.Task;
import Domain.TaskStates.TaskObserver;
import Domain.TaskStates.TaskProxy;

import java.util.HashSet;
import java.util.Set;

/**
 * A user currently registered within the system
 */
public class User implements TaskObserver {

    private final String username;
    private final String password;
    private final Set<Role> roles;

    private Task pendingTask;
    private Task executingTask;

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

    private Task getPendingTask(){
        return pendingTask;
    }

    private void setPendingTask(Task task){
        pendingTask = task;
    }

    private Task getExecutingTask(){
        return executingTask;
    }

    private void setExecutingTask(Task task){
        executingTask = task;
    }

    public TaskProxy getPendingTaskData() {
        if (getPendingTask() == null){
            return null;
        } else {
            return getPendingTask().getTaskData();
        }
    }

    public TaskProxy getExecutingTaskData() {
        if (getExecutingTask() == null){
            return null;
        } else {
            return getExecutingTask().getTaskData();
        }
    }

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
}
