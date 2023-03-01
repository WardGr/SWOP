public class PMSession extends Session {
    private final ProjectManager PM;

    public PMSession(ProjectManager pm) {
        PM = pm;
    }

    public User getUser() {
        return PM;
    }
}
