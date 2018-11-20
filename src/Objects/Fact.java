package Objects;

import Structures.Course;
import Structures.Slot;

import java.util.HashMap;
import java.util.Map;

public class Fact {
    private Map<Course, Slot> schedule;
    private int evaluation;

     public Fact() {
            this(new HashMap<Course,Slot>(), Integer.MAX_VALUE);
     }

     public Fact(HashMap<Course, Slot> schedule) {
         this(schedule, Integer.MAX_VALUE);
     }

     public Fact(HashMap<Course,Slot> schedule, int evaluation) {
         this.schedule = schedule;
         this.evaluation = evaluation;
     }

     // needs
     public void setSchedule() {
         System.out.println("setSchedule is unfinished");
     }

     public int getEvaluation() {
         // call sum from other eval functions defined in assignment description
         return -1;
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
