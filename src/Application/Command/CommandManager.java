package Application.Command;

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
    private Node currNode = null;
    public CommandManager() {}


    private void addLastExecutedCommand(Command command, User executingUser){
        Node newNode = new Node(command, executingUser);
        if (currNode == null) {
            currNode = newNode;
        } else {
            currNode.next = newNode;
            newNode.prev = currNode;
            currNode = newNode;
        }
    }

    private Command getLastExecutedCommand(){
        return currNode.getcommand();
    }

    private User getUserLastExecutedCommand(){
        return currNode.getUser();
    }

    private User getUserLastUndoneCommand(){
        return currNode.getNext().getUser();
    }


    /**
     * Adds a command to the list of executed commands
     *
     * @param command           The command to be executed
     * @param executingUser     The user that executed the command
     */
    public void addExecutedCommand(Command command, User executingUser) {
        addLastExecutedCommand(command, executingUser);
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
        if (currNode == null || currNode.getPrev() == null) {
            throw new EmptyCommandStackException("There are no actions to undo");
        } else if (getUserLastExecutedCommand() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to undo the last action");
        }
        try{
            getLastExecutedCommand().undo();
            currNode = currNode.getPrev();
        } catch (Exception e) {
            throw new UndoNotPossibleException();
        }
    }

    /**
     * Redoes the last command undone by the current user
     *
     * @param currentUser                   The user that wants to redo the last command
     * @throws EmptyCommandStackException   If there are no commands to redo
     * @throws IncorrectUserException       If the current user is not the user that executed the last command and is not a project manager
     */
    public void redoLast(User currentUser) throws EmptyCommandStackException, IncorrectUserException {
        if (currNode == null || currNode.getNext() == null) {
            throw new EmptyCommandStackException("There are no actions to redo");
        } else if (getUserLastUndoneCommand() != currentUser &&
                !currentUser.getRoles().contains(Role.PROJECTMANAGER)) {
            throw new IncorrectUserException("The current user is not allowed to redo the last action");
        }
        try{
            currNode = currNode.getNext();
            getLastExecutedCommand().execute();
        } catch (Exception e) {
            throw new EmptyCommandStackException("There are no actions to redo");
        }
    }

    /**
     * @return a list of tuples containing all the executed commands and the username of the user that executed the command, in order of execution
     */
    public List<Tuple<CommandData,String>> getExecutedCommands(){
        if (currNode == null){
            return new LinkedList<>();
        }
        Node curr = currNode;
        List<Tuple<CommandData,String>> prevCmdList = new LinkedList<>();
        while (curr.getPrev() != null){
            prevCmdList.add(new Tuple<>(curr.getcommand().getCommandData(), curr.getUser().getUsername()));
            curr = curr.getPrev();
        }
        return prevCmdList;
    }

    /**
     * @return a list of tuples containing all the undone commands and the username of the user that executed the command, in order of undoing
     */
    public List<Tuple<CommandData,String>> getUndoneCommands() {
        if (currNode == null || currNode.getNext() == null){
            return new LinkedList<>();
        }
        Node curr = currNode.getNext();
        List<Tuple<CommandData,String>> nextCmdList = new LinkedList<>();
        while (curr.getNext() != null){
            nextCmdList.add(new Tuple<>(curr.getcommand().getCommandData(), curr.getUser().getUsername()));
            curr = curr.getNext();
        }
        return nextCmdList;
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
        Node curr = currNode.getNext();
        if (curr == null){
            return null;
        }
        return curr.getcommand().getCommandData();
    }

    private class Node {
        private final Command command;
        private Node next;
        private Node prev;
        private final User user;

        public Node(Command command, User user) {
            this.user = user;
            this.command = command;
            this.next = null;
            this.prev = null;
        }

        public Command getcommand() {
            return command;
        }

        public Node getNext() {
            return next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public User getUser() {
            return user;
        }
    }
    
}