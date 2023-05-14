package Application;
import Domain.Command.Command;
import Domain.CommandHandler;
import Domain.Role;
import Domain.Time;
import Domain.User;

import java.util.List;
import java.util.Set;

public class CommandController {
    private Session session;
    private CommandHandler cmdHandler = new CommandHandler();

    public CommandController(Session session) {
        this.session = session;
    }

    public void undo() throws Exception {
        cmdHandler.undo(session.getCurrentUser());
    }

    public void redo() throws Exception {
        cmdHandler.redo(session.getCurrentUser());
    }

    public List<String> possibleUndoes() {
        return cmdHandler.possibleUndoes(session.getCurrentUser());
    }

    public List<String> possibleRedoes() {
        return cmdHandler.possibleRedoes(session.getCurrentUser());
    }

    public void addCommand(Command cmd) {
        cmdHandler.addNode(cmd, session.getCurrentUser());
    }

    }

