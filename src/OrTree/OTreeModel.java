package OrTree;

import Parser.Reader;
import Structures.Assignment;
import Structures.Course;
import Structures.Lab;
import Structures.Lecture;
import Structures.NotCompatible;
import Structures.Slot;
import Structures.Unwanted;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import java.util.Set;

public class OTreeModel {
    private final Reader parser;
    private final int evening = 12;
    private Prob root;
    private Set<NotCompatible> notCompatible;
    private LinkedList<Course> usedCourses;
    
    
    public OTreeModel() {
        this.parser = null;
        this.root = null;
    }
    
    public OTreeModel(Reader parser){
        this.parser = parser;
        
        // Get partial assignments
        HashMap<Course, Slot> partAssign = new HashMap<>();
        parser.getPartialAssignments().forEach((assign) -> {
            partAssign.put(assign.getCourse(),assign.getSlot());
            usedCourses.remove(assign.getCourse());
        });
        
        // Check for CPSC 313 and 413 and add 813/913 and their not-compatibles
        notCompatible = parser.getNotCompatible();
        // Check for CPSC 313
        Set<Lecture> courses = parser.getCourses();
        if(courses.contains(new Lecture("CPSC 313 LEC 01"))){
            partAssign.put(new Lab("CPSC 813 TUT 01"), new Slot("TU","18:00", Integer.MAX_VALUE,0));
            Iterator<NotCompatible> itor = notCompatible.iterator();
            while(itor.hasNext()){
                NotCompatible noPair = itor.next();
                if(noPair.getCourse(0).equals(new Lecture("CPSC 313 LEC 01"))){
                    notCompatible.add(new NotCompatible(noPair.getCourse(0), new Lab("CPSC 813 TUT 01")));
                }
            }
        } 
        // Check for CPSC 413
        if(courses.contains(new Lecture("CPSC 413 LEC 01"))){
            partAssign.put(new Lab("CPSC 913 TUT 01"), new Slot("TU","18:00", Integer.MAX_VALUE,0));
            Iterator<NotCompatible> itor = notCompatible.iterator();
            while(itor.hasNext()){
                NotCompatible noPair = itor.next();
                if(noPair.getCourse(0).equals(new Lecture("CPSC 413 LEC 01"))){
                    notCompatible.add(new NotCompatible(noPair.getCourse(0), new Lab("CPSC 913 TUT 01")));
                }
            }
        }
        
        Prob part = checkPartials(partAssign);
        if(part==null){
            System.out.println("Error: Partial assignments are not valid");
        } else{
            root = part;
        }
    }
    
    /**
     * Checks the state of a parent with a parent and a new assignment.
     * @param parent
     * @param newAsign
     * @return 
     */
    private String getState(Prob parent, Assignment newAsign){
        Course newCourse = newAsign.getCourse();
        Slot newSlot = newAsign.getSlot();
        HashMap<Course, Slot> schedule = (HashMap<Course, Slot>) parent.getScheduel();
        
        // Check Not Compatible set against new assignment
        for(NotCompatible notComp:notCompatible){
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
        Set<Unwanted> unwanted = parser.getUnwanted();
        if(unwanted.contains(new Unwanted(newCourse, newSlot))){ // Redefine equals for Unwanted
            return "No";
        }
        
        // Check courseMax
        if(newCourse instanceof Lecture){
            HashMap<Slot, Integer> numCourse = parent.getNumCourses();
            if(numCourse.containsKey(newSlot)){
                if(numCourse.get(newSlot)+1 >= newSlot.getMax()){
                    return "No";
                }
            }
        } else{        // Check labMax
           HashMap<Slot, Integer> numLab = parent.getNumLabs();
            if(numLab.containsKey(newSlot)){
                if(numLab.get(newSlot)+1 >= newSlot.getMax()){
                    return "No";
                }
            } 
        }
    
        // Check labs and courses are not at same time
        if(newCourse instanceof Lecture){
            Lecture newLecture = (Lecture) newCourse;
            Set<Lab> labs = parser.getCourseLabs().get(newLecture);
            for(Lab lab:labs){
                if(schedule.get(lab).equals(newSlot)){
                    return "No";
                }
            }
        } else{ // Check to see if lab conflicts with lecture 
            Iterator<Map.Entry<Lecture, Set<Lab>>> map = parser.getCourseLabs().entrySet().iterator();
            outerloop:
            while(map.hasNext()){
                Map.Entry<Lecture, Set<Lab>> entry = map.next();
                Lecture lecture = entry.getKey();
                Set<Lab> labs = entry.getValue();
                for(Lab lab:labs){
                    if(lab.equals((Lab) newCourse)){
                        if(schedule.get(lab).equals(schedule.get(lecture))){
                            return "No";
                        }
                        break outerloop;
                    }
                }
            }
        }
        
        // Check additional constraints
        // Nothing at TU 11:00-12:30
        if(newSlot.getDay().equals("TU") && newSlot.getTime().equals("11:00")){
            return "No";
        }
        
        // Check to make sure all Lec 09 are after evening(variable [0,24])
        if(newCourse.getIdentifier().matches("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+(09)+[\\s]*")){
            if(Integer.parseInt(newSlot.getTime().substring(0,2))<evening){
                return "No";
            }
        }
        
        // Check 500-level classes dont conflict
        if(newCourse.getIdentifier().matches("[\\s]*(CPSC)[\\s]+(5)+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*")){
            if(parent.get500Slots().contains(newSlot)){
                return "No";
            }
        }
        
        
        // Check if all labs and courses are scheduled 
        int numCourseLab = parser.getCourses().size()+parser.getLabs().size();
        if(schedule.size()+1==numCourseLab){
            return "Yes";
        } 
        return "?";
    }
    
    /**
     * Checks the state of a schedule with no parent.
     * @param schedule
     * @return 
     */
    private Prob checkPartials(HashMap<Course, Slot> schedule){
        Set<Map.Entry<Course, Slot>> map = schedule.entrySet();
        Iterator<Map.Entry<Course, Slot>> itor = map.iterator();
        HashSet<Slot> num500 = new HashSet();
        
        // Iterate over all assignments checking one-side of constraints for each
        while(itor.hasNext()){
            Map.Entry<Course, Slot> entry = itor.next();
            Course course = entry.getKey();
            Slot slot = entry.getValue();
            
            // Check not compatible set
            for(NotCompatible notComp:notCompatible){
                if(notComp.getCourse(0).equals(course)){
                    if(schedule.get(notComp.getCourse(1)).equals(slot)){
                        return null;
                    }
                }
            }
            
            // Check Unwanted
            Set<Unwanted> unwanted = parser.getUnwanted();
            if(unwanted.contains(new Unwanted(course, slot))){
                return null;
            }
            
            // Check labs and courses are not at the same time 
            if(course instanceof Lecture){ // Check to see if lecture conflicts
                Lecture newLecture = (Lecture) course;
                Set<Lab> labs = parser.getCourseLabs().get(newLecture);
                for(Lab lab:labs){
                    if(slot.equals(schedule.get(lab))){
                        return null;
                    }
                }
            } 
            
            // Check no lecture at TU 11:00-12:30
            if(course instanceof Lecture){
                if(slot.getDay().equals("TU") && slot.getTime().equals("11:00")){
                    return null;
                }
            }
            // Check to make sure all Lec 09 are after evening(variable [0,24])
            if(course.getIdentifier().matches("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+(09)+[\\s]*")){
                if(Integer.parseInt(slot.getTime().substring(0,2))<evening){
                    return null;
                }
            }
             // Check 500-level classes dont conflict
             if(course.getIdentifier().matches("[\\s]*(CPSC)[\\s]+(5)+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*")){
                if(num500.contains(slot)){
                    return null;
                } else{
                    num500.add(slot);
                }
             }
        }
        
        Prob part = new Prob(schedule);
        
        // Check max course
        Iterator<Map.Entry<Slot, Integer>> itorCMax = part.getNumCourses().entrySet().iterator();
        while(itorCMax.hasNext()){
            Map.Entry<Slot, Integer> entrCMax = itorCMax.next();
            if(entrCMax.getKey().getMax()<entrCMax.getValue()){
                return null;
            }
        }
        
        // Check max lab
        Iterator<Map.Entry<Slot, Integer>> itorLMax = part.getNumLabs().entrySet().iterator();
        while(itorLMax.hasNext()){
            Map.Entry<Slot, Integer> entrLMax = itorLMax.next();
            if(entrLMax.getKey().getMax()<entrLMax.getValue()){
                return null;
            }
        }
        
        // Check if all labs and courses are scheduled 
        int numCourseLab = parser.getCourses().size()+parser.getLabs().size();
        if(schedule.size()+1==numCourseLab){
            part.setState("Yes");
            return part;
        } 
        return part;
    }
    
    private ArrayList<Prob> altern(Prob leaf, Course g){
        ArrayList<Prob> alterns = new ArrayList();
        Set<Slot> slots;
        if(g instanceof Lecture){
            slots = parser.getCourseSlots();
        } else {
            slots = parser.getLabSlots();
        }
        slots.forEach((slot) -> {
            Assignment newAsign = new Assignment(g,slot);
            alterns.add(new Prob(leaf, newAsign, getState(leaf,newAsign)));
        });
        return alterns;
    }
    
    public Prob depthFirst(){
        LinkedList<Course> avaCourses = new LinkedList(parser.getCourses());
        avaCourses.addAll(parser.getLabs());
        avaCourses.removeAll(usedCourses);
        OrTreeControl1 control = new OrTreeControl1();
        PriorityQueue<Prob> leafs = new PriorityQueue(avaCourses.size()*avaCourses.size(), control);
        
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
        guide.stream().filter((assign) -> (usedCourses.contains(assign.getCourse()))).forEachOrdered((assign) -> {
            guide.remove(assign);
        });
        OrTreeControl2 control = new OrTreeControl2(guide.toArray(new Assignment[0]), usedCourses.size());
        PriorityQueue<Prob> leafs = new PriorityQueue(guide.size()*guide.size(), control);
        
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

