package Structures;

public class PartialAssignment {
    private final Course course;
    private final Slot slot;
    private final Lab lab;


    public PartialAssignment( Course course, Slot slot ) {
        this.course = course;
        this.slot = slot;
        this.lab = null;
    }

    public PartialAssignment(Lab lab, Slot slot){
        this.lab = lab;
        this.slot = slot;
        this.course = null;
    }

    public Course getCourse(){
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
