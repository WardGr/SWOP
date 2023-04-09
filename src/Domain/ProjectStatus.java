package Domain;

/**
 * Statuses a project can have
 *
 * @see Project
 */
public enum ProjectStatus {
    ONGOING {
        @Override
        public String toString() {
            return "ongoing";
        }
    },

    FINISHED {
        @Override
        public String toString() {
            return "finished";
        }
    }
}