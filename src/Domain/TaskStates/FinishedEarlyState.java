package Domain.TaskStates;

// Ik doe het nu gewoon met polymorfisme, in case er extra functionaliteit komt bij specifieke finished states
public class FinishedEarlyState extends FinishedState {
    @Override
    public String toString() {
        return "finished, early";
    }

}
