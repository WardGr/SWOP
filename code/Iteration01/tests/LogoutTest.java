import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.*;

public class LogoutTest {
    Session session = new Session();
    UserManager userManager = new UserManager();
    SessionUI sessionUi = new SessionUI(session, userManager);
    SessionController sessionController = new SessionController(session, userManager);
    @Test
    public void testLogout() {
        // Set all System.out (so prints and such) output to go into this stream, so we can assert its contents.
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // Check if session is set up correctly
        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());

        // Logout, user not logged in, should fail
        sessionUi.logout();
        //assertEquals(out.toString(),"Already logged out.");
        out.reset();

        // Login as a developer
        System.setIn(new ByteArrayInputStream("OlavBl\ntoilet753\n".getBytes()));
        sessionUi.loginRequest();
        assertEquals(out.toString(),
                """
                        Type 'BACK' to cancel login\r
                        Enter username:\r
                        Enter password:\r
                        Welcome OlavBl! Your assigned role is developer\r
                        """.replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        assertTrue(session.isLoggedIn());
        out.reset();

        // Logout, session should be removed and role made null
        sessionUi.logout();
        assertEquals(out.toString(),"Logged out.\n".replaceAll("\\n|\\r\\n", System.getProperty("line.separator")));
        out.reset();

        assertFalse(session.isLoggedIn());
        assertNull(session.getRole());

    }
}
