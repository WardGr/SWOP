package Tests.ControllerTests;

import Application.Session.LoginException;
import Application.Session.Session;
import Application.SystemControllers.SessionController;
import Domain.User.UserManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionControllerTest {

    Session omer;
    UserManager um;
    SessionController sc;

    @Before
    public void setUp() throws Exception {
        this.omer = new Session();
        this.um = new UserManager();
        this.sc = new SessionController(omer, um);
    }

    @Test
    public void testPrecondition() throws Exception {
        assertTrue(sc.loginPrecondition());
        sc.login("WardGr", "minecraft123");
        assertFalse(sc.loginPrecondition());
        sc.logout();
        assertTrue(sc.loginPrecondition());
    }

    @Test
    public void loginTest() throws LoginException {
        sc.login("WardGr", "minecraft123");
        assertEquals("WardGr", omer.getCurrentUser().getUsername());
    }

    @Test
    public void logoutTest() throws LoginException {
        assertFalse(sc.logout());
        sc.login("WardGr", "minecraft123");
        assertTrue(sc.logout());
        assertNull(omer.getCurrentUser());
    }

    @Test
    public void testWrongUsernamePassword() {
        assertThrows(LoginException.class, () -> sc.login("Thomas", "1234"));
        assertThrows(LoginException.class, () -> sc.login("WardGr", "toilet573"));
        assertThrows(LoginException.class, () -> sc.login("OlavBl", "minecraft123"));
        assertTrue(sc.loginPrecondition());
    }

    @Test
    public void testAlreadyLoggedIn() throws LoginException {
        sc.login("WardGr", "minecraft123");
        assertThrows(LoginException.class, () -> sc.login("WardGr", "minecraft123"));
        assertFalse(sc.loginPrecondition());
        assertTrue(sc.logout());
        assertTrue(sc.loginPrecondition());
    }
}
