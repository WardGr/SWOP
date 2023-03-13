public enum Role {
  DEVELOPER {
    @Override
    public String toString() {
      return "developer";
    }
  },
  PROJECTMANAGER {
    @Override
    public String toString() {
      return "project manager";
    }
  },
}
