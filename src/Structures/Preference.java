package Structures;

public class Preference {
    private final String day;
    private final String time;
    private final String identifier;
    private final String value;

    public Preference(String[] input) {
        this(input[0], input[1], input[2], input[3]);
    }

    public Preference( String day, String time, String identifier, String value ) {
        this.day = day;
        this.time = time;
        this.identifier = identifier;
        this.value = value;
    }
    public String getDay() { return day; }
    public String getTime() { return time; }
    public String getIdentifier() { return identifier; }
    public String getValue() { return value; }
}
