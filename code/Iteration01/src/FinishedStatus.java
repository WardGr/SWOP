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
