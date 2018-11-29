package Structures;

public class Course {

    private final String course;

    public Course(String course) { this.course = course; }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Course) return this.course.equals(((Course) object).getCourse());
        else return false;
    }

    @Override
    public String toString() { return String.format("%s", course); }

    public String getCourse() { return course; }
}
