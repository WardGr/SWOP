package Domain;

import Domain.TaskStates.Task;

/**
 * Statuses a task can have
 *
 * @see Task
 */
public enum Status {
    AVAILABLE {
        @Override
        public String toString() {
            return "available";
        }
    },
    UNAVAILABLE {
        @Override
        public String toString() {
            return "unavailable";
        }
    },
    EXECUTING {
        @Override
        public String toString() {
            return "executing";
        }
    },
    FINISHED {
        @Override
        public String toString() {
            return "finished";
        }
    },
    FAILED {
        @Override
        public String toString() {
            return "failed";
        }
    },
    PENDING {
        @Override
        public String toString() {
            return "pending";
        }
    },
}
