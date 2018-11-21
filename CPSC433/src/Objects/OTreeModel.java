package Objects;

import Structures.Assignment;
import Structures.Course;
import Structures.Slot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import java.util.LinkedList;
import java.util.Map;

public class OTreeModel {
    private HashMap<Slot, Integer> maxLabs;
    private HashMap<Slot, Integer> maxCourses;
    private FactComparator comparator;
    // Partial Assignments
    // Unwanted
    // Additional Constraints
    
    
    public OTreeModel() {
        this.maxLabs = null;
        this.maxCourses = null;
        this.comparator = null;
    }
    
    public OTreeModel(HashMap<Slot, Integer> maxLabs, HashMap<Slot, Integer> maxCourses){
        this.maxCourses = maxCourses;
        this.maxLabs = maxLabs;
        this.comparator = null;
    }
    
    private boolean solved(Fact leaf){
        return false;
    }
    
    private boolean unsolvable(Fact leaf){
        return false;
    }
    
    private ArrayList<Fact> altern(Fact leaf, Assignment g){
        
        return null;
    }
    
    public Fact guided(ArrayList<Assignment> guide){
        this.comparator = new FactComparator(guide);
        int depth = 0;
        PriorityQueue<Fact> leafs = new PriorityQueue(guide.size(), comparator);
        
        while(!leafs.isEmpty()){
            Fact leaf = leafs.poll();
            if(solved(leaf)){
                return leaf;
            } else if(!unsolvable(leaf)){ // Leaf is in guide or not, doesnt matter
                depth++;
                for(Fact fact:altern(leaf, guide.get(depth))){
                    leafs.add(fact);   
                }
            } 
        }
        return null;
    }
}

