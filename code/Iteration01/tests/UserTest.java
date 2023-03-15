import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    @Test
    public void testUser() {
        User thomas = new User("Thomas", "banaan123", Role.PROJECTMANAGER);
        assertEquals("Thomas", thomas.getUsername());
        assertEquals("banaan123", thomas.getPassword());
        assertEquals(Role.PROJECTMANAGER, thomas.getRole());
        assertNotEquals("Thomas", thomas.getPassword());
        assertNotEquals("banaan123", thomas.getUsername());
        assertNotSame(thomas.getRole(), Role.DEVELOPER);
        assertNotSame("banaan1234", thomas.getPassword());

        User jonathan = new User("Jonathan", "perzik789", Role.DEVELOPER);
        assertEquals("Jonathan", jonathan.getUsername());
        assertEquals("perzik789", jonathan.getPassword());
        assertNotEquals("perzik7890", jonathan.getPassword());
        assertNotEquals("thomas", jonathan.getUsername());
        assertEquals(Role.DEVELOPER, jonathan.getRole());
        assertNotSame(jonathan.getRole(), Role.PROJECTMANAGER);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User(null, null, null);
        });

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User("Fiona", "hoi123", null);
        });

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            User fiona = new User("Fiona", null, Role.PROJECTMANAGER);
        });
    }
}
