package Structures;

public class NotCompatible {
    private final Course c1;
    private final Course c2;

    public NotCompatible(Course c1, Course c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public String toString() {
        return String.format("%s, %s\n", c1.toString(), c2.toString());
    }

}
