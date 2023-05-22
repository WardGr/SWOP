package Domain;

 import Domain.Command.Command;
 import Domain.Command.CommandData;
 import Domain.Command.UndoNotPossibleException;
 import Domain.TaskStates.IncorrectRoleException;
 import Domain.TaskStates.LoopDependencyGraphException;

 import java.util.LinkedList;
 import java.util.List;
 import java.util.Stack;

public class CommandManager implements CommandInterface {

    private final Stack<Tuple<Command,User>> executedCommandStack = new Stack<>();
    private final Stack<Tuple<Command,User>> undoneCommandStack = new Stack<>();

    public CommandManager() {}

    public Stack<Tuple<Command,User>> getExecutedStack() {
        return executedCommandStack;
    }

    public Stack<Tuple<Command,User>> getUndoneStack() {
        return undoneCommandStack;
    }


    public void addExecutedCommand(Command command, User executingUser) {
        getExecutedStack().push(new Tuple<>(command, executingUser));
        if (getExecutedStack().size() > 10){
            getExecutedStack().remove(0);
        }
        getUndoneStack().clear();
    }

    public void undoLastCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException, UndoNotPossibleException {
        if (getExecutedStack().empty()) {
            throw new EmptyCommandStackException("There are no executed actions to undo");
        }
        Tuple<Command,User> previousCommandTuple = getExecutedStack().peek();
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
        getExecutedStack().pop();
        getUndoneStack().push(previousCommandTuple);
    }

    public void redoLast(User currentUser) throws EmptyCommandStackException, IncorrectUserException {
        if (getUndoneStack().empty()) {
            throw new EmptyCommandStackException("There are no undone actions to redo");
        }
        Tuple<Command,User> previousUndoneCommandTuple = getUndoneStack().peek();
        if (previousUndoneCommandTuple.getSecond() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to redo the last undone action");
        }
        try{
            previousUndoneCommandTuple.getFirst().execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        getUndoneStack().pop();
        getExecutedStack().push(previousUndoneCommandTuple);
    }

    public List<Tuple<CommandData,String>> getPreviousCommandsList(){
        List<Tuple<CommandData,String>> prevCmdList = new LinkedList<>();
        for (Tuple<Command,User> prevCmd : getExecutedStack()){
            prevCmdList.add(new Tuple<>(prevCmd.getFirst().getCommandData(), prevCmd.getSecond().getUsername()));
        }
        return prevCmdList;
    }

    public List<Tuple<CommandData,String>> getUndoneCommandsList() {
        List<Tuple<CommandData, String>> undoneCmdList = new LinkedList<>();
        for (Tuple<Command,User> undoneCmd : getUndoneStack()){
            undoneCmdList.add(new Tuple<>(undoneCmd.getFirst().getCommandData(), undoneCmd.getSecond().getUsername()));
        }
        return undoneCmdList;
    }

    public CommandData getLastExecutedCommand(){
        return getExecutedStack().peek().getFirst().getCommandData();
    }

    public CommandData getLastUndoneCommand(){
        return getUndoneStack().peek().getFirst().getCommandData();
    }
}