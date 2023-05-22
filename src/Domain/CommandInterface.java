package Domain;

import Domain.Command.Command;
import Domain.Command.CommandData;
import Domain.Command.UndoNotPossibleException;

import java.util.List;

public interface CommandInterface {

    void addExecutedCommand(Command command, User executingUser);

    void undoLastCommand(User currentUser) throws EmptyCommandStackException, IncorrectUserException, UndoNotPossibleException;

    void redoLast(User currentUser) throws EmptyCommandStackException, IncorrectUserException;

    List<Tuple<CommandData,String>> getPreviousCommandsList();

    List<Tuple<CommandData,String>> getUndoneCommandsList();

    CommandData getLastExecutedCommand();

    CommandData getLastUndoneCommand();

}
