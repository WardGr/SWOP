import java.util.LinkedList;
import java.util.List;

public class UserManager {
    private List<User> users;

    public UserManager() {
        // Temporarily hardcode some users x
        LinkedList<User> users = new LinkedList<>();

        User user1 = new User("Ward", "123", Role.PROJECTMANAGER);
        User user2 = new User("Sam", "vijf5", Role.DEVELOPER);
        User user3 = new User("Dieter", "123", Role.PROJECTMANAGER);
        User user4 = new User("Olav", "123", Role.DEVELOPER);

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

        this.users = users;
    }

    /**
     * Checks to see if the given username and password correspond to a valid user, and returns their role
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     * @return If the user exists, returns the appropriate role of the user, else returns null.
     */
    public Role login(String username, String password) {
        for (User user : users) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                return user.getRole();
            }
        }
        /* TODO: Ik heb voor nu een null gereturned, custom exceptions zijn kut om in java te maken, daar moet ge een eigen klasse voor maken
         *  anders kunnen we ook een RuntimeException gooien, ma ik vind da ni zo logisch? Da lijkt mij eerder iets voor
         *  nullpointerdereference enzo..
         */
        return null;
    }
}
