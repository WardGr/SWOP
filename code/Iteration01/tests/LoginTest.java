import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {
    Session session = new Session();
    UserManager userManager = new UserManager();
    SessionUI sessionUi = new SessionUI(session, userManager);
    SessionController sessionController = new SessionController(session, sessionUi, userManager);

    public LoginTest() throws NotValidTimeException {
    }

    @Test
    public void testLogin() {
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());
        // Login as a developer
        sessionController.login("OlavBl", "toilet753");
        assertTrue(session.isLoggedIn());
        assertEquals(session.getRole(), Role.DEVELOPER);
        // Login as a project manager, should fail as already logged in
        // Role should be developer
        assertEquals(session.getCurrentUser().getUsername(), "OlavBl");
        assertTrue(session.isLoggedIn());
        assertSame(session.getRole(), Role.DEVELOPER);
        // Logout
        session.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());
        // Login as a project manager
        // Role should be project manager
        sessionController.login("WardGr", "minecraft123");
        assertTrue(session.isLoggedIn());
        assertEquals(session.getRole(), Role.PROJECTMANAGER);
        assertEquals(session.getCurrentUser().getUsername(), "WardGr");
        assertEquals(session.getCurrentUser().getPassword(), "minecraft123");
        // Lougout
        // Role should be null
        // Login with invalid user
        sessionController.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());
        System.out.println("For sake: die returend nu en vraagt voor input, dit is echt kut om te testen :(");
        sessionController.login("WardGr", "toilet786");
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());
    }
}
