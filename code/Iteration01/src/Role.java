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
    } // java is toch zo mooi he, toString() voor enums
}
