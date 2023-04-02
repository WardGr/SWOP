package Application;

import Domain.*;

/**
 * Separates domain from UI for the advancetime use-case
 */
public class AdvanceTimeController {

    private final SessionWrapper session;
    private final TaskManSystem taskManSystem;

    public AdvanceTimeController(
            SessionWrapper session,
            TaskManSystem taskManSystem
    ) {
        this.session = session;
        this.taskManSystem = taskManSystem;
    }

    private SessionWrapper getSession() {
        return session;
    }

    private TaskManSystem getTaskManSystem() {
        return taskManSystem;
    }

    public int getSystemHour() {
        return getTaskManSystem().getSystemTime().getHour();
    }

    public int getSystemMinute() {
        return getTaskManSystem().getSystemTime().getMinute();
    }

    private Time getSystemTime() {
        return getTaskManSystem().getSystemTime();
    }

    /**
     * @return whether the preconditions of advancetime are met
     */
    public boolean advanceTimePreconditions() {
        return getSession().getRole() == Role.PROJECTMANAGER || getSession().getRole() == Role.DEVELOPER;
    }

    /**
     * Sets the system time to the new time given by the user
     *
     * @param newHour   hour given by the user for the new time
     * @param newMinute minute given by the user for the new time
     * @throws IncorrectPermissionException     if the user is not logged in as project manager
     * @throws InvalidTimeException             if newMinute > 59 or < 0
     * @throws NewTimeBeforeSystemTimeException if the given time is before the system time (can only ADVANCE time)
     */
    public void setNewTime(int newHour, int newMinute) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
        if (!advanceTimePreconditions()) {
            throw new IncorrectPermissionException("Incorrect permission: User is not a project manager or developer");
        }
        getTaskManSystem().advanceTime(new Time(newHour, newMinute));
    }

    /**
     * Advances the time by the given minutes
     *
     * @param advanceMinutes amount of minutes to advance the system clock with
     * @throws IncorrectPermissionException if the user is not logged in as project manager or developer
     * @throws InvalidTimeException if somehow TaskManSystem incorrectly creates a Time object from the given minutes
     * @throws NewTimeBeforeSystemTimeException if advanceMinutes < 0
     */
    public void setNewTime(int advanceMinutes) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
        if (!advanceTimePreconditions()) {
            throw new IncorrectPermissionException("Incorrect permission: User is not a project manager or developer");
        }
        getTaskManSystem().advanceTime(advanceMinutes);
    }
}
