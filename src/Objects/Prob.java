package Objects;

import Structures.Course;
import Structures.Slot;

import java.util.List;
import java.util.Map;

public class Prob {
    // prob has a schedule map as data
    // and a char that can be solved unsolved or unsolvable
    private Map<Course, Slot> schedule;
    private State state; // use by assigning state = State.SOLVED or any other state.
    private Prob parent;
    private List<Prob> children;

    private enum State {
        SOLVED { public String toString() { return "Yes"; } },
        UNSOLVED { public String toString() {return "No"; } },
        UNSOLVABLE { public String toString() {return "?"; } }
    }

    public Prob() {
        this(null, null, null);
    }

    public Prob(Map schedule, Prob parent, List children) {
        this.schedule = schedule;
        this.parent = parent;
        this.children = children;
    }

    public boolean isValid() {
        // do stuff to check partial solution constraints
        return true;
    }

    public void setSchedule() {}

    public void setState() {}

    public void setChildren() {}

}
