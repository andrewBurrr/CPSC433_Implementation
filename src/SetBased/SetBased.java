package SetBased;

import OrTree.OTreeModel;
import Structures.*;
import Parser.Reader;
import Structures.Assignment;
import Structures.Course;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.abs;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SetBased{
    private ArrayList<Fact> facts;
    private float threshold;
    private double difTol;
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
    private String fileName;
    private double killPercent;
    private boolean output;
    
    public SetBased(Reader reader, OTreeModel oTree, float[] weights, String fileName, boolean output){
        //Initialize the SetBased environment
        this.fileName = fileName;
        this.threshold = (float) 5;
        this.killPercent = 0.30;
        this.difTol = 0.05;
        this.maxPopulation = 50;
        this.facts = new ArrayList();
        this.reader = reader;
        this.oTree = oTree;
        this.courseLab = new LinkedHashSet(reader.getCourses());
        this.courseLab.addAll(reader.getLabs());
        this.maxInitSols = 15;
        this.variance = Integer.MAX_VALUE;
        this.firMoment = 0;
        this.secMoment = 0;
        this.wMinFill = weights[0];
        this.wPref = weights[1];
        this.wPair = weights[2];
        this.wSecDiff = weights[3];
        this.output = output;
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
        Course mutCourse = mutSchedule.keySet().toArray(new Course[0])[random.nextInt(mutSchedule.size()-1)];
        Slot newSlot;
        // Make sure new slot for mutCourse is of right type and is different
        if(mutCourse instanceof Lecture){ // Lecture
            newSlot = reader.getCourseSlots().toArray(new Slot[0])[random.nextInt(reader.getCourseSlots().size()-1)];
            while(newSlot.equals(mutSchedule.get(mutCourse))){
                newSlot = reader.getCourseSlots().toArray(new Slot[0])[random.nextInt(reader.getCourseSlots().size()-1)];
            }
        } else { // Lab
            newSlot = reader.getLabSlots().toArray(new Slot[0])[random.nextInt(reader.getLabSlots().size()-1)];
            while(newSlot.equals(mutSchedule.get(mutCourse))){
                newSlot = reader.getLabSlots().toArray(new Slot[0])[random.nextInt(reader.getLabSlots().size()-1)];
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
        int numberKilled = (int) (killPercent * facts.size());
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
            if(f!=null){
                firMoment += f.getEvaluation();
                secMoment += f.getEvaluation()*f.getEvaluation();
            }
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
        System.out.println("Status: Set Based - Creating Initial Solution Set");
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName.replace(".","_log."),true))) {
            writer.println("Status: Set Based - Creating Initial Solution Set");
        } catch (IOException ex) {
            Logger.getLogger(SetBased.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(int i = 0; i < maxInitSols;i++){
            Fact fact = (Fact) oTree.depthFirst();
            if(fact != null) {
                fact.setEvaluation(Eval(fact));
                if(!facts.contains(fact)) {
                    facts.add(fact);
                    System.out.println("Status: Set Based - Added Initial Solution");
                    try (PrintWriter writer = new PrintWriter(new FileWriter(fileName.replace(".","_log."),true))) {
                        writer.println("Status: Set Based - Added Initial Solution");
                        writer.flush();
                        writer.close();
                        try (PrintWriter writer2 = new PrintWriter(new FileWriter(fileName.replace(".","_solutionlog."),true))) {
                            writer2.append(fact.toString()+"\n");
                            writer2.flush();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(SetBased.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                return null;
            }
        }
        
        System.out.println("Status: Set Based - Begining evolution");
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName.replace(".","_log."),true))) {
                    writer.println("Status: Set Based - Begining evolution");
                } catch (IOException ex) {
            Logger.getLogger(SetBased.class.getName()).log(Level.SEVERE, null, ex);
        }
        getVariance();
        Random rand = new Random();
        while(variance>threshold || facts.size()<(maxPopulation*(0.9-killPercent))){
            System.out.println(variance);
            System.out.println("Eval:"+facts.get(0).getEvaluation());
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName.replace(".","_output."),true))) {
                    writer.println("Status: Set Based - Begining evolution");
                } catch (IOException ex) {
            Logger.getLogger(SetBased.class.getName()).log(Level.SEVERE, null, ex);
        }
            // If facts is empty we run depthFirst
            //If facts are too big, kill them off with Tod()
            Fact newFacts[] = new Fact[2];
            if (facts.size() > maxPopulation) {
                if(output){
                    System.out.println("Status: Set Based - Killing off the weak");
                }
                Tod();
                getVariance();
//                try (PrintWriter writer = new PrintWriter(new FileWriter(fileName.replace(".","_log."),true))) {
//                    writer.println("Status: Set Based - Killing off the weak");
//                    writer.append("Facts:\n"+facts.toString() + "\n  ");
//                    writer.append("Best Evaluation:" + facts.get(0).getEvaluation()+"\n");
//                    writer.flush();
//                    writer.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(SetBased.class.getName()).log(Level.SEVERE, null, ex);
//                }
                Collections.sort(facts);
                if(abs((variance-lastEval)/variance) < difTol){
                    break;
                }
                lastEval = variance;
            } else {
                if(rand.nextBoolean()) {
                    if(output) {
                        System.out.println("Status: Mutating a pop");
                    }
                    Fact newFact = Mutation();
                    if(newFact != null) {
                        newFact.setEvaluation(Eval(newFact));
                        facts.add(newFact);
                        Collections.sort(facts);
                        newFacts[0] = newFact;
                        Collections.sort(facts);
                    }
                } else {
                    if(output) {
                        System.out.println("Status: Combining two pops");
                    }
                    int i =0;
                    for(Fact f: Combination()){
                        if(f != null){
                            f.setEvaluation(Eval(f));
                            facts.add(f);
                            Collections.sort(facts);
                            newFacts[i] = f;
                            i++;
                        }
                    }
                    Collections.sort(facts);
                }
                getVariance(newFacts);
            }

        }
        return facts.remove(0);
    }
    
    public List<Fact> getFacts(){return facts;}
}
