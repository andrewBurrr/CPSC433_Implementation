package Objects;

import Structures.Course;
import Structures.Slot;

import java.util.LinkedList;
import java.util.Map;

public class OTree {
    private Prob root;
    
    public OTree(Map<Course, Slot> rootData) {
        root = new Prob(rootData, new Prob(), new LinkedList());
    }
}

