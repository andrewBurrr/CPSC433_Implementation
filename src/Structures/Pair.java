package Structures;

import java.util.HashMap;
import java.util.Map;

public class Pair {
    private final Map<Course, Course> courseCourse;

    public Pair(HashMap<Course, Course> CourseCourse) {
        this.courseCourse = CourseCourse;
    }

    @Override
    public String toString() {
        Course course1 = (Course) courseCourse.keySet().toArray()[0];
        return String.format("%s, %s\n", course1.getIdentifier(),
                    courseCourse.get(course1).getIdentifier());
    }

//    public String getxIdentifier() { return xIdentifier; }
//    public String getyIdentifier() { return yIdentifier; }
}
