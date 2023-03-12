public class AdvanceTimeController {
    private Session session;
    private TaskManSystem taskManSystem;
    private AdvanceTimeUI ui;
    public AdvanceTimeController(Session session, TaskManSystem taskManSystem, AdvanceTimeUI ui){
        this.session = session;
        this.taskManSystem = taskManSystem;
        this.ui = ui;
    }

    public void advanceTime(){
        if (session.getRole() != Role.PROJECTMANAGER) {
            ui.printAccessError(Role.PROJECTMANAGER);
            return;
        }
        ui.chooseNewTime(taskManSystem.getSystemHour(), taskManSystem.getSystemMinute());
    }

    public void setNewTime(int newHour, int newMinute){
        if (session.getRole() != Role.PROJECTMANAGER) {
            ui.printAccessError(Role.PROJECTMANAGER);
            return;
        }

        try {
            taskManSystem.advanceTime(newHour,newMinute);
        } catch (NotValidTimeException e) {
            ui.printNotValidTimeError();
        } catch (NewTimeBeforeSystemTimeException e) {
            ui.printNewBeforeSystemTimeError();
        }

    }
}
