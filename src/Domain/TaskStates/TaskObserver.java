package Domain.TaskStates;

public interface TaskObserver {
    default void update(Task task){}
}
