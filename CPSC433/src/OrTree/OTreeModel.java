package OrTree;

import Objects.Fact;
import OrTree.OrTreeControl2;
import Parser.Reader;
import Structures.Assignment;
import Structures.Course;
import Structures.Lecture;
import Structures.Slot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import java.util.Set;

public class OTreeModel {
    private final HashMap<Slot, Integer> maxLabs;
    private final HashMap<Slot, Integer> maxCourses;
    private final Reader parser;
    // Partial Assignments
    // Unwanted
    // Additional Constraints
    
    
    public OTreeModel() {
        this.maxLabs = null;
        this.maxCourses = null;
        this.parser = null;
    }
    
    public OTreeModel(HashMap<Slot, Integer> maxLabs, HashMap<Slot, Integer> maxCourses, Reader parser){
        this.maxCourses = maxCourses;
        this.maxLabs = maxLabs;
        this.parser = parser;
    }
    
    public boolean solved(Fact leaf){
        return false;
    }
    
    public boolean unsolvable(Fact leaf){
        return false;
    }
    
    private ArrayList<Fact> altern(Fact leaf, Assignment g){
        ArrayList<Fact> alterns = new ArrayList();
        Set<Slot> slots;
        if(g.getCourse() instanceof Lecture){
            slots = parser.getCourseSlots();
        } else {
            slots = parser.getLabSlots();
        }
        for(Slot slot:slots){
            HashMap<Course, Slot> newMap = new HashMap((HashMap<Course, Slot>) leaf.getScheduel());
            newMap.put(g.getCourse(),slot);
            Fact newFact = new Fact(newMap);
            alterns.add(newFact);
        }
        return alterns;
    }
    
    public Fact guided(ArrayList<Assignment> guide){
        OrTreeControl2 comparator = new OrTreeControl2(guide,this);
        int depth = 0;
        PriorityQueue<Fact> leafs = new PriorityQueue(guide.size(), comparator);
        
        while(!leafs.isEmpty()){
            Fact leaf = leafs.poll();
            if(solved(leaf)){
                return leaf;
            } else if(!unsolvable(leaf)){ // Leaf is in guide or not, doesnt matter
                depth++;
                for(Fact fact:altern(leaf, guide.get(depth+1))){
                    leafs.add(fact);   
                }
            } 
        }
        return null;
    }
    
    public Fact depthFirst(){
        return null;
    }
}

