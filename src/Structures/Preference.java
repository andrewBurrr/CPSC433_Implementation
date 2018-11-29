package Structures;

public class Preference {
    private final Slot slot;
    private final Course course;
    private final String value;

    public Preference(String[] input) {
        this(input[0], input[1], input[2], input[3]);
    }

    public Preference( String day, String time, String course, String value ) {
        this.course = new Course(course);
        this.slot = new Slot(day, time);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s", course, slot, value);
    }

    public Course getCourse() { return course; }
    public Slot getSlot() { return slot; }
    public String getValue() { return value; }
}
