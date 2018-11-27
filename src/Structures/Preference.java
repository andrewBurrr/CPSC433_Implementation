package Structures;

public class Preference {
    private final Course course;
    private final Lab lab;
    private final Slot slot;
    private final int value;

//    public Preference(String[] input) {
//        this(input[0], input[1], input[2], input[3]);
//    }

    public Preference( Course course, Slot slot, String value) {
        this.course = course;
        this.lab = null;
        this.slot = slot;
        this.value = Integer.parseInt(value);
    }

    public Preference(Lab lab, Slot slot, String value){
        this.lab = lab;
        this.course = null;
        this.slot = slot;
        this.value = Integer.parseInt(value);
    }
    @Override
    public String toString() {
        if (course != null) {
            return String.format("%s, %s, %s, %s\n",
                    slot.getDay(), slot.getTime(), course.getIdentifier(), value);
        } else {
            return String.format("%s, %s, %s, %s\n",
                    slot.getDay(), slot.getTime(), lab.getIdentifier(), value);
        }
    }
    public Course getCourse(){ return course; }

    public Slot getSlot(){ return slot; }

    public Lab getLab(){ return lab; }

    public int getValue(){ return value; }
}
