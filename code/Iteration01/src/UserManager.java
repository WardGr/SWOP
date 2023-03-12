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
     * Checks to see if the given username and password correspond to a valid user, and returns it's user object
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     * @throws UserNotFoundException if the given username and password dont correspond to a user in the db
     * @return returns the user that matches the username and password
     */
    public User getUser(String username, String password) throws UserNotFoundException {
        for (User user : getUsers()) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    public User getDeveloper(String userName) throws UserNotFoundException {
        for (User user : getUsers()){
            if (user.getUsername().equals(userName) && user.getRole().equals(Role.DEVELOPER)){
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    private List<User> getUsers() {
        return List.copyOf(users);
    }
}
