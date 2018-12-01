package SetBased;

import Exceptions.InvalidInputException;
import OrTree.OTreeModel;
import Structures.Lab;
import Structures.Lecture;
import Structures.Slot;
import Parser.Reader;
import OrTree.Prob;
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
        Fact mutationFact = new Fact(Facts.get(random.nextInt(Facts.size())));
        Map<Course, Slot> mutationSchedule = mutationFact.getScheduel();

        // get courses in schedule as an Array
        ArrayList mutationCoursesArray = new ArrayList(mutationFact.getScheduel().keySet());
        // Get a random course to be replace
        Object courseToBeReplaced = mutationCoursesArray.get(random.nextInt(mutationCoursesArray.size()));

        // actual mutation
        System.out.println("Mutating");
        //If courseToBeReplace is instance of Lab
        if(courseToBeReplaced instanceof Lab){
            //Getting a random newSlot to mutate
            List<Slot> labSlot = new ArrayList<>(reader.getCourseSlots());
            Slot newSlot = labSlot.get(random.nextInt(labSlot.size()));
            //Getting the numLab and decrement the number of Lab assigned to the Slot
            HashMap <Slot, Integer> numLab = mutationFact.getNumLabs();
            //When we take out the lab from the current Slot, we decrement the numLab at that Slot by 1
            numLab.replace(mutationSchedule.get(courseToBeReplaced), numLab.get(mutationSchedule.get(courseToBeReplaced)) - 1);
            //We assign the Lab to a new Slot
            mutationSchedule.replace((Lab) courseToBeReplaced, newSlot);
            //Increment the Slot that have just been assigned a new Lab by 1
            //If previously the numLab contains the newSlot, increment by 1
            if(numLab.containsKey(newSlot)){
                numLab.replace(newSlot, numLab.get(newSlot) + 1);
            }else{
                //Else initialize the map <newSLot, 1>
                numLab.put(newSlot, 1);
            }
            //Or instance of Lecture
        } else if(courseToBeReplaced instanceof Lecture){
            //Getting a random newSlot to mutate
            List<Slot> courseSlot = new ArrayList<>(reader.getCourseSlots());
            Slot newSlot = courseSlot.get(random.nextInt(courseSlot.size()));
            //Getting the numCourses
            HashMap<Slot, Integer> numCourses = mutationFact.getNumCourses();
            //When we take out the course from the current Slot, we decrement by 1
            numCourses.replace(mutationSchedule.get(courseToBeReplaced), numCourses.get(mutationSchedule.get(courseToBeReplaced)) - 1);
            //We assign the course to a new slot
            mutationSchedule.replace((Lecture) courseToBeReplaced, newSlot);
            //We increment the number of course in that slot if it exist
            if(numCourses.containsKey(newSlot)){
                numCourses.replace(newSlot, numCourses.get(newSlot) + 1);
            }else{
                //Otherwise add map<newSLot, 1>
                numCourses.put(newSlot, 1);
            }
        }
        mutationFact.setSchedule(mutationSchedule);
        return mutationFact;

    }
    //TODO: Implement Combination according to setBasedBreakDown, return two random new Fact
    private Fact[] Combination(){
        return new Fact[2];
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
        if(Facts.get(0) != null){
            Facts.add(Mutation());

        }
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

    public List<Fact> getFacts(){return Facts;}

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
