public class ShowProjectsTest {
    Session session = new Session();
    UserManager userManager = new UserManager();
    SessionUI sessionUi = new SessionUI(session, userManager);
    SessionController sessionController = new SessionController(session, userManager);
}
