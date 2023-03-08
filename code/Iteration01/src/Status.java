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
    }
}
