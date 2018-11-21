package Structures;

public class Slot {
    private final String day;
    private final String time;
    private final String max;
    private final String min;

    public Slot(String[] input) {
        this(input[0], input[1], input[2], input[3]);
    }

    public Slot( String day, String time, String max, String min ) {
        this.day = day;
        this.time = time;
        this.max = max;
        this.min = min;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s\n", day, time, max, min);
    }

    public String getDay() { return this.day; }
    public String getTime() { return this.time; }
    public String getMax() { return this.max; }
    public String getMin() {return this.min; }
}
