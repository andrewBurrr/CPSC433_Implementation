package Structures;

public class Unwanted {
    private final Course course;
    private final Slot slot;

    public Unwanted(String[] input) {
        this(input[0], input[1], input[2]);
    }

    public Unwanted(String course, String day, String time) {
        this.course = new Course(course);
        this.slot = new Slot(day, time);
    }

    @Override
    public String toString() {
        return String.format("%s, %s", course, slot);
    }

    public Course getCourse() { return course; }
    public Slot getSlot() {return slot; }
}
