package Tests;

import Domain.*;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

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

    private final UserManager userManager = new UserManager();

    private User ward;
    private User olav;
    private User sam;
    private User dieter;
    private User sander;
    private User jasper;
    private User jeroen;
    private User hannah;


    @Before
    public void setUp() {
        // Set up roles for users
        HashSet<Role> wardRoles = new HashSet<>();
        wardRoles.add(Role.JAVAPROGRAMMER);
        wardRoles.add(Role.PYTHONPROGRAMMER);

        HashSet<Role> olavRoles = new HashSet<>();
        olavRoles.add(Role.PYTHONPROGRAMMER);

        HashSet<Role> samRoles = new HashSet<>();
        samRoles.add(Role.JAVAPROGRAMMER);

        HashSet<Role> dieterRoles = new HashSet<>();
        dieterRoles.add(Role.SYSADMIN);

        HashSet<Role> sanderRoles = new HashSet<>();
        sanderRoles.add(Role.PROJECTMANAGER);

        HashSet<Role> jasperRoles = new HashSet<>();
        jasperRoles.add(Role.PROJECTMANAGER);
        jasperRoles.add(Role.JAVAPROGRAMMER);

        HashSet<Role> jeroenRoles = new HashSet<>();
        jeroenRoles.add(Role.PROJECTMANAGER);
        jeroenRoles.add(Role.PYTHONPROGRAMMER);

        HashSet<Role> hannahRoles = new HashSet<>();
        hannahRoles.add(Role.PROJECTMANAGER);
        hannahRoles.add(Role.PYTHONPROGRAMMER);
        hannahRoles.add(Role.JAVAPROGRAMMER);


        ward = new User("WardGr", "minecraft123", wardRoles);
        olav = new User("OlavBl", "peer123", olavRoles);
        sam = new User("SamHa", "trein123", samRoles);
        dieter = new User("DieterVH", "computer776", dieterRoles);
        sander = new User("SanderSc", "appelboom885", sanderRoles);
        jasper = new User("JasperVH", "computer776", jasperRoles);
        jeroen = new User("jeroenBe", "Aardappel862", jeroenRoles);
        hannah = new User("HannahEr", "Perzik123", hannahRoles);
    }

    @Test
    public void UserManTest() throws LoginException, UserNotFoundException {
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
        assertEquals(sander.getUsername(), userManager.getUser("SanderSc", "appelboom885").getUsername());
        assertEquals(sander.getPassword(), userManager.getUser("SanderSc", "appelboom885").getPassword());
        assertEquals(sander.getRoles(), userManager.getUser("SanderSc", "appelboom885").getRoles());

        assertThrows(LoginException.class, () -> {userManager.getUser("Fiona", "hoi123");});
        assertThrows(LoginException.class, () -> {userManager.getUser("Tom", null);});
        assertThrows(LoginException.class, () -> {userManager.getUser("WardGr", "fout_password");});
        assertThrows(LoginException.class, () -> {userManager.getUser("Fout_user", "minecraft123");});

        assertEquals(olav.getUsername(), userManager.getUser("OlavBl").getUsername());
        assertEquals(olav.getPassword(), userManager.getUser("OlavBl").getPassword());
        assertEquals(olav.getRoles(), userManager.getUser("OlavBl").getRoles());
        assertEquals(sam.getUsername(), userManager.getUser("SamHa").getUsername());
        assertEquals(sam.getPassword(), userManager.getUser("SamHa").getPassword());
        assertEquals(sam.getRoles(), userManager.getUser("SamHa").getRoles());
        assertEquals(sander.getUsername(), userManager.getUser("SanderSc").getUsername());
        assertEquals(sander.getPassword(), userManager.getUser("SanderSc").getPassword());
        assertEquals(sander.getRoles(), userManager.getUser("SanderSc").getRoles());

        assertThrows(UserNotFoundException.class, () -> {userManager.getUser("Fiona");});
        assertThrows(UserNotFoundException.class, () -> {userManager.getUser("Tom");});

    }
}
