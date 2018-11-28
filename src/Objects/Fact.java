package Objects;

import Structures.Assignment;
import Structures.Course;
import Structures.Lab;
import Structures.Lecture;
import Structures.Slot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Fact {
    private Map<Course, Slot> schedule;
    private int evaluation;
    private HashMap<Slot, Integer> numCourseSlot;
    private HashMap<Slot, Integer> numLabSlot;

     public Fact() {
            this(new HashMap<Course,Slot>(), Integer.MAX_VALUE);
     }

     public Fact(HashMap<Course, Slot> schedule) {
         this(schedule, Integer.MAX_VALUE);
     }

     public Fact(HashMap<Course,Slot> schedule, int evaluation) {
         this.schedule = schedule;
         this.evaluation = evaluation;
         this.numCourseSlot = new HashMap<>(schedule.size());
         this.numLabSlot = new HashMap<>(schedule.size());
         
         
         // Something to generate numCourseSlot and numLabSlot
         Set<Map.Entry<Course, Slot>> map = schedule.entrySet(); //Convert map to set of entries <Course,Slot>
         Iterator<Map.Entry<Course,Slot>> itor = map.iterator(); //COnvert set to an iterator
         while(itor.hasNext()){ 
             Map.Entry<Course,Slot> entry = itor.next(); // Get next entry
             Course course = entry.getKey(); // Get the course from entry
             Slot slot = entry.getValue(); // Get the slot from entry
             if(course instanceof Lecture){ // If course is a lecture 
                 // If slot already exits i slot get the Integer and increment else set to 1
                this.numCourseSlot.put(slot, this.numCourseSlot.getOrDefault(slot,0)+1);
             } else if(course instanceof Lab){ // If course is a lab
                 // If slot already exits i slot get the Integer and increment else set to 1
                this.numLabSlot.put(slot, this.numLabSlot.getOrDefault(slot,0)+1);
             } else{
                //Error checking just in case, even though this should never happen
                System.out.println("Error the new schedule has neither Course or Lab");
             }
         }
     }
     
     public Fact(Fact parent, Assignment newAssign){
         this.schedule= new HashMap(parent.getScheduel());
         this.numCourseSlot = new HashMap(parent.getNumCourses());
         this.numLabSlot = new HashMap(parent.getNumLabs());
         
         this.schedule.put(newAssign.getCourse(),newAssign.getSlot());
         Course newCourse = newAssign.getCourse();
         Slot newSlot = newAssign.getSlot();
         if(newCourse instanceof Lecture){ // If new course is a lecture
             // If slot already exits i slot get the Integer and increment else set to 1
            this.numCourseSlot.put(newSlot, this.numCourseSlot.getOrDefault(newSlot,0)+1);
         } else if(newCourse instanceof Lab) { // If new course is a lab
             // If slot already exits i slot get the Integer and increment else set to 1
            this.numLabSlot.put(newSlot, this.numLabSlot.getOrDefault(newSlot,0)+1);
         } else{
             //Error checking just in case, even though this should never happen
            System.out.println("Error the new schedule has neither Course or Lab");
         }
     }
     

     // needs
     public void setSchedule() {
         System.out.println("setSchedule is unfinished");
     }

     public int getEvaluation() {
         // call sum from other eval functions defined in assignment description
         return evaluation;
     }

     public void setEvaluation(int evaluation){
         this.evaluation = evaluation;
     }
     public Map<Course, Slot> getScheduel(){
         return schedule;
     }
     
     public HashMap<Slot, Integer> getNumCourses(){
         return numCourseSlot;
     }
     
     public HashMap<Slot, Integer> getNumLabs(){
         return numLabSlot;
     }
     
    @Override
    public String toString() {
        StringBuilder temp = new StringBuilder("Eval-value: " + evaluation + "\n");
        for (Map.Entry<Course, Slot> entry : schedule.entrySet()) {
            temp.append(String.format("%-26s:%s", entry.getKey(), entry.getValue()));
        }
        return temp.toString();
    }
}
