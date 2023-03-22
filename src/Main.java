// TODO: Do we have to comment every class with @ tags as well? And what about postconditions and invariants?


import UserInterface.*;
import Application.*;
import Domain.InvalidTimeException;
import Domain.TaskManSystem;
import Domain.Time;
import Domain.UserManager;
import UserInterface.UserInterface;

/**
 * Creates the initial objects and starts the UI
 */
public class Main {

    public static void main(String[] args) {
        try {
            TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0)); // exception thrown by the new Time
            UserManager userManager = new UserManager();
            Session session = new Session();

            SessionController sessionController = new SessionController(session, userManager);
            AdvanceTimeController advanceTimeController = new AdvanceTimeController(session, taskManSystem);
            CreateProjectController createProjectController = new CreateProjectController(session, taskManSystem);
            ShowProjectsController showProjectsController = new ShowProjectsController(session, taskManSystem);
            CreateTaskController createTaskController = new CreateTaskController(session, taskManSystem, userManager);
            LoadSystemController loadSystemController = new LoadSystemController(userManager, taskManSystem, session);
            UpdateTaskController updateTaskController = new UpdateTaskController(session, taskManSystem);

            SessionUI sessionUI = new SessionUI(sessionController);
            AdvanceTimeUI advanceTimeUI = new AdvanceTimeUI(advanceTimeController);
            CreateProjectUI createProjectUI = new CreateProjectUI(createProjectController);
            ShowProjectsUI showProjectsUI = new ShowProjectsUI(showProjectsController);
            CreateTaskUI createTaskUI = new CreateTaskUI(createTaskController);
            LoadSystemUI loadSystemUI = new LoadSystemUI(loadSystemController);
            UpdateTaskUI updateTaskUI = new UpdateTaskUI(updateTaskController);

            UserInterface UI = new UserInterface(
                    sessionUI,
                    advanceTimeUI,
                    createProjectUI,
                    showProjectsUI,
                    createTaskUI,
                    loadSystemUI,
                    updateTaskUI
            );
            UI.startSystem();
        } catch (InvalidTimeException e) {
            System.out.println("Somehow the initial start time is invalid, this really should not happen.");
        }
    }
}
