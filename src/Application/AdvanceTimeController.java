package Application;

import Domain.*;

import java.util.Set;

import Domain.Command.AdvanceTimeCommand;
import Domain.Command.Command;
import Domain.Command.ImpossibleUndoRedo;
import Domain.Command.SetNewTimeCommand;

/**
 * Separates domain from UI for the advancetime use-case
 */
public class AdvanceTimeController {

    private final SessionProxy session;
    private final TaskManSystem taskManSystem;
    private final CommandManager commandManager;


    /**
     * Creates this controller object
     *
     * @param session           The current session to set as active session
     * @param taskManSystem     The system object to set as current system
     */
    public AdvanceTimeController(
            SessionProxy session,
            TaskManSystem taskManSystem,
            CommandManager commandManager
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.commandManager = commandManager;
    }

    /**
     * @return  The session data object with currently logged-in user
     */
    private SessionProxy getSession() {
        return session;
    }

    /**
     * @return  The object containing the current taskmanager system
     */
    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    private CommandManager getCommandManager() {
        return commandManager;
    }

    public Time getSystemTime() {
        return getTaskManSystem().getSystemTime();
    }

    /**
     * @return whether the preconditions of advancetime are met
     */
    public boolean advanceTimePreconditions() {
        Set<Role> roles = getSession().getRoles();
        return roles != null &&
                (roles.contains(Role.PROJECTMANAGER) ||
                        roles.contains(Role.SYSADMIN) ||
                        roles.contains(Role.JAVAPROGRAMMER) ||
                        roles.contains(Role.PYTHONPROGRAMMER));
    }

    /**
     * Sets the system time to the new time given by the user
     *
     * @param newTime time given by the user for the new time
     * @throws IncorrectPermissionException     if the user is not logged in as project manager
     * @throws NewTimeBeforeSystemTimeException if the given time is before the system time (can only ADVANCE time)
     */
    public void setNewTime(Time newTime) throws IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        if (!advanceTimePreconditions()) {
            throw new IncorrectPermissionException("Incorrect permission: User is not a project manager or developer");
        }
        getTaskManSystem().advanceTime(newTime);
        SetNewTimeCommand cmd = new SetNewTimeCommand(getTaskManSystem(), newTime);
        cmd.execute();
        getCommandManager().addExecutedCommand(cmd, getSession().getCurrentUser());
    }

    /**
     * Advances the time by the given minutes
     *
     * @param advanceMinutes amount of minutes to advance the system clock with
     * @throws IncorrectPermissionException     if the user is not logged in as project manager or developer
     * @throws InvalidTimeException             if somehow TaskManSystem incorrectly creates a Time object from the given minutes
     * @throws NewTimeBeforeSystemTimeException if advanceMinutes < 0
     */
    public void advanceTime(int advanceMinutes) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
        if (!advanceTimePreconditions()) {
            throw new IncorrectPermissionException("Incorrect permission: User is not a project manager or developer");
        }
        getTaskManSystem().advanceTime(advanceMinutes);
        AdvanceTimeCommand cmd = new AdvanceTimeCommand(getTaskManSystem(), advanceMinutes);
        cmd.execute();
        getCommandManager().addExecutedCommand(cmd, getSession().getCurrentUser());
    }
}
