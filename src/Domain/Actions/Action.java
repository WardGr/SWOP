package Domain.Actions;

import Domain.User;

public abstract class Action {

    User getUser() // zo ofwel in de nodes bijhouden
    {
        return null;
    }

    public void undo() throws Exception {
    }

    public void redo() throws Exception {

    }

}
