package Structures;

public class Unwanted {
    private final String identifier;
    private final String day;
    private final String time;

    public Unwanted(String[] input) {
        this(input[0], input[1], input[2]);
    }

    public Unwanted(String identifier, String day, String time) {
        this.identifier = identifier;
        this.day = day;
        this.time = time;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s\n", identifier, day, time);
    }

    public String getIdentifier() { return identifier; }
    public String getDay() { return day; }
    public String getTime() { return time; }
}
