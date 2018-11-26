package Structures;

public class Slot {
    private final String day;
    private final String time;
    private final int max;
    private final int min;

    public Slot(String[] input) {
        this(input[0], input[1], input[2], input[3]);
    }

    public Slot( String day, String time, String max, String min ) {
        this.day = day;
        this.time = time;
        this.max = Integer.parseInt(max);
        this.min = Integer.parseInt(min);
    }
        public Slot( String day, String time, int max, int min ) {
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
    public int getMax() { return this.max; }
    public int getMin() {return this.min; }
}
