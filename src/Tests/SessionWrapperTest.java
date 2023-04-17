package Tests;
import Application.Session;
import Application.SessionWrapper;
import Domain.*;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class SessionWrapperTest {

    @Test
    public void test() {
        Set<Role> wardRoles = new HashSet<>();
        wardRoles.add(Role.SYSADMIN);
        wardRoles.add(Role.JAVAPROGRAMMER);
        User ward = new User("Ward", "ward", wardRoles);

        Set<Role> dieterRoles = new HashSet<>();
        dieterRoles.add(Role.PROJECTMANAGER);
        dieterRoles.add(Role.PYTHONPROGRAMMER);
        dieterRoles.add(Role.JAVAPROGRAMMER);
        User dieter = new User("Dieter", "dieter", dieterRoles);

        Session current = new Session();
        current.login(ward);

        SessionWrapper wrapper = new SessionWrapper(current);
        assertEquals(ward, wrapper.getCurrentUser());
        assertEquals(2, wrapper.getRoles().size());
        assertEquals(Role.SYSADMIN, wrapper.getRoles().toArray()[0]);
        assertEquals(Role.JAVAPROGRAMMER, wrapper.getRoles().toArray()[1]);

        current.logout();
        current.login(dieter);

        assertEquals(dieter, wrapper.getCurrentUser());
        assertEquals(3, wrapper.getRoles().size());
        assertEquals(Role.PYTHONPROGRAMMER, wrapper.getRoles().toArray()[0]);
        assertEquals(Role.PROJECTMANAGER, wrapper.getRoles().toArray()[1]);
        assertEquals(Role.JAVAPROGRAMMER, wrapper.getRoles().toArray()[2]);
    }

}
