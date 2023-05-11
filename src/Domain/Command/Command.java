package Domain.Command;

import Domain.User;

public abstract class Command {

    User getUser() // zo ofwel in de nodes bijhouden
    {
        return null;
    }

    public void undo() throws Exception {
    }

    public void redo() throws Exception {

    }

    public String information()
    {
        return null;
    }

    public boolean reversePossible()
    {
        return true;
    }
}
