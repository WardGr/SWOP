package Tests.Domain.UserTest;

import Application.LoginException;
import Domain.User.Role;
import Domain.User.UserManager;
import Domain.User.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


/*
WardGr minecraft123 javaDev pythonDev
OlavBl peer123 pythonDev
SamHa trein123 javaDev
DieterVH computer776 sysadmin
SanderSc appelboom885 projectMan
JasperVH computer776 projectMan javaDev
jeroenBe Aardappel862 projectMan pythonDev
HannahEr Perzik123 projectMan javaDev pythonDev
 */

public class UserManagerTest {

    private UserManager userManager;


    @Before
    public void setUp() {
        userManager = new UserManager();
    }

    @Test
    public void testGetUserWithPassword() throws LoginException, UserNotFoundException {
        assertEquals("WardGr", userManager.getUser("WardGr", "minecraft123").getUsername());
        assertEquals("minecraft123", userManager.getUser("WardGr", "minecraft123").getPassword());
        assertEquals(Set.of(Role.JAVAPROGRAMMER, Role.PYTHONPROGRAMMER), userManager.getUser("WardGr", "minecraft123").getRoles());
        assertEquals("SamHa", userManager.getUser("SamHa", "trein123").getUsername());
        assertEquals("trein123", userManager.getUser("SamHa", "trein123").getPassword());
        assertEquals(Set.of(Role.JAVAPROGRAMMER), userManager.getUser("SamHa", "trein123").getRoles());
        assertEquals("OlavBl", userManager.getUser(("OlavBl"), "peer123").getUsername());
        assertEquals("peer123", userManager.getUser("OlavBl", "peer123").getPassword());
        assertEquals(Set.of(Role.PYTHONPROGRAMMER), userManager.getUser("OlavBl", "peer123").getRoles());
        assertEquals("DieterVH", userManager.getUser("DieterVH", "computer776").getUsername());
        assertEquals("computer776", userManager.getUser("DieterVH", "computer776").getPassword());
        assertEquals(Set.of(Role.PROJECTMANAGER), userManager.getUser("DieterVH", "computer776").getRoles());

        assertThrows(LoginException.class, () -> {
            userManager.getUser("Fiona", "hoi123");
        });
        assertThrows(LoginException.class, () -> {
            userManager.getUser("Tom", null);
        });
        assertThrows(LoginException.class, () -> {
            userManager.getUser("WardGr", "fout_password");
        });
        assertThrows(LoginException.class, () -> {
            userManager.getUser("Fout_user", "minecraft123");
        });
    }

    @Test
    public void testGetUser() throws UserNotFoundException {
        assertEquals("WardGr", userManager.getUser("WardGr").getUsername());
        assertEquals("minecraft123", userManager.getUser("WardGr").getPassword());
        assertEquals(Set.of(Role.JAVAPROGRAMMER, Role.PYTHONPROGRAMMER), userManager.getUser("WardGr").getRoles());
        assertEquals("OlavBl", userManager.getUser("OlavBl").getUsername());
        assertEquals("peer123", userManager.getUser("OlavBl").getPassword());
        assertEquals(Set.of(Role.PYTHONPROGRAMMER), userManager.getUser("OlavBl").getRoles());
        assertEquals("SamHa", userManager.getUser("SamHa").getUsername());
        assertEquals("trein123", userManager.getUser("SamHa").getPassword());
        assertEquals(Set.of(Role.JAVAPROGRAMMER), userManager.getUser("SamHa").getRoles());
        assertEquals("DieterVH", userManager.getUser("DieterVH").getUsername());
        assertEquals("computer776", userManager.getUser("DieterVH").getPassword());
        assertEquals(Set.of(Role.PROJECTMANAGER), userManager.getUser("DieterVH").getRoles());

        assertThrows(UserNotFoundException.class, () -> {
            userManager.getUser("Fiona");
        });
        assertThrows(UserNotFoundException.class, () -> {
            userManager.getUser("Tom");
        });
    }

    @Test
    public void testGetUsers(){
        assertEquals(8, userManager.getUsers().size());
    }
}
