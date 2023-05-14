package Domain;

public class Tuple<T1,T2> {
    private final T1 first;
    private final T2 second;

    public Tuple(T1 first, T2 second){
        this.first = first;
        this.second = second;
    }
    public T1 getFirst(){
        return first;
    }
    public T2 getSecond(){
        return second;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple<?, ?> t2) {
            return this.getFirst().equals(t2.getFirst()) && this.getSecond().equals(t2.getSecond());
        }
        return false;
    }
}
