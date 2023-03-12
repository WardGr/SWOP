import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class LoginTest {
    Session session = new Session();
    UserManager userManager = new UserManager();
    SessionUI sessionUi = new SessionUI(session, userManager);
    SessionController sessionController = new SessionController(session, sessionUi, userManager);

    public LoginTest() {
    }

    @Test
    public void testLogin() {


        // Set all System.out (so prints and such) output to go into this stream, so we can assert its contents.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Check if session is set up correctly
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());

        // Login as a developer
        System.setIn(new ByteArrayInputStream("OlavBl\ntoilet753\n".getBytes()));

        sessionUi.loginRequest(new Scanner(System.in));
        assertEquals(out.toString(),
                """
                        Type 'BACK' to cancel login\r
                        Enter username:\r
                        Enter password:\r
                        Welcome OlavBl! Your assigned role is developer\r
                        """);
        out.reset();

        assertTrue(session.isLoggedIn());
        assertEquals(session.getRole(), Role.DEVELOPER);

        // Login as a project manager, should fail as already logged in, current session should stay untouched
        // Role should stay developer
        sessionUi.loginRequest(new Scanner(System.in));
        assertEquals(out.toString(),"You are already logged in!\r\n");
        out.reset();

        assertEquals(session.getCurrentUser().getUsername(), "OlavBl");
        assertTrue(session.isLoggedIn());
        assertSame(session.getRole(), Role.DEVELOPER);

        // Logout, session should be removed and role made null
        sessionUi.logout();
        assertEquals(out.toString(),"Logged out.\r\n");
        out.reset();

        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());


        // Try to log in with invalid user input, should give incorrect username/password, and tests the "BACK" option.
        System.setIn(new ByteArrayInputStream("WardGr\ntoilet753\nBACK\n".getBytes()));
        sessionUi.loginRequest(new Scanner(System.in));
        assertEquals(out.toString(),
                        """
                        Type 'BACK' to cancel login\r
                        Enter username:\r
                        Enter password:\r
                        Incorrect username/password combination, please try again\r
                        Type 'BACK' to cancel login\r
                        Enter username:\r
                        """);
        out.reset();

        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());

        // Try to log in with Project Manager, should succeed as user is logged out
        System.setIn(new ByteArrayInputStream("WardGr\nminecraft123\n".getBytes()));
        sessionUi.loginRequest(new Scanner(System.in));
        assertEquals(out.toString(),
                """
                Type 'BACK' to cancel login\r
                Enter username:\r
                Enter password:\r
                Welcome WardGr! Your assigned role is project manager\r
                """);
        out.reset();

        assertTrue(session.isLoggedIn());
        assertEquals(session.getRole(), Role.PROJECTMANAGER);
        assertEquals(session.getCurrentUser().getUsername(), "WardGr");
        assertEquals(session.getCurrentUser().getPassword(), "minecraft123");

        // Logout
        // Role should be null
        sessionController.logout();
        assertEquals(out.toString(), "Logged out.\r\n");
        out.reset();

        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());

        sessionController.logout();
        assertEquals(out.toString(),"Already logged out.\r\n");
    }
}
