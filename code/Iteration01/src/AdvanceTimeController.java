public class AdvanceTimeController {

  private final Session session;
  private final TaskManSystem taskManSystem;

  public AdvanceTimeController(
    Session session,
    TaskManSystem taskManSystem
  ) {
    this.session = session;
    this.taskManSystem = taskManSystem;
  }

  private Session getSession() {
    return session;
  }

  private TaskManSystem getTaskManSystem() {
    return taskManSystem;
  }



  public int getSystemHour(){
    return getTaskManSystem().getSystemTime().getHour();
  }

  public int getSystemMinute(){
    return getTaskManSystem().getSystemTime().getMinute();
  }

  /**
   * @return whether the preconditions of advancetime are met
   */
  public boolean advanceTimePreconditions() {
    return getSession().getRole() == Role.PROJECTMANAGER;
  }

  /**
   * Sets the system time to the new time given by the user
   *
   * @param newHour hour given by the user for the new time
   * @param newMinute minute given by the user for the new time
   * @throws IncorrectPermissionException if the user is not logged in as project manager
   * @throws InvalidTimeException if newMinute > 59 or < 0
   * @throws NewTimeBeforeSystemTimeException if the given time is before the system time (can only ADVANCE time)
   */
  public void setNewTime(int newHour, int newMinute) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException("");
    }
    getTaskManSystem().advanceTime(new Time(newHour, newMinute));
  }
}
