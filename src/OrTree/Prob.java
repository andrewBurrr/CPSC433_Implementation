package OrTree;

import Objects.Fact;
import Structures.Assignment;
import Structures.Course;
import Structures.Slot;
import java.util.HashMap;

import java.util.Map;

public class Prob extends Fact{
    // and a char that can be solved unsolved or unsolvable
    private State state; // use by assigning state = State.SOLVED or any other state.

    private enum State {
        SOLVED { public String toString() { return "Yes"; } },
        UNSOLVABLE { public String toString() {return "No"; } },
        UNSOLVED { public String toString() {return "?"; } }
    }

    public Prob() {
        this(null);
    }

    public Prob(Map schedule) {
        super(new HashMap((HashMap<Course, Slot>) schedule));
        this.state = State.UNSOLVED;
    }
    
    public Prob(Map schedule, String sol){
        super(new HashMap((HashMap<Course, Slot>) schedule));
        switch(sol) {
            case "Yes": this.state = State.SOLVED;
            break;
            case "No": this.state = State.UNSOLVABLE;
            break;
            default: this.state = State.UNSOLVED;
        }
    }

    Prob(Prob leaf, Assignment assignment, String sol) {
        super(leaf, assignment);
        switch(sol) {
            case "Yes": this.state = State.SOLVED;
            break;
            case "No": this.state = State.UNSOLVABLE;
            break;
            default: this.state = State.UNSOLVED;
        }
    }
    
    public void setState(String sol){
        switch(sol) {
            case "Yes": this.state = State.SOLVED;
            break;
            case "No": this.state = State.UNSOLVABLE;
            break;
            default: this.state = State.UNSOLVED;
        }
    }
    
    public boolean isSolved(){
        return this.state == State.SOLVED; 
    }
    
    public boolean isUnsolvable(){
        return this.state == State.UNSOLVABLE;
    }
}
