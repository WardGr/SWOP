package Tests;

import Application.Session;
import Application.SessionWrapper;
import Domain.Role;
import Domain.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class SessionTest {
    @Test
    public void SessionTest() {
        /*
        Session brewerSession = new Session();
        Session bossManSession = new Session();
        User brewer = new User("OlavBl", "peer123", Role.DEVELOPER);
        User bossman = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);

        assertNull(brewerSession.getRole());
        assertNull(bossManSession.getRole());

        assertFalse(brewerSession.isLoggedIn());
        assertFalse(bossManSession.isLoggedIn());
        assertNull(brewerSession.getCurrentUser());
        assertNull(bossManSession.getCurrentUser());
        brewerSession.login(brewer);
        assertFalse(bossManSession.isLoggedIn());
        assertEquals(brewer, brewerSession.getCurrentUser());
        assertTrue(brewerSession.isLoggedIn());
        bossManSession.login(bossman);
        assertTrue(bossManSession.isLoggedIn());
        assertEquals(bossman, bossManSession.getCurrentUser());

        assertEquals(Role.DEVELOPER, brewerSession.getRole());
        assertEquals(Role.PROJECTMANAGER, bossManSession.getRole());

        brewerSession.logout();
        assertFalse(brewerSession.isLoggedIn());
        assertNull(brewerSession.getCurrentUser());
        assertNull(brewerSession.getRole());

        bossManSession.logout();
        assertFalse(bossManSession.isLoggedIn());
        assertNull(bossManSession.getCurrentUser());
        assertNull(bossManSession.getRole());

        bossManSession.login(brewer);
        assertEquals(brewer, bossManSession.getCurrentUser());
        assertEquals(Role.DEVELOPER, bossManSession.getRole());
        assertTrue(bossManSession.isLoggedIn());

        SessionWrapper sessionWrapper1 = new SessionWrapper(brewerSession);
        assertNull(sessionWrapper1.getRole());
        assertNull(sessionWrapper1.getCurrentUser());

        brewerSession.login(brewer);
        SessionWrapper sessionWrapper2 = new SessionWrapper(brewerSession);
        assertEquals(sessionWrapper2.getRole(), Role.DEVELOPER);
        assertEquals(sessionWrapper2.getCurrentUser(), brewer);

        */


    }
}
