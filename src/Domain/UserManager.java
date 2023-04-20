package Domain;

import Application.LoginException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Keeps track of all registered users, manages login
 */
public class UserManager {

    private List<User> users;

    /**
     * Initialises the UserManager object, reading the users.txt file and adding all users to the currently registered
     * users list
     */
    public UserManager() {
        try {
            LinkedList<User> users = new LinkedList<>();
            File usersFile = new File("src/Resources/users.txt");
            Scanner myReader = new Scanner(usersFile);
            while (myReader.hasNextLine()) {
                List<String> data = List.of(myReader.nextLine().split(" "));
                String username = data.get(0);
                String password = data.get(1);

                Set<Role> roles = new HashSet<>();

                for (String role : data.subList(2, data.size())) {
                    switch (role) {
                        case "sysadmin" -> roles.add(Role.SYSADMIN);
                        case "javaDev" -> roles.add(Role.JAVAPROGRAMMER);
                        case "pythonDev" -> roles.add(Role.PYTHONPROGRAMMER);
                        case "projectMan" -> roles.add(Role.PROJECTMANAGER);
                    }
                }

                User user = new User(username, password, roles);
                users.add(user);
            }
            myReader.close();
            this.users = users;
        } catch (FileNotFoundException e) {
            System.out.println("Error reading file.");
            e.printStackTrace();
        }
    }

    /**
     * Checks to see if the given username and password correspond to a valid user, and returns it's user object
     *
     * @param username The username the user gave via the UI login prompt
     * @param password The password the user gave via the UI login prompt
     * @return returns the user that matches the username and password
     * @throws LoginException if the given username/password combination does not match
     */
    public User getUser(String username, String password)
            throws LoginException {
        for (User user : getUsers()) {
            if (
                    username.equals(user.getUsername()) &&
                            password.equals(user.getPassword())
            ) {
                return user;
            }
        }
        throw new LoginException("Username/password combination is incorrect");
    }

    /**
     * @param userName Name of the user to get the User object from
     * @return The user object corresponding to the given userName
     * @throws UserNotFoundException if the given userName does not correspond to an existing User
     */
    public User getUser(String userName) throws UserNotFoundException {
        for (User user : getUsers()) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    /**
     * @return An immutable list of users
     */
    public List<User> getUsers() {
        return List.copyOf(users);
    }
}
