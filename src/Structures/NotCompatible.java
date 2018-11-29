package Structures;

public class NotCompatible {
    private final Course courseX;
    private final Course courseY;

    public NotCompatible(String[] input) {
        this(input[0], input[1]);
    }

    public NotCompatible(String courseX, String courseY) {
        this.courseX = new Course(courseX);
        this.courseY = new Course(courseY);
    }

    @Override
    public String toString() {
        return String.format("%s, %s", courseX, courseY);
    }

    public Course getCourseX() { return courseX; }
    public Course getCourseY() { return courseY; }
}
