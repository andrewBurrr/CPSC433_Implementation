package Structures;

public class PartialAssignment {
    private final String identifier;
    private final String day;
    private final String time;

    public PartialAssignment(String[] input) {
        this(input[0], input[1], input[2]);
    }

    public PartialAssignment( String identifier, String day, String time ) {
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
