public class DevSession extends Session {
    private final Developer dev;

    DevSession(Developer dev) {
        this.dev = dev;
    }

    public User getUser() {
        return this.dev;
    }
}
