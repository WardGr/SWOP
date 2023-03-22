package Domain;

/**
 * Different statuses a finished project can be in, depending on it's timing
 */
public enum FinishedStatus {
    EARLY {
        @Override
        public String toString() {
            return "early";
        }
    },
    ON_TIME {
        @Override
        public String toString() {
            return "on time";
        }
    },
    DELAYED {
        @Override
        public String toString() {
            return "delayed";
        }
    }
}
