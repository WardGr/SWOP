package Domain.Command;

 import Domain.DataClasses.Tuple;
 import Domain.User.IncorrectUserException;
 import Domain.User.Role;
 import Domain.User.User;

 import java.util.LinkedList;
 import java.util.List;
 import java.util.Stack;

/**
 * Class that manages the commands executed by the users
 * manages the undo and redo functionality and the command history
 */
public class CommandManager implements CommandInterface {

    private final Stack<Tuple<Command, User>> executedCommandStack = new Stack<>();
    private final Stack<Tuple<Command,User>> undoneCommandStack = new Stack<>();

    public CommandManager() {}

    private Stack<Tuple<Command,User>> getExecutedStack() {
        return executedCommandStack;
    }
    private Stack<Tuple<Command,User>> getUndoneStack() {
        return undoneCommandStack;
    }


    private void addLastExecutedCommand(Command command, User executingUser){
        getExecutedStack().push(new Tuple<>(command, executingUser));
    }

    private void trimExecutedCommandList(){
        while(getExecutedStack().size() > 10){
            getExecutedStack().remove(0);
        }
    }

    private Command getLastExecutedCommand(){
        if (getExecutedStack().empty()){
            return null;
        }
        Tuple<Command,User> command = getExecutedStack().peek();
        return command.getFirst();
    }

    private User getUserLastExecutedCommand(){
        Tuple<Command,User> command = getExecutedStack().peek();
        return command.getSecond();
    }

    private void removeLastExecutedCommand(){
        getExecutedStack().pop();
    }


    private void addUndoneCommand(Command command, User executingUser){
        getUndoneStack().push(new Tuple<>(command, executingUser));
    }

    private Command getLastUndoneCommand(){
        if (getUndoneStack().empty()){
            return null;
        }
        Tuple<Command,User> command = getUndoneStack().peek();
        return command.getFirst();
    }

    private User getUserLastUndoneCommand(){
        Tuple<Command,User> command = getUndoneStack().peek();
        return command.getSecond();
    }

    private void removeLastUndoneCommand(){
        getUndoneStack().pop();
    }

    private void clearUndoneCommands(){
        getUndoneStack().clear();
    }


    /**
     * Adds a command to the list of executed commands
     *
     * @param command           The command to be executed
     * @param executingUser     The user that executed the command
     */
    public void addExecutedCommand(Command command, User executingUser) {
        addLastExecutedCommand(command, executingUser);
        trimExecutedCommandList();
        clearUndoneCommands();
    }

    /**
     * Undoes the last command executed by the current user
     *
     * @param currentUser                   The user that wants to undo the last command
     * @throws EmptyCommandStackException   If there are no commands to undo
     * @throws IncorrectUserException       If the current user is not the user that executed the last command and is not a project manager
     * @throws UndoNotPossibleException     If the last executed command cannot be undone
     */
    public void undoLastCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException, UndoNotPossibleException {
        if (getExecutedStack().empty()) {
            throw new EmptyCommandStackException("There are no executed actions to undo");
        }
        if (getUserLastExecutedCommand() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to undo the last executed action");
        }
        getLastExecutedCommand().undo();
        addUndoneCommand(getLastExecutedCommand(), getUserLastExecutedCommand());
        removeLastExecutedCommand();
    }

    /**
     * Redoes the last command undone by the current user
     *
     * @param currentUser                   The user that wants to redo the last command
     * @throws EmptyCommandStackException   If there are no commands to redo
     * @throws IncorrectUserException       If the current user is not the user that executed the last command and is not a project manager
     */
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

    /**
     * @return a list of tuples containing all the executed commands and the username of the user that executed the command, in order of execution
     */
    public List<Tuple<CommandData,String>> getExecutedCommands(){
        List<Tuple<CommandData,String>> prevCmdList = new LinkedList<>();
        for (Tuple<Command,User> prevCmd : getExecutedStack()){
            prevCmdList.add(new Tuple<>(prevCmd.getFirst().getCommandData(), prevCmd.getSecond().getUsername()));
        }
        return prevCmdList;
    }

    /**
     * @return a list of tuples containing all the undone commands and the username of the user that executed the command, in order of undoing
     */
    public List<Tuple<CommandData,String>> getUndoneCommands() {
        List<Tuple<CommandData, String>> undoneCmdList = new LinkedList<>();
        for (Tuple<Command,User> undoneCmd : getUndoneStack()){
            undoneCmdList.add(new Tuple<>(undoneCmd.getFirst().getCommandData(), undoneCmd.getSecond().getUsername()));
        }
        return undoneCmdList;
    }

    /**
     * @return command data of the last executed command
     */
    public CommandData getLastExecutedCommandData(){
        if (getLastExecutedCommand() == null){
            return null;
        }
        return getLastExecutedCommand().getCommandData();
    }

    /**
     * @return command data of the last undone command
     */
    public CommandData getLastUndoneCommandData(){
        if (getLastUndoneCommand() == null){
            return null;
        }
        return getLastUndoneCommand().getCommandData();
    }
}