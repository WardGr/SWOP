package Domain.DataClasses;

public record Tuple<T1, T2>(T1 first, T2 second) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple<?, ?> t2) {
            return this.first().equals(t2.first()) && this.second().equals(t2.second());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return second().hashCode();
    }

    @Override
    public String toString() {
        return "(" + first().toString() + ", " + second().toString() + ")";
    }
}