package Tests;

import Domain.Role;
import Domain.User;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testUser() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.PROJECTMANAGER);
        User thomas = new User("Thomas", "banaan123", roles);
        assertEquals("Thomas", thomas.getUsername());
        assertEquals("banaan123", thomas.getPassword());
        assertEquals(roles, thomas.getRoles());
        assertNotEquals("Thomas", thomas.getPassword());
        assertNotEquals("banaan123", thomas.getUsername());
        roles.add(Role.PYTHONPROGRAMMER);
        assertNotSame(roles, thomas.getRoles());
        assertNotSame("banaan1234", thomas.getPassword());

        roles.add(Role.JAVAPROGRAMMER);
        User jonathan = new User("Jonathan", "perzik789", roles);
        assertEquals("Jonathan", jonathan.getUsername());
        assertEquals("perzik789", jonathan.getPassword());
        assertNotEquals("perzik7890", jonathan.getPassword());
        assertNotEquals("thomas", jonathan.getUsername());
        assertEquals(roles, jonathan.getRoles());
        assertFalse(jonathan.getRoles().contains(Role.SYSADMIN));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User(null, null, null);
        });

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User("Fiona", "hoi123", null);
        });

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User("Fiona", null, roles);
        });

        Set<Role> emptyRoles = new HashSet<>();
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User("Fiona", "hoi123", emptyRoles);
        });
    }
}