package Structures;

public class Unwanted {
    private final Course course;
    private final Slot slot;

    public Unwanted(Course course, Slot slot) {
        this.course = course;
        this.slot = slot;
    }

    public Course getCourse(){
        return course;
    }
    
    public Slot getSlot(){
        return slot;
    }
    
    @Override
    public String toString() {
        return String.format("%s, %s\n", course.toString(), slot.toString());
    }
}
