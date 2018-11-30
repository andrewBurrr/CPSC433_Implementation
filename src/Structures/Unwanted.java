package Structures;

import java.util.Arrays;

public class Unwanted {
    private final Course course;
    private final Slot slot;

    public Unwanted(Course course, Slot slot) {
        this.course = course;
        this.slot = slot;
    }

    public Unwanted(String[] input){
        this(input[0], Arrays.copyOfRange(input, 1, 3));
    }
    
    public Unwanted(String course, String[] slot){
        this.slot = new Slot(slot);
        if(course.matches(".*(TUT|LAB).*")){ 
            this.course = new Lab(course);
        } else{
            this.course = new Lecture(course);
        }
    }
    
    public Course getCourse(){
        return course;
    }
    
    public Slot getSlot(){
        return slot;
    }
    
    @Override
    public String toString() {
        return String.format("%-26s\t:%s\n", course.toString(),
                slot.toString());
    }
}
