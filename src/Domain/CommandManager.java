package Domain;

 import Domain.Command.Command;
 import Domain.Command.CommandData;
 import Domain.Command.UndoNotPossibleException;
 import Domain.TaskStates.IncorrectRoleException;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Stack;

public class CommandManager {

    private final Stack<Tuple<Command,User>> executedCommandStack = new Stack<>();
    private final Stack<Tuple<Command,User>> undoneCommandStack = new Stack<>();

    public CommandManager() {}

    public void addExecutedCommand(Command command, User executingUser) {
        executedCommandStack.push(new Tuple<>(command, executingUser));
        if (executedCommandStack.size() > 10){
            executedCommandStack.remove(0);
        }
        undoneCommandStack.clear();
    }

    public void undoLastCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException, UndoNotPossibleException {
        if (executedCommandStack.empty()) {
            throw new EmptyCommandStackException("There are no executed actions to undo");
        }
        Tuple<Command,User> previousCommandTuple = executedCommandStack.peek();
        if (previousCommandTuple.getSecond() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to undo the last executed action");
        }
        try {
            previousCommandTuple.getFirst().undo();
        } catch (ProjectNotFoundException | EndTimeBeforeStartTimeException | TaskNotFoundException |
                 IncorrectTaskStatusException | UserAlreadyAssignedToTaskException | LoopDependencyGraphException |
                 IncorrectRoleException e) {
            throw new RuntimeException(e);
        }
        executedCommandStack.pop();
        undoneCommandStack.push(previousCommandTuple);
    }

    public void redoLastUndoneCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException {
        if (undoneCommandStack.empty()) {
            throw new EmptyCommandStackException("There are no undone actions to redo");
        }
        Tuple<Command,User> previousUndoneCommandTuple = undoneCommandStack.peek();
        if (previousUndoneCommandTuple.getSecond() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to redo the last undone action");
        }
        try{
            previousUndoneCommandTuple.getFirst().execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        undoneCommandStack.pop();
        executedCommandStack.push(previousUndoneCommandTuple);
    }

    public List<Tuple<CommandData,String>> getPreviousCommandsList(){
        List<Tuple<CommandData,String>> prevCmdList = new LinkedList<>();
        for (Tuple<Command,User> prevCmd : executedCommandStack){
            prevCmdList.add(new Tuple<>(prevCmd.getFirst().getCommandData(), prevCmd.getSecond().getUsername()));
        }
        return prevCmdList;
    }

    public List<Tuple<CommandData,String>> getUndoneCommandsList() {
        List<Tuple<CommandData, String>> undoneCmdList = new LinkedList<>();
        for (Tuple<Command,User> undoneCmd : undoneCommandStack){
            undoneCmdList.add(new Tuple<>(undoneCmd.getFirst().getCommandData(), undoneCmd.getSecond().getUsername()));
        }
        return undoneCmdList;
    }

    public CommandData getLastCommand(){
        return executedCommandStack.peek().getFirst().getCommandData();
    }

    public CommandData getLastUndoneCommand(){
        return undoneCommandStack.peek().getFirst().getCommandData();
    }
}