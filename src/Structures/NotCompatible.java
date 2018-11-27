package Structures;

import java.util.HashMap;
import java.util.Map;

public class NotCompatible {
    private final Map<Course, Course> courseCourse;

    public NotCompatible(HashMap<Course, Course> CourseCourse) {
        this.courseCourse = CourseCourse;
    }

    @Override
    public String toString() {
        Course course1 = (Course) courseCourse.keySet().toArray()[0];
        return String.format("%s, %s\n", course1.getIdentifier(),
                courseCourse.get(course1).getIdentifier());
    }

    public Map<Course, Course> getCourse(){ return courseCourse;}
}
