public class Session {
    private User currentUser;


    public Session(User newUser) {
        this.currentUser = newUser;
    }

    public User getCurrentUser() throws NotLoggedInException {
        if(getCurrentUser() == null) {
            throw new NotLoggedInException();
        }
        return currentUser;
    }

    public String getUserName() throws NotLoggedInException {
        if(getCurrentUser() == null) {
            throw new NotLoggedInException();
        }
        return currentUser.getUsername();
    }

    public Role getRole() throws NotLoggedInException {
        if(getCurrentUser() == null) {
            throw new NotLoggedInException();
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
