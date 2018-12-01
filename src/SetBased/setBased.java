package SetBased;

import Exceptions.InvalidInputException;
import OrTree.OTreeModel;
import Structures.Lab;
import Structures.Lecture;
import Structures.Slot;
import Parser.Reader;
import OrTree.Prob;
import Structures.Assignment;
import Structures.Course;
import com.sun.prism.shape.ShapeRep;

import java.util.*;


public class setBased {
    private List<Fact> Facts;
    private int threshold;
    private int maxPopulation;
    private Reader reader;
    private OTreeModel oTree;
    private Set<Course> courseLab;
    
    //TODO: Implement Mutation according to setBasedBreakDown, return a Fact newFact
    private Fact Mutation(){
        Random random = new Random();
        
        //Create a Deep Copy of a random Fact, then getSChedule()
        Fact mutFact = new Fact(Facts.get(random.nextInt(Facts.size()-1))); 
        Map<Course, Slot> mutSchedule = mutFact.getScheduel();
        
        // Get a random course (lab or lecture) from scheduel
        Course mutCourse = mutSchedule.keySet().toArray(new Course[0])[random.nextInt(mutSchedule.size())];
        Slot newSlot;
        // Make sure new slot for mutCourse is of right type and is different
        if(mutCourse instanceof Lecture){ // Lecture
            newSlot = reader.getCourseSlots().toArray(new Slot[0])[random.nextInt(reader.getCourseSlots().size())];
            while(newSlot.equals(mutSchedule.get(mutCourse))){
                newSlot = reader.getCourseSlots().toArray(new Slot[0])[random.nextInt(reader.getCourseSlots().size())];
            }
        } else { // Lab
            newSlot = reader.getCourseSlots().toArray(new Slot[0])[random.nextInt(reader.getLabSlots().size())];
            while(newSlot.equals(mutSchedule.get(mutCourse))){
                newSlot = reader.getCourseSlots().toArray(new Slot[0])[random.nextInt(reader.getLabSlots().size())];
            }
        }
        // Remove course from scheduel 
        mutSchedule.remove(mutCourse);
        // Create arraylist for guide and create iterator to add non-changed assigns
        ArrayList<Assignment> guide = new ArrayList(mutSchedule.size());
        guide.add(new Assignment(mutCourse, newSlot));
        Iterator<Map.Entry<Course, Slot>> itor = mutSchedule.entrySet().iterator();
        
        while(itor.hasNext()) {
            Map.Entry<Course, Slot> entry = itor.next();
            guide.add(new Assignment(entry.getKey(), entry.getValue()));
        }
        
        Fact newFact = (Fact) oTree.guided(guide);
        if(newFact != null){
            newFact.setEvaluation(Eval(newFact));
        }

        return newFact;

    }
    
    //TODO: Implement Combination according to setBasedBreakDown, return two random new Fact
    private Fact[] Combination(){
        Random rand = new Random();
        
        HashMap<Course, Slot> p1Map = new HashMap(Facts.get(rand.nextInt(Facts.size()-1)).getScheduel());
        HashMap<Course, Slot> p2Map = new HashMap(Facts.get(rand.nextInt(Facts.size()-1)).getScheduel());
        while(p2Map.equals(p1Map)){
            p2Map = new HashMap(Facts.get(rand.nextInt(Facts.size()-1)).getScheduel());
        }
        
        ArrayList<Assignment> guide1 = new ArrayList(p1Map.size());
        ArrayList<Assignment> guide2 = new ArrayList(p1Map.size());
        
        Iterator<Map.Entry<Course, Slot>> itor = p1Map.entrySet().iterator();
        while(itor.hasNext()) {
            Map.Entry<Course, Slot> entry = itor.next();
            if(rand.nextBoolean()){
                guide1.add(new Assignment(entry.getKey(), entry.getValue()));
                guide2.add(new Assignment(entry.getKey(), p2Map.get(entry.getKey())));
            } else {
                guide2.add(new Assignment(entry.getKey(), entry.getValue()));
                guide1.add(new Assignment(entry.getKey(), p2Map.get(entry.getKey())));
            }
        }
        
        Fact[] newFacts = new Fact[2];
        
        Fact newFact1 = (Fact) oTree.guided(guide1);
        newFact1.setEvaluation(Eval(newFact1));
        Fact newFact2 = (Fact) oTree.guided(guide2);
        newFact2.setEvaluation(Eval(newFact2));
        
        newFacts[0] = newFact1;
        newFacts[1] = newFact2;
        
        return newFacts;
    }

    //TODO: Implement Tod
    private void Tod(){
        int killPercent;
    }

    //TODO: Implement Eval to calculate the soft constraint, take in a Fact, return an int
    private int Eval(Fact solution){
        return 0;
    }

    //TODO: Calculate the Variance of the current Facts, return a float
    private float getVariance(Set<Fact> Facts){
        return 0.0f;
    }

    public void run()  {
        if (Facts.isEmpty()) {
            //Run OTree.depthFirst()
            System.out.println("running depthFirst");
            Facts.add(oTree.depthFirst());
        }
        Facts.add(Mutation());

//            while (true) {
//                //If Facts is empty we run depthFirst
//                //If Facts are too big, kill them off with Tod()
//                if (Facts.size() > maxPopulation) {
//                    Tod();
//                } else {
//                    //Randomly choose between Mutation and Combination in search control
//                    if (new Random().nextInt(1) == 1) {
//                        //Mutation return a newFact
//                        Fact newFact = Mutation(reader);
//                        //Calculate the soft constraint of the newFact
//                        newFact.setEvaluation(Eval(newFact));
//                        //Add to our current set of solution
//                        //TODO: Only add to Facts if newFact.evaluation < currentBestSolution/some other values
//                        Facts.add(newFact);
//                    } else {
//                        //Combination return an array of newFact
//                        Fact[] newFact = Combination();
//                        //A loop to calculate the soft constraint for newFact[i]
//                        for (int i = 0; i < newFact.length; i++) {
//                            newFact[i].setEvaluation(Eval(newFact[i]));
//                            //TODO: Only add to Facts if newFact.evaluation < currentBestSolution/some other values
//                            Facts.add(newFact[i]);
//                        }
//                    }
//                }
//                //Calculate the variance of our current Facts, if variance < threshold quit the setBased
//                float currentVariance = getVariance(Facts);
//                if (currentVariance <= threshold) {
//                    System.out.println("The best solutions are found, terminating the setBased");
//                    break;
//                }
//            }
//        }
    }
    //This is the main function in setBased
    public setBased(Reader reader, OTreeModel oTree){
        //Initialize the setBased environment
         threshold = 0;
         maxPopulation = 0;
         Facts = new ArrayList<Fact>();
        this.reader = reader;
        this.oTree = oTree;
        this.courseLab = new LinkedHashSet(reader.getCourses());
        this.courseLab.addAll(reader.getLabs());

    }

    @Override
    //TODO: Implement toString to properly display the result after we finish searching, return a String of all
    //TODO: the current set of solution in Facts as well as the evaluation in correct format
    public String toString() {
        String statement = "";
        if (Facts == null) {
            return "Facts is null";
        } else {
            for (Fact fact : Facts) {
                statement += fact.toString();
            }
            return statement;
        }
    }
}
