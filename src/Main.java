// TODO: Do we have to comment every class with @ tags as well? And what about postconditions and invariants?


import Application.*;
import Domain.InvalidTimeException;
import Domain.TaskManSystem;
import Domain.Time;
import Domain.UserManager;
import UserInterface.*;

/**
 * Creates the initial objects and starts the UI
 */
public class Main {

    public static void main(String[] args) {
        try {
            TaskManSystem taskManSystem = new TaskManSystem(new Time(0, 0)); // exception thrown by the new Time
            UserManager userManager = new UserManager();
            Session session = new Session();
            SessionProxy sessionProxy = new SessionProxy(session);

            SessionController sessionController = new SessionController(session, userManager);
            AdvanceTimeController advanceTimeController = new AdvanceTimeController(sessionProxy, taskManSystem);
            CreateProjectController createProjectController = new CreateProjectController(sessionProxy, taskManSystem);
            ShowProjectsController showProjectsController = new ShowProjectsController(sessionProxy, taskManSystem);
            CreateTaskController createTaskController = new CreateTaskController(sessionProxy, taskManSystem, userManager);
            LoadSystemController loadSystemController = new LoadSystemController(sessionProxy, taskManSystem, userManager);
            StartTaskController startTaskController = new StartTaskController(sessionProxy, taskManSystem);
            EndTaskController endTaskController = new EndTaskController(sessionProxy, taskManSystem);
            UpdateDependenciesController updateDependenciesController = new UpdateDependenciesController(sessionProxy, taskManSystem);

            SessionUI sessionUI = new SessionUI(sessionController);
            AdvanceTimeUI advanceTimeUI = new AdvanceTimeUI(advanceTimeController);
            CreateProjectUI createProjectUI = new CreateProjectUI(createProjectController);
            ShowProjectsUI showProjectsUI = new ShowProjectsUI(showProjectsController);
            CreateTaskUI createTaskUI = new CreateTaskUI(createTaskController);
            LoadSystemUI loadSystemUI = new LoadSystemUI(loadSystemController);
            StartTaskUI startTaskUI = new StartTaskUI(startTaskController);
            EndTaskUI endTaskUI = new EndTaskUI(endTaskController);
            UpdateDependenciesUI updateDependenciesUI = new UpdateDependenciesUI(updateDependenciesController);

            UserInterface UI = new UserInterface(
                    sessionUI,
                    advanceTimeUI,
                    createProjectUI,
                    showProjectsUI,
                    createTaskUI,
                    loadSystemUI,
                    startTaskUI,
                    endTaskUI,
                    updateDependenciesUI
            );
            UI.startSystem();
        } catch (InvalidTimeException e) {
            System.out.println("Somehow the initial start time is invalid, this really should not happen.");
        }
    }
}
