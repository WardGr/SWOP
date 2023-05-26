package Tests.DataClasses;

import Application.Session.Session;
import Application.Session.SessionProxy;
import Domain.User.Role;
import Domain.User.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SessionTest {

    @Mock
    private User brewer;
    @Mock
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
        this.brewerRoles = brewerRoles;
        this.bossRoles = bossRoles;

        Mockito.when(brewer.getRoles()).thenReturn(brewerRoles);
        Mockito.when(bossman.getRoles()).thenReturn(bossRoles);
    }

    @Test
    public void testLogin() {
        // Login
        assertFalse(brewerSession.isLoggedIn());
        assertFalse(bossManSession.isLoggedIn());

        assertEquals(brewerRoles, brewerSession.login(brewer));
        assertEquals(bossRoles, bossManSession.login(bossman));

        assertTrue(brewerSession.isLoggedIn());
        assertTrue(bossManSession.isLoggedIn());

        // Logout
        brewerSession.logout();
        bossManSession.logout();

        assertFalse(brewerSession.isLoggedIn());
        assertFalse(bossManSession.isLoggedIn());
    }

    @Test
    public void testSessionProxy() {
        brewerSession.login(brewer);
        bossManSession.login(bossman);

        SessionProxy brewerProxy = new SessionProxy(brewerSession);
        SessionProxy bossProxy = new SessionProxy(bossManSession);

        assertFalse(brewerProxy.getRoles().isEmpty());
        assertFalse(bossProxy.getRoles().isEmpty());

        assertEquals(brewerRoles, brewerProxy.getRoles());
        assertEquals(bossRoles, bossProxy.getRoles());

        brewerSession.logout();
        bossManSession.logout();

        assertTrue(brewerProxy.getRoles().isEmpty());
        assertTrue(bossProxy.getRoles().isEmpty());
    }
}
