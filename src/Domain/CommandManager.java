package Domain;

 import Domain.Command.Command;
 import Domain.Command.CommandData;
 import Domain.Command.UndoNotPossibleException;

 import java.util.LinkedList;
 import java.util.List;
 import java.util.Stack;

public class CommandManager implements CommandInterface {

    private final Stack<Tuple<Command,User>> executedCommandStack = new Stack<>();
    private final Stack<Tuple<Command,User>> undoneCommandStack = new Stack<>();

    public CommandManager() {}

    private Stack<Tuple<Command,User>> getExecutedStack() {
        return executedCommandStack;
    }

    private void addLastExecutedCommand(Command command, User executingUser){
        executedCommandStack.push(new Tuple<>(command, executingUser));
    }

    private void trimExecutedCommandList(int size){
        while(executedCommandStack.size() > size){
            executedCommandStack.remove(0);
        }
    }

    private Command getLastExecutedCommand(){
        if (getExecutedStack().empty()){
            return null;
        }
        Tuple<Command,User> command = executedCommandStack.peek();
        return command.getFirst();
    }

    private User getUserLastExecutedCommand(){
        Tuple<Command,User> command = executedCommandStack.peek();
        return command.getSecond();
    }

    private void removeLastExecutedCommand(){
        executedCommandStack.pop();
    }

    private Stack<Tuple<Command,User>> getUndoneStack() {
        return undoneCommandStack;
    }

    private void addUndoneCommand(Command command, User executingUser){
        undoneCommandStack.push(new Tuple<>(command, executingUser));
    }

    private Command getLastUndoneCommand(){
        if (getUndoneStack().empty()){
            return null;
        }
        Tuple<Command,User> command = undoneCommandStack.peek();
        return command.getFirst();
    }

    private User getUserLastUndoneCommand(){
        Tuple<Command,User> command = undoneCommandStack.peek();
        return command.getSecond();
    }

    private void removeLastUndoneCommand(){
        undoneCommandStack.pop();
    }

    private void clearUndoneCommands(){
        undoneCommandStack.clear();
    }



    public void addExecutedCommand(Command command, User executingUser) {
        addLastExecutedCommand(command, executingUser);
        trimExecutedCommandList(10);
        clearUndoneCommands();
    }

    public void undoLastCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException, UndoNotPossibleException {
        if (getExecutedStack().empty()) {
            throw new EmptyCommandStackException("There are no executed actions to undo");
        }
        if (getUserLastExecutedCommand() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to undo the last executed action");
        }
        try {
            getLastExecutedCommand().undo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        addUndoneCommand(getLastExecutedCommand(), getUserLastExecutedCommand());
        removeLastExecutedCommand();
    }

    public void redoLast(User currentUser) throws EmptyCommandStackException, IncorrectUserException {
        if (getUndoneStack().empty()) {
            throw new EmptyCommandStackException("There are no undone actions to redo");
        }
        if (getUserLastUndoneCommand() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to redo the last undone action");
        }
        try{
            getLastUndoneCommand().execute();
        } catch (Exception e) {
            throw new RuntimeException();
        }
        addLastExecutedCommand(getLastUndoneCommand(), getUserLastUndoneCommand());
        removeLastUndoneCommand();
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

    public CommandData getLastExecutedCommandData(){
        if (getLastExecutedCommand() == null){
            return null;
        }
        return getLastExecutedCommand().getCommandData();
    }

    public CommandData getLastUndoneCommandData(){
        if (getLastUndoneCommand() == null){
            return null;
        }
        return getLastUndoneCommand().getCommandData();
    }
}