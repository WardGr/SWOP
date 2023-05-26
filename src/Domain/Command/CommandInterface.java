package Domain.Command;

import Domain.User.IncorrectUserException;
import Domain.DataClasses.Tuple;
import Domain.User.User;

import java.util.List;

/**
 * Interface to separate controllers from the object that manages the commands
 */
public interface CommandInterface {

    /**
     * Adds a command to the list of executed commands
     *
     * @param command           The command to be executed
     * @param executingUser     The user that wants to execute the command
     */
    void addExecutedCommand(Command command, User executingUser);

    /**
     * Adds a command to the list of undone commands
     *
     * @param currentUser                   The user that wants to undo the last command
     * @throws EmptyCommandStackException   If there are no commands to undo
     * @throws IncorrectUserException       If the current user is not the one who executed the last command and is not a project manager
     * @throws UndoNotPossibleException     If the last command cannot be undone
     */
    void undoLastCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException, UndoNotPossibleException;

    /**
     * Redoes the last command undone by the current user
     *
     * @param currentUser                   The user that undid the command
     * @throws EmptyCommandStackException   If there are no commands to redo
     * @throws IncorrectUserException       If the current user is not the one who executed the last command and is not a project manager
     */
    void redoLast(User currentUser) throws EmptyCommandStackException, IncorrectUserException;

    /**
     * @return  A list of tuples containing the data of the executed commands and the user that executed them, in order of execution
     */
    List<Tuple<CommandData,String>> getExecutedCommands();

    /**
     * @return  A list of tuples containing the data of the undone commands and the user that executed them, in order of undoing
     */
    List<Tuple<CommandData,String>> getUndoneCommands();

    /**
     * @return  Data of the last command executed
     */
    CommandData getLastExecutedCommandData();

    /**
     * @return  Data of the last command that was undone
     */
    CommandData getLastUndoneCommandData();

}
