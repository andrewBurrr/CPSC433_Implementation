package Objects;

import Structures.Course;
import Structures.Slot;

import java.util.LinkedList;
import java.util.Map;

public class Otree {
    private Prob root;

    public Otree(Map<Course, Slot> rootData) {
        root = new Prob(rootData, new Prob(), new LinkedList());
    }
}

