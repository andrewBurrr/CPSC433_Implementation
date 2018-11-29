package OrTree;

import Objects.Fact;
import Structures.Assignment;
import Structures.Class;
import Structures.Slot;
import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;
import java.util.Set;

public class Prob extends Fact{
    // and a char that can be solved unsolved or unsolvable
    private State state; // use by assigning state = State.SOLVED or any other state.
    private Set<Slot> slots500;
    private enum State {
        SOLVED { public String toString() { return "Yes"; } },
        UNSOLVABLE { public String toString() {return "No"; } },
        UNSOLVED { public String toString() {return "?"; } }
    }

    public Prob() {
        this(null);
    }

    public Prob(Map schedule) {
        this(schedule, "?");
    }
    
    public Prob(Map schedule, String sol){
        super(new HashMap((HashMap<Class, Slot>) schedule));
        switch(sol) {
            case "Yes": this.state = State.SOLVED;
            break;
            case "No": this.state = State.UNSOLVABLE;
            break;
            default: this.state = State.UNSOLVED;
        }
        // Might want to move this into OTreeModel
        Iterator<Map.Entry<Class, Slot>> itor = schedule.entrySet().iterator();
        while(itor.hasNext()){
            Map.Entry<Class, Slot> entry = itor.next();
            Class course = entry.getKey();
            Slot slot = entry.getValue();
            if(course.getIdentifier().matches("[\\s]*(CPSC)[\\s]+(5)+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*")){
                this.slots500.add(slot);
            }
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
        this.slots500 = leaf.slots500;
        if(assignment.getCourse().getIdentifier().matches("[\\s]*(CPSC)[\\s]+(5)+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*")){
            this.slots500.add(assignment.getSlot());
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
    
    public Set<Slot> get500Slots(){
        return slots500;
    }
}
