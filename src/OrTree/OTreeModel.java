package OrTree;

import Parser.Reader;
import Structures.Assignment;
import Structures.Course;
import Structures.Lab;
import Structures.Lecture;
import Structures.NotCompatible;
import Structures.PartialAssignment;
import Structures.Slot;
import Structures.Unwanted;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import java.util.Set;

public class OTreeModel {
    private final Reader parser;
    // Partial Assignments
    // Unwanted
    // Additional Constraints
    
    
    public OTreeModel() {
        this.parser = null;
    }
    
    public OTreeModel(HashMap<Slot, Integer> maxLabs, HashMap<Slot, Integer> maxCourses, Reader parser){
        this.parser = parser;
    }
    
    private String getState(Prob leaf, Assignment newAsign){
        HashMap<Course, Slot> schedule = (HashMap<Course, Slot>) leaf.getScheduel();
        // Check Not Compatible set against new assignment
        for(NotCompatible notComp:parser.getNotCompatible()){
            if(notComp.getCourse(0).equals(newAsign.getCourse())){
               if(schedule.get(notComp.getCourse(1)).equals(newAsign.getSlot())){
                   return "No";
               } 
            } else if (notComp.getCourse(1).equals(newAsign.getCourse())){
                if(schedule.get(notComp.getCourse(0)).equals(newAsign.getSlot())){
                    return "No";
                }
            }
        }
        
        // Check Unwanted 
        for(Unwanted unwanted:parser.getUnwanted()){
            if(unwanted.getCourse().equals(newAsign.getCourse())){
                if(unwanted.getSlot().equals(newAsign.getSlot())){
                    return "No";
                }
            }
        }
        
        // Check courseMax
        
        
        // Check labMax
        
        
        // Check labs and courses are not at same time
        
        
        // Check additional constraints 
        int numCourseLab = parser.getCourses().size()+parser.getLabs().size();
        if(schedule.size()+1==numCourseLab){
            return "Yes";
        } 
        return "?";
    }
    
    private String getState(Prob leaf){
        return null;
    }
    
    private ArrayList<Prob> altern(Prob leaf, Course g){
        ArrayList<Prob> alterns = new ArrayList();
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
        }).map((newMap) -> new Prob(newMap)).forEachOrdered((newFact) -> {
            alterns.add(newFact);
        });
        return alterns;
    }
    
    public Prob depthFirst(){
        OrTreeControl1 control = new OrTreeControl1();
        LinkedList<Course> avaCourses = new LinkedList(parser.getCourses());
        avaCourses.addAll(parser.getLabs());
        PriorityQueue<Prob> leafs = new PriorityQueue(avaCourses.size(), control);
        
        // root node has partial assignments 
        HashMap<Course, Slot> partAssigns = new HashMap();
        parser.getPartialAssignments().forEach((assign) -> {
            partAssigns.put(assign.getCourse(),assign.getSlot());
            avaCourses.remove(assign.getCourse()); 
            //Might want to check partial assign course exists
        });
        Prob root = new Prob(partAssigns);
        leafs.add(root);
        
        while(!leafs.isEmpty()){
            Prob leaf = leafs.poll();
            if(leaf.isSolved()){
                return leaf; // Return solution
            } else if(!leaf.isUnsolvable()){ 
                Random rand = new Random();
                altern(leaf, avaCourses.remove(rand.nextInt(avaCourses.size()))).forEach((fact) -> {
                    leafs.add(fact);
                });
            } 
        }
        return null; // Should never happen unless bad input
    }
    
    public Prob guided(LinkedList<Assignment> guide){        
        // root node has partial assignments 
        HashMap<Course, Slot> partAssigns = new HashMap();
        parser.getPartialAssignments().forEach((assign) -> {
            partAssigns.put(assign.getCourse(),assign.getSlot());
            guide.remove(new Assignment(assign.getCourse(), assign.getSlot()));
            //Might want to check partial assign course exists
        });
        Prob root = new Prob(partAssigns);
        OrTreeControl2 control = new OrTreeControl2(guide.toArray(new Assignment[0]), partAssigns.size());
        PriorityQueue<Prob> leafs = new PriorityQueue(guide.size(), control);
        
        leafs.add(root);
        while(!leafs.isEmpty()){
            Prob leaf = leafs.poll();
            if(leaf.isSolved()){
                return leaf;
            } else if(!leaf.isUnsolvable()){ // Leaf is in guide or not, altern
                altern(leaf, guide.poll().getCourse()).forEach((fact) -> {
                    leafs.add(fact);
                });
            } 
        }
        return null; // shouldnt happen unless bad input
    }
}
