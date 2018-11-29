package Structures;

public class Pair {
    private final Course courseX;
    private final Course courseY;

    public Pair( String[] input ) {
        this(input[0], input[1]);
    }

    public Pair( String courseX, String courseY ) {
        this.courseX = new Course(courseX);
        this.courseY = new Course(courseY);
    }

    @Override
    public String toString() { return String.format("%s, %s", courseX, courseY); }

    public Course getCourseX() { return courseX; }
    public Course getCourseY() { return courseY; }
}
