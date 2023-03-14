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

  public boolean advanceTimePreconditions() {
    return getSession().getRole() == Role.PROJECTMANAGER;
  }

  public int getSystemHour(){
    return getTaskManSystem().getSystemHour();
  }

  public int getSystemMinute(){
    return getTaskManSystem().getSystemMinute();
  }

  public void setNewTime(int newHour, int newMinute) throws IncorrectPermissionException, InvalidTimeException, NewTimeBeforeSystemTimeException {
    if (getSession().getRole() != Role.PROJECTMANAGER) {
      throw new IncorrectPermissionException();
    }
    getTaskManSystem().advanceTime(newHour, newMinute);
  }
}
