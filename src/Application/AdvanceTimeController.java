package Application;

import Domain.*;

import java.util.Set;

/**
 * Separates domain from UI for the advancetime use-case
 */
public class AdvanceTimeController {

    private final SessionProxy session;
    private final TaskManSystem taskManSystem;

    public AdvanceTimeController(
            SessionProxy session,
            TaskManSystem taskManSystem
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    private SessionProxy getSession() {
        return session;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
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
     * @throws InvalidTimeException             if newMinute > 59 or < 0
     * @throws NewTimeBeforeSystemTimeException if the given time is before the system time (can only ADVANCE time)
     */
    public void setNewTime(Time newTime) throws IncorrectPermissionException, NewTimeBeforeSystemTimeException {
        if (!advanceTimePreconditions()) {
            throw new IncorrectPermissionException("Incorrect permission: User is not a project manager or developer");
        }
        getTaskManSystem().advanceTime(newTime);
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
    }
}
