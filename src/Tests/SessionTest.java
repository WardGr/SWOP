package Tests;

import Application.Session;
import Application.SessionProxy;
import Domain.Role;
import Domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SessionTest {

    private User brewer;
    private User bossman;

    private Session brewerSession;
    private Session bossManSession;

    private Set<Role> brewerRoles;
    private Set<Role> bossRoles;

    @Before
    public void setUp() {
        brewerSession = new Session();
        bossManSession = new Session();

        Set<Role> brewerRoles = new HashSet<>();
        brewerRoles.add(Role.PYTHONPROGRAMMER);

        Set<Role> bossRoles = new HashSet<>();
        bossRoles.add(Role.PROJECTMANAGER);

        brewer = new User("OlavBl", "peer123", brewerRoles);
        bossman = new User("WardGr", "minecraft123", bossRoles);

        this.brewerRoles = brewerRoles;
        this.bossRoles = bossRoles;
    }

    @Test
    public void SessionTest() {

        assertTrue(brewerSession.getRoles().isEmpty());
        assertTrue(bossManSession.getRoles().isEmpty());

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

        assertEquals(brewerRoles, brewerSession.getRoles());
        assertEquals(bossRoles, bossManSession.getRoles());

        brewerSession.logout();
        assertFalse(brewerSession.isLoggedIn());
        assertNull(brewerSession.getCurrentUser());
        assertTrue(brewerSession.getRoles().isEmpty());

        bossManSession.logout();
        assertFalse(bossManSession.isLoggedIn());
        assertNull(bossManSession.getCurrentUser());
        assertTrue(bossManSession.getRoles().isEmpty());

        bossManSession.login(brewer);
        assertEquals(brewer, bossManSession.getCurrentUser());
        assertEquals(brewerRoles, bossManSession.getRoles());
        assertTrue(bossManSession.isLoggedIn());

        SessionProxy sessionProxy1 = new SessionProxy(brewerSession);
        assertTrue(sessionProxy1.getRoles().isEmpty());
        assertNull(sessionProxy1.getCurrentUser());

        brewerSession.login(brewer);
        SessionProxy sessionProxy2 = new SessionProxy(brewerSession);
        assertEquals(sessionProxy2.getRoles(), brewerRoles);
        assertEquals(sessionProxy2.getCurrentUser(), brewer);


    }
}
