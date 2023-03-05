public class Task {
    private final String name;
    private final String description;
    private final int estimatedDuration;
    private final int acceptableDeviation;

    public Task(String name, String description, int estimatedDuration, int acceptableDeviation) {
        this.name = name;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.acceptableDeviation = acceptableDeviation;
    }

    @Override
    public String toString() {
        return  "Task Name:          " + name                + '\n' +
                "Description:        " + description         + '\n' +
                "Estimated Duration: " + estimatedDuration   + '\n' +
                "Accepted Deviation: " + acceptableDeviation + '\n';
    }

    // TODO: MOET GE EIGENLIJK BIJ EEN STRING RETURNEN OOK COPYOF DOEN? DAS TOCH EEN LIJST?
    public String getName() {
        return name;
    }
}
