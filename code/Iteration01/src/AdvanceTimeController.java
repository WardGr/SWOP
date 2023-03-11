public class AdvanceTimeController {
    private Session session;
    private TaskManSystem taskManSystem;
    private AdvanceTimeUI ui;
    private Time systemTime;
    public AdvanceTimeController(Session session, TaskManSystem taskManSystem, AdvanceTimeUI ui, Time systemTime){
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.ui = ui;
        this.systemTime = systemTime;
    }

    public void advanceTime(){
        ui.chooseNewTime();
    }

    public void setNewTime(int newTimeInt){
        if (session.getRole() != Role.PROJECTMANAGER) {
            ui.printAccessError(Role.PROJECTMANAGER);
            return;
        }

        Time newTime = new Time(newTimeInt);

        if (newTime.before(systemTime)){
            ui.printNewBeforeSystemTimeError();
        }

        taskManSystem.advanceTime(newTime);
    }
}
