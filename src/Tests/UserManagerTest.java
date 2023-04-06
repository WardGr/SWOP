package Tests;

import Domain.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserManagerTest {

    private final UserManager userManager = new UserManager();


    @Test
    public void UserManTest() throws LoginException, UserNotFoundException {
        /*
        User ward = new User("WardGr", "minecraft123", Role.PROJECTMANAGER);
        User olav = new User("OlavBl", "peer123", Role.DEVELOPER);
        User sam = new User("SamHa", "trein123", Role.DEVELOPER);
        User dieter = new User("DieterVH", "computer776", Role.PROJECTMANAGER);
        User sander = new User("SanderSc", "appelboom885", Role.DEVELOPER);
        User jasper = new User("JasperVH", "computer776", Role.PROJECTMANAGER);
        User jeroen = new User("jeroenBe", "Aardappel862", Role.DEVELOPER);
        User hannah = new User("HannahEr", "Perzik123", Role.PROJECTMANAGER);

        assertEquals(ward.getUsername(), userManager.getUser("WardGr", "minecraft123").getUsername());
        assertEquals(ward.getPassword(), userManager.getUser("WardGr", "minecraft123").getPassword());
        assertEquals(ward.getRole(), userManager.getUser("WardGr", "minecraft123").getRole());
        assertEquals(sam.getUsername(), userManager.getUser("SamHa", "trein123").getUsername());
        assertEquals(sam.getPassword(), userManager.getUser("SamHa", "trein123").getPassword());
        assertEquals(sam.getRole(), userManager.getUser("SamHa", "trein123").getRole());
        assertEquals(olav.getUsername(), userManager.getUser(("OlavBl"), "peer123").getUsername());
        assertEquals(olav.getPassword(), userManager.getUser("OlavBl", "peer123").getPassword());
        assertEquals(olav.getRole(), userManager.getUser("OlavBl", "peer123").getRole());
        assertEquals(dieter.getUsername(), userManager.getUser("DieterVH", "computer776").getUsername());
        assertEquals(dieter.getPassword(), userManager.getUser("DieterVH", "computer776").getPassword());
        assertEquals(dieter.getRole(), userManager.getUser("DieterVH", "computer776").getRole());
        assertEquals(sander.getUsername(), userManager.getUser("SanderSc", "appelboom885").getUsername());
        assertEquals(sander.getPassword(), userManager.getUser("SanderSc", "appelboom885").getPassword());
        assertEquals(sander.getRole(), userManager.getUser("SanderSc", "appelboom885").getRole());
        assertEquals(jasper.getUsername(), userManager.getUser("JasperVH", "computer776").getUsername());
        assertEquals(jasper.getPassword(), userManager.getUser("JasperVH", "computer776").getPassword());
        assertEquals(jasper.getRole(), userManager.getUser("JasperVH", "computer776").getRole());
        assertEquals(hannah.getUsername(), userManager.getUser("HannahEr", "Perzik123").getUsername());
        assertEquals(hannah.getPassword(), userManager.getUser("HannahEr", "Perzik123").getPassword());
        assertEquals(hannah.getRole(), userManager.getUser("HannahEr", "Perzik123").getRole());
        assertEquals(jeroen.getUsername(), userManager.getUser("jeroenBe", "Aardappel862").getUsername());
        assertEquals(jeroen.getPassword(), userManager.getUser("jeroenBe", "Aardappel862").getPassword());
        assertEquals(jeroen.getRole(), userManager.getUser("jeroenBe", "Aardappel862").getRole());


        Exception exception = assertThrows(LoginException.class, () -> {
            userManager.getUser("Fiona", "hoi123");
        });
        exception = assertThrows(LoginException.class, () -> {
            userManager.getUser("Tom", null);
        });
        exception = assertThrows(LoginException.class, () -> {
            userManager.getUser("WardGr", "fout_password");
        });
        exception = assertThrows(LoginException.class, () -> {
            userManager.getUser("Fout_user", "minecraft123");
        });

        assertEquals(olav.getUsername(), userManager.getDeveloper("OlavBl").getUsername());
        assertEquals(olav.getPassword(), userManager.getDeveloper("OlavBl").getPassword());
        assertEquals(olav.getRole(), userManager.getDeveloper("OlavBl").getRole());
        assertEquals(sam.getUsername(), userManager.getDeveloper("SamHa").getUsername());
        assertEquals(sam.getPassword(), userManager.getDeveloper("SamHa").getPassword());
        assertEquals(sam.getRole(), userManager.getDeveloper("SamHa").getRole());
        assertEquals(sander.getUsername(), userManager.getDeveloper("SanderSc").getUsername());
        assertEquals(sander.getPassword(), userManager.getDeveloper("SanderSc").getPassword());
        assertEquals(sander.getRole(), userManager.getDeveloper("SanderSc").getRole());
        assertEquals(jeroen.getUsername(), userManager.getDeveloper("jeroenBe").getUsername());
        assertEquals(jeroen.getPassword(), userManager.getDeveloper("jeroenBe").getPassword());
        assertEquals(jeroen.getRole(), userManager.getDeveloper("jeroenBe").getRole());

        exception = assertThrows(UserNotFoundException.class, () -> {
            userManager.getDeveloper("Fiona");
        });
        exception = assertThrows(UserNotFoundException.class, () -> {
            userManager.getDeveloper("Tom");
        });
        exception = assertThrows(UserNotFoundException.class, () -> {
            userManager.getDeveloper("WardGr");
        });
        exception = assertThrows(UserNotFoundException.class, () -> {
            userManager.getDeveloper("DieterVH");
        });

        */
    }
}
