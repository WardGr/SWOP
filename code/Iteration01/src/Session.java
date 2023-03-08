public class Session {
    private User currentUser;


    public Session(User newUser) {
        this.currentUser = newUser;
    }

    public Session() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUserName() {
        if(getCurrentUser() == null) {
            return null;
        }
        return currentUser.getUsername();
    }

    public Role getRole() {
        if(getCurrentUser() == null) {
            return null;
        }
        return currentUser.getRole();
    }

    public void login(User newUser) {
        this.currentUser = newUser;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}