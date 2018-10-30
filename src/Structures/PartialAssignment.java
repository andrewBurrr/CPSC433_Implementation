package Structures;

public class PartialAssignment<T1, T2, T3> {
    private final T1 identifier;
    private final T2 day;
    private final T3 time;

    public PartialAssignment( T1 identifier, T2 day, T3 time ) {
        this.identifier = identifier;
        this.day = day;
        this.time = time;
    }
    public T1 getIdentifier() { return identifier; }
    public T2 getDay() { return day; }
    public T3 getTime() { return time; }
}
