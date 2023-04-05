package Domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Keeps track of all registered users, manages login
 */
public class UserManager {

    private List<User> users;

    public UserManager() {
        try {
            LinkedList<User> users = new LinkedList<>();
            File usersFile = new File("src/Resources/users.txt");
            Scanner myReader = new Scanner(usersFile);
            while (myReader.hasNextLine()) {
                String[] data = myReader.nextLine().split(" ");
                String username = data[0];
                String password = data[1];
                Role role = null;
                if (data[2].equals("developer")) {
                    role = Role.SYSADMIN; // TODO
                } else if (data[2].equals("manager")) {
                    role = Role.PROJECTMANAGER;
                }
                User user = new User(username, password, role);
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
     * @throws UserNotFoundException if the given username and password dont correspond to a user in the db
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

    // TODO,misschien niet meer nodig met de andere checks
    public User getDeveloper(String userName) throws UserNotFoundException {
        for (User user : getUsers()) {
            if (
                    user.getUsername().equals(userName) &&
                            ( user.getRole().equals(Role.SYSADMIN) ||
                                user.getRole().equals(Role.JAVAPROGRAMMER) ||
                                user.getRole().equals(Role.PYTHONPROGRAMMER) )
            ) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }

    private List<User> getUsers() {
        return List.copyOf(users);
    }
}