package Structures;

public class Slot<T1, T2, T3, T4> {
    private final T1 day;
    private final T2 time;
    private final T3 max;
    private final T4 min;

    public Slot( T1 day, T2 time, T3 max, T4 min ) {
        this.day = day;
        this.time = time;
        this.max = max;
        this.min = min;
    }
    public T1 getDay() { return this.day; }
    public T2 getTime() { return this.time; }
    public T3 getMax() { return this.max; }
    public T4 getMin() {return this.min; }
}
