package Structures;

public class NotCompatible {
    private final Course course1;
    private final Course course2;

    public NotCompatible(Course[] input) {
        this(input[0], input[1]);
    }

    public NotCompatible(Course xIdentifier, Course yIdentifier) {
        this.course1 = xIdentifier;
        this.course2 = yIdentifier;
    }

    @Override
    public String toString() {
        return String.format("%s, %s\n", course1.toString(), course2.toString());
    }

    public Course getCourse(int num){
        switch(num){
            case 0: return course1;
            case 1: return course2;
            default: return null;
        }
    }
}
