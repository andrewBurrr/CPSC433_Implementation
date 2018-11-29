package Structures;

public class PartialAssignment {
    private final Class course;
    private final Slot slot;


    public PartialAssignment( Class course, Slot slot ) {
        this.course = course;
        this.slot = slot;
    }


    public Class getCourse(){
        return course;
    }

    public Slot getSlot(){
        return slot;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s\n", course.getIdentifier(),
                slot.getDay(), slot.getTime());
    }

}
