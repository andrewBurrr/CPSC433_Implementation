package Structures;

import Exceptions.InvalidInputException;

import java.util.HashMap;
import java.util.Map;

public class NotCompatible {
    private final Map<Course, Course> courseCourse;
    private final Map<Course, Lab> courseLab;
    private final Map<Lab, Lab> labLab;

    public NotCompatible(HashMap<Course, Course> CourseCourse, HashMap<Course, Lab> CourseLab,
                         HashMap <Lab, Lab> LabLab) {
        this.courseCourse = CourseCourse;
        this.courseLab = CourseLab;
        this.labLab = LabLab;
    }

    @Override
    public String toString() {
        if (!courseCourse.isEmpty()){
            Course course1 = (Course) courseCourse.keySet().toArray()[0];
            return String.format("%s, %s\n", course1.getIdentifier(),
                    courseCourse.get(course1).getIdentifier());
        } else if(!courseLab.isEmpty()){
            Course course1 = (Course) courseLab.keySet().toArray()[0];
            return String.format("$s, %s\n", course1.getIdentifier(),
                    courseLab.get(course1).getIdentifier());
        } else{
            Lab lab1 = (Lab) labLab.keySet().toArray()[0];
            return String.format("%s, %s\n", lab1, labLab.get(lab1).getIdentifier());
        }
    }

    public Map<Course, Course> getCourseCourse(){ return courseCourse;}
    public Map<Course,Lab> getCourseLab(){return courseLab;}
    public Map<Lab, Lab> getLabLab(){return labLab;}
}
