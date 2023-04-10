package Domain;

/**
 * The different user roles
 */
public enum Role {
    SYSADMIN {
        @Override
        public String toString() {
            return "system administration developer";
        }
    },

    JAVAPROGRAMMER {
        @Override
        public String toString() {
            return "Java programmer";
        }
    },

    PYTHONPROGRAMMER {
        @Override
        public String toString() {
            return "Python programmer";
        }
    },

    PROJECTMANAGER {
        @Override
        public String toString() {
            return "project manager";
        }
    },
}
