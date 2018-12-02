package SetBased;

import OrTree.OTreeModel;
import Structures.*;
import Parser.Reader;
import Structures.Assignment;
import Structures.Course;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SetBased{
    private ArrayList<Fact> facts;
    private float threshold;
    private float difTol;
    private int maxPopulation;
    private Reader reader;
    private OTreeModel oTree;
    private Set<Course> courseLab;
    private int maxInitSols;
    private int variance;
    private float firMoment;
    private float secMoment;
    private final float wMinFill;
    private final float wPref;
    private final float wPair;
    private final float wSecDiff;
    private final float pen_CourseMin;
    private final float pen_LabMin;
    private static final Logger LOGGER = Logger.getLogger( SetBased.class.getName() );;

    public SetBased(Reader reader, OTreeModel oTree, float[] weights){
        //Initialize the SetBased environment\
        try {
            Handler handle = new FileHandler("SetBased.log");
            LOGGER.addHandler(handle);
            LOGGER.setLevel(Level.FINEST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.threshold = 1;
         this.difTol = 1;
         this.maxPopulation = 10;
         this.facts = new ArrayList();
         this.reader = reader;
         this.oTree = oTree;
         this.courseLab = new LinkedHashSet(reader.getCourses());
         this.courseLab.addAll(reader.getLabs());
         this.maxInitSols = 50;
         this.variance = Integer.MAX_VALUE;
         this.firMoment = 0;
         this.secMoment = 0;
         this.wMinFill = weights[0];
         this.wPref = weights[1];
         this.wPair = weights[2];
         this.wSecDiff = weights[3];
         if(weights.length==6){
             this.pen_CourseMin = weights[4];
             this.pen_LabMin = weights[5];
         } else {
             this.pen_CourseMin = 1;
             this.pen_LabMin = 1;
         }
    }
    
    //TODO: Implement Mutation according to setBasedBreakDown, return a Fact newFact
    private Fact Mutation(){
        Random random = new Random();
        
        //Create a Deep Copy of a random Fact, then getSChedule()
        Fact mutFact = new Fact(facts.get(random.nextInt(facts.size()-1))); 
        Map<Course, Slot> mutSchedule = mutFact.getScheduel();
        
        // Get a random lecture (lab or lecture) from scheduel
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
        // Remove lecture from lectures
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
        
        HashMap<Course, Slot> p1Map = new HashMap(facts.get(rand.nextInt(facts.size()-1)).getScheduel());
        HashMap<Course, Slot> p2Map = new HashMap(facts.get(rand.nextInt(facts.size()-1)).getScheduel());
        while(p2Map.equals(p1Map)){
            p2Map = new HashMap(facts.get(rand.nextInt(facts.size()-1)).getScheduel());
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
        int killPercent = 30;
        int numberKilled = (killPercent * facts.size()) / 100;
        facts.subList(facts.size()-numberKilled, facts.size()-1).clear();

    }

    //TODO: Implement Eval to calculate the soft constraint, take in a Fact, return an int
    private float Eval(Fact sol){
        return wMinFill*EvalMinFill(sol) + wPref*EvalPref(sol) + wPair*EvalPair(sol) + wSecDiff*EvalSecDiff(sol);
    }
    
    private float EvalMinFill(Fact sol){
        float pen = 0;
        
        Iterator<Map.Entry<Slot, Integer>> itor = sol.getNumCourses().entrySet().iterator();
        while(itor.hasNext()) {
            Map.Entry<Slot, Integer> entry = itor.next();
            if(entry.getKey().getMin() > entry.getValue()){
                pen += pen_CourseMin;
            }
        }
        itor = sol.getNumLabs().entrySet().iterator();
        while(itor.hasNext()) {
            Map.Entry<Slot, Integer> entry = itor.next();
            if(entry.getKey().getMin() > entry.getValue()){
                pen += pen_LabMin;
            }
        }
        
        return pen;
    }
    
    private float EvalPref(Fact sol) {
        float pen = 0;
        HashMap<Course, Slot> schedule = sol.getScheduel();
        Iterator<Preference> itor = reader.getPreferences().iterator();
        
        while(itor.hasNext()) {
            Preference pref = itor.next();
            if(!pref.getSlot().equals(schedule.get(pref.getCourse()))) {
                pen += pref.getValue();
            }
        }
        
        return pen;
    }

    private float EvalPair(Fact sol) {
        float pen = 0;
        HashMap<Course, Slot> schedule = sol.getScheduel();
        Iterator<Pair> itor = reader.getPairs().iterator();
        
        while(itor.hasNext()) {
            Pair pair = itor.next();
            if(!schedule.get(pair.getCourse(0)).equals(schedule.get(pair.getCourse(1)))) {
                pen += 1;
            }
        }
        
        return pen;
    }

    private float EvalSecDiff(Fact sol) {
        float pen = 0;
        
        ArrayList<Course> lectures = new ArrayList(reader.getCourses());
        HashMap<Course, Slot> schedule = sol.getScheduel();
        lectures.sort(new CourseSorterAlpaNum());
        
        Course lecture;
        while(lectures.size()>1){
            lecture = lectures.remove(0);
            int i = 0;
            while(i<lectures.size() && lectures.get(i).getName().equals(lecture.getName()) &&
                    lectures.get(i).getNumber().equals(lecture.getNumber() )){
                if(schedule.get(lecture).equals(schedule.get(lectures.get(i)))) {
                    pen += 1;
                }
                i++;
            }
        }
        
        return pen;
    }

    //TODO: Calculate the Variance of the current facts, return a float
    private int getVariance(Fact[] newFacts){
        // Calculate first moment (mu, mean)
        firMoment = firMoment*(facts.size()-newFacts.length);
        secMoment = secMoment*(facts.size()-newFacts.length);
        for(Fact f:newFacts){
            firMoment += f.getEvaluation();
            secMoment += f.getEvaluation()*f.getEvaluation();
        }
        firMoment = firMoment/facts.size();
        secMoment = secMoment/facts.size();
        
        variance = (int) (secMoment - firMoment*firMoment);
        
        return variance;
    }
    
    private int getVariance(){
        firMoment = 0;
        secMoment = 0;
        for(Fact f:facts){
            firMoment += f.getEvaluation();
            secMoment += f.getEvaluation()*f.getEvaluation();
        }
        firMoment = firMoment/facts.size();
        secMoment = secMoment/facts.size();
        
        variance = (int) (secMoment - firMoment*firMoment);
        
        return variance;
    }

    //This is the main function in SetBased
    public Fact run()  {
        float lastEval = 0;
        System.out.println("Status: Creating Initial Solution Set");
        for(int i = 0; i < maxInitSols;i++){
            Fact fact = (Fact) oTree.depthFirst();
            if(fact != null) {
                fact.setEvaluation(Eval(fact));
                if(!facts.contains(fact)) {
                    facts.add(fact);
                }
            }
        }
        if(facts.isEmpty()) {
            return null;
        }
        
        System.out.println("Status: Begining evolution");
        getVariance();
        Random rand = new Random();
        
        while(variance>threshold){
            // If facts is empty we run depthFirst
            //If facts are too big, kill them off with Tod()
            Fact newFacts[] = new Fact[2];
            if (facts.size() > maxPopulation) {
                System.out.println("Status: Killing off the weak");
                Tod();
                getVariance();
                if(variance-lastEval < difTol){
                    break;
                }
                lastEval = variance;
            } else {
                if(rand.nextBoolean()) {
                    System.out.println("Mutating");
                    Fact newFact = Mutation();
                    if(newFact != null) {
                        facts.add(newFact);
                        Collections.sort(facts);
                        if(facts.get(0).equals(newFact)){
                            LOGGER.log(Level.FINE,
                                    "New Best Solution created from Mutation: {0}", newFact.getEvaluation());
                        }
                        newFacts[0] = newFact;

                    }
                } else {
                    System.out.println("Combination");
                    int i =0;
                    for(Fact f: Combination()){
                        if(f != null){
                            facts.add(f);
                            newFacts[i] = f;
                            if(facts.get(0).equals(f)){
                                LOGGER.log(Level.FINE,
                                        "New Best Solution created from Mutation: {0}", f.getEvaluation());
                            }

                            i++;
                        }
                    }
                }
                getVariance(newFacts);
            }

        }
        return facts.remove(0);
        
//        if(facts.get(0) != null){
//            facts.add(Mutation());
//            Collections.sort(facts);
//        }
//            while (true) {
//                //If facts is empty we run depthFirst
//                //If facts are too big, kill them off with Tod()
//                if (facts.size() > maxPopulation) {
//                    Tod();
//                } else {
//                    //Randomly choose between Mutation and Combination in search control
//                    if (new Random().nextInt(1) == 1) {
//                        //Mutation return a newFact
//                        Fact newFact = Mutation(reader);
//                        //Calculate the soft constraint of the newFact
//                        newFact.setEvaluation(Eval(newFact));
//                        //Add to our current set of solution
//                        //TODO: Only add to facts if newFact.evaluation < currentBestSolution/some other values
//                        facts.add(newFact);
//                    } else {
//                        //Combination return an array of newFact
//                        Fact[] newFact = Combination();
//                        //A loop to calculate the soft constraint for newFact[i]
//                        for (int i = 0; i < newFact.length; i++) {
//                            newFact[i].setEvaluation(Eval(newFact[i]));
//                            //TODO: Only add to facts if newFact.evaluation < currentBestSolution/some other values
//                            facts.add(newFact[i]);
//                        }
//                    }
//                }
//                //Calculate the variance of our current facts, if variance < threshold quit the SetBased
//                float currentVariance = getVariance(facts);
//                if (currentVariance <= threshold) {
//                    System.out.println("The best solutions are found, terminating the SetBased");
//                    break;
//                }
//            }
//        }
    }
    
    public List<Fact> getFacts(){return facts;}
}
