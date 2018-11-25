package Objects;

import Structures.Assignment;
import Structures.Course;
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
         // Something to generate numCourseSlot and numLabSlot
         Set<Map.Entry<Course, Slot>> map = schedule.entrySet();
         Iterator<Map.Entry<Course,Slot>> intor = map.iterator();
         while(intor.hasNext()){
             Map.Entry<Course,Slot> entry = intor.next();
             Course course = entry.getKey();
             Slot slot = entry.getValue();
             if(course instanceof Lecture){
                this.numCourseSlot.put(slot, this.numCourseSlot.getOrDefault(slot,0)+1);
             } else {
                 this.numLabSlot.put(slot, this.numLabSlot.getOrDefault(slot,0)+1);
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
         if(newCourse instanceof Lecture){
             if(this.numCourseSlot.containsKey(newSlot)){
                 Integer put = this.numCourseSlot.get(newSlot)+1;
                 this.numCourseSlot.put(newSlot, put);
             } else {
                 this.numCourseSlot.put(newSlot, 1);
             }
         } else {
             if(this.numLabSlot.containsKey(newSlot)){
                 Integer put = this.numLabSlot.get(newSlot)+1;
                 this.numLabSlot.put(newSlot, put);
             } else {
                 this.numLabSlot.put(newSlot, 1);
             }
         }
     }
     

     // needs
     public void setSchedule() {
         System.out.println("setSchedule is unfinished");
     }

     public int getEvaluation() {
         // call sum from other eval functions defined in assignment description
         return -1;
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
