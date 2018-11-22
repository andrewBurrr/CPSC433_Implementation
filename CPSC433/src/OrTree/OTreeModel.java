package OrTree;

import Objects.Fact;
import Parser.Reader;
import Structures.Assignment;
import Structures.Course;
import Structures.Lecture;
import Structures.Slot;
import static java.lang.Math.random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

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
    
    private ArrayList<Fact> altern(Fact leaf, Course g){
        ArrayList<Fact> alterns = new ArrayList();
        Set<Slot> slots;
        if(g instanceof Lecture){
            slots = parser.getCourseSlots();
        } else {
            slots = parser.getLabSlots();
        }
        slots.stream().map((slot) -> {
            HashMap<Course, Slot> newMap = new HashMap((HashMap<Course, Slot>) leaf.getScheduel());
            newMap.put(g,slot);
            return newMap;
        }).map((newMap) -> new Fact(newMap)).forEachOrdered((newFact) -> {
            alterns.add(newFact);
        });
        return alterns;
    }
    
    public Fact guided(ArrayList<Assignment> guide){
        OrTreeControl2 control = new OrTreeControl2(guide,this);
        int depth = 0;
        PriorityQueue<Fact> leafs = new PriorityQueue(guide.size(), control);
        
        while(!leafs.isEmpty()){
            Fact leaf = leafs.poll();
            if(solved(leaf)){
                return leaf;
            } else if(!unsolvable(leaf)){ // Leaf is in guide or not, doesnt matter
                depth++;
                for(Fact fact:altern(leaf, guide.get(depth+1).getCourse())){
                    leafs.add(fact);   
                }
            } 
        }
        return null;
    }
    
    public Fact depthFirst(){
        OrTreeControl1 control = new OrTreeControl1(this);
        LinkedList<Course> avaCourses = new LinkedList(parser.getCourses());
        avaCourses.addAll(parser.getLabs());
        PriorityQueue<Fact> leafs = new PriorityQueue(avaCourses.size(), control);
        
        while(!leafs.isEmpty()){
            Fact leaf = leafs.poll();
            if(solved(leaf)){
                return leaf; // Return solution
            } else if(!unsolvable(leaf)){ //Leaf is in guide or not, doesnt matter we do the same thing
                Random rand = new Random();
                altern(leaf, avaCourses.remove(rand.nextInt(avaCourses.size()))).forEach((fact) -> {
                    leafs.add(fact);
                });
            } 
        }
        return null; // Should never happen
    }
}

