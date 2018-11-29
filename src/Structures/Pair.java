package Structures;

public class Pair {
    private final Class c1;
    private final Class c2;

    public Pair(Course c1, Course c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public String toString() {
        return String.format("%s, %s\n", c1.toString(), c2.toString());
    }
}
