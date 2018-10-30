package Structures;

public class Preference<T1, T2, T3, T4> {
    private final T1 day;
    private final T2 time;
    private final T3 identifier;
    private final T4 value;

    public Preference( T1 day, T2 time, T3 identifier, T4 value ) {
        this.day = day;
        this.time = time;
        this.identifier = identifier;
        this.value = value;
    }
    public T1 getDay() { return day; }
    public T2 getTime() { return time; }
    public T3 getIdentifier() { return identifier; }
    public T4 getValue() { return value; }
}
