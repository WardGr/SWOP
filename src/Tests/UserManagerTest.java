package Tests;

import Application.LoginException;
import Domain.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashSet;
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

    @Mock
    private User ward;
    @Mock
    private User olav;
    @Mock
    private User sam;
    @Mock
    private User dieter;


    @Before
    public void setUp() {
        Mockito.when(ward.getUsername()).thenReturn("WardGr");
        Mockito.when(ward.getPassword()).thenReturn("minecraft123");
        Mockito.when(ward.getRoles()).thenReturn(Set.of(Role.JAVAPROGRAMMER, Role.PYTHONPROGRAMMER));

        Mockito.when(olav.getUsername()).thenReturn("OlavBl");
        Mockito.when(olav.getPassword()).thenReturn("peer123");
        Mockito.when(olav.getRoles()).thenReturn(Set.of(Role.PYTHONPROGRAMMER));

        Mockito.when(olav.getUsername()).thenReturn("SamHa");
        Mockito.when(olav.getPassword()).thenReturn("trein123");
        Mockito.when(olav.getRoles()).thenReturn(Set.of(Role.JAVAPROGRAMMER));

        Mockito.when(olav.getUsername()).thenReturn("DieterVH");
        Mockito.when(olav.getPassword()).thenReturn("computer776");
        Mockito.when(olav.getRoles()).thenReturn(Set.of(Role.PROJECTMANAGER));

        userManager = new UserManager();
    }

    @Test
    public void testGetUserWithPassword() throws LoginException, UserNotFoundException {
        assertEquals(ward.getUsername(), userManager.getUser("WardGr", "minecraft123").getUsername());
        assertEquals(ward.getPassword(), userManager.getUser("WardGr", "minecraft123").getPassword());
        assertEquals(ward.getRoles(), userManager.getUser("WardGr", "minecraft123").getRoles());
        assertEquals(sam.getUsername(), userManager.getUser("SamHa", "trein123").getUsername());
        assertEquals(sam.getPassword(), userManager.getUser("SamHa", "trein123").getPassword());
        assertEquals(sam.getRoles(), userManager.getUser("SamHa", "trein123").getRoles());
        assertEquals(olav.getUsername(), userManager.getUser(("OlavBl"), "peer123").getUsername());
        assertEquals(olav.getPassword(), userManager.getUser("OlavBl", "peer123").getPassword());
        assertEquals(olav.getRoles(), userManager.getUser("OlavBl", "peer123").getRoles());
        assertEquals(dieter.getUsername(), userManager.getUser("DieterVH", "computer776").getUsername());
        assertEquals(dieter.getPassword(), userManager.getUser("DieterVH", "computer776").getPassword());
        assertEquals(dieter.getRoles(), userManager.getUser("DieterVH", "computer776").getRoles());

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
        assertEquals(ward.getUsername(), userManager.getUser("WardGr").getUsername());
        assertEquals(ward.getPassword(), userManager.getUser("WardGr").getPassword());
        assertEquals(ward.getRoles(), userManager.getUser("WardGr").getRoles());
        assertEquals(olav.getUsername(), userManager.getUser("OlavBl").getUsername());
        assertEquals(olav.getPassword(), userManager.getUser("OlavBl").getPassword());
        assertEquals(olav.getRoles(), userManager.getUser("OlavBl").getRoles());
        assertEquals(sam.getUsername(), userManager.getUser("SamHa").getUsername());
        assertEquals(sam.getPassword(), userManager.getUser("SamHa").getPassword());
        assertEquals(sam.getRoles(), userManager.getUser("SamHa").getRoles());
        assertEquals(dieter.getUsername(), userManager.getUser("DieterVH").getUsername());
        assertEquals(dieter.getPassword(), userManager.getUser("DieterVH").getPassword());
        assertEquals(dieter.getRoles(), userManager.getUser("DieterVH").getRoles());

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
