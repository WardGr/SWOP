package Domain.Task;

/**
 * Different statuses a finished task can be in, depending on when it is due and when it was finished
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