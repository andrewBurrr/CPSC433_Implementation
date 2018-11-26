package Objects;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import Structures.Course;
import Structures.Lecture;
import Structures.Slot;
import Parser.Reader;
;

public class setBased {
    private Set<Fact> Facts;
    private Reader reader;

    //TODO: Implement Mutation according to setBasedBreakDown, return a Fact newFact
    private Fact Mutation(){
        Random r = new Random();
        Fact a_solution = null;
        Fact a_mutation = null;
        Set<Slot> course_list = reader.getCourseSlots();

        // to get a random schedule (fact) from set of (schedules) facts.
        int a_schedule = r.nextInt(Facts.size());
        int i = 0;
        for (Object f : Facts) {
            if (i == a_schedule){
                a_solution = (Fact) f;
                a_mutation = a_solution;
                break;
            }
            i++;
        }

        // get courses in schedule
        Object [] courses_in_schedule = a_solution.return_schedule().keySet().toArray();
        // a random course to be replaced
        Object course = r.nextInt(courses_in_schedule.length);

        // get replacement course from course_list;
        Object[] replacements = course_list.toArray();

        // actual mutation
        while(true) {
            Object replacement = r.nextInt(replacements.length);
            if (!replacement.equals(course) && !a_mutation.return_schedule().containsKey(replacement)) {
                a_mutation.return_schedule().remove(course, a_mutation.return_schedule().get(course));
                a_mutation.return_schedule().put((Course) replacement, a_solution.return_schedule().get(course));
                break;
            }
        }

        return a_mutation;
//        return new Fact();
    }


    //TODO: Implement Combination according to setBasedBreakDown, return two random new Fact
    private Fact[] Combination(){
        /*
        //creating solutionA1 != solutionA2
        solutionA1 = Fact[random(len(Fact))]
        solutionA2 = Fact[random(len(Fact))]
        while solution1 == solution2:
        solution2 = Fact[random(len(Fact))]
        //Generate guides
        solutionG1 = solutionA1[0:len(solutionA1)/2]+solutionA2[len(solutionA1)/2:]
        solutionG2 = reverse(solutionA2[0:len(solutionA1)/2]+solutionA1[len(solutionA1)/2:])

        */
        Random r = new Random();
        Fact solution_1 = null;
        Fact solution_2 = null;
        Fact solution_3 = null;
        Fact solution_4 = null;

        int facts_index = r.nextInt(Facts.size());
        int i = 0;
        for (Object f : Facts) {
            if (i == facts_index){
                solution_1 = (Fact) f;
                break;
            }
            i++;
        }

        int facts_index_2 = r.nextInt(Facts.size());
        int j = 0;
        for (Object f : Facts) {
            if (i == facts_index_2) {
                solution_2 = (Fact) f;
                if (!solution_1.equals(solution_2)) {
                    break;
                }
            }
            j++;
        }

        // cant slice a hash map lol

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

    //This is the main function in setBased
    public setBased(){
        //Initialize the setBased environment
        int threshold = 0;
        int maxPopulation = 0;
        while(true){
            //If Facts is empty we run depthFirst
            if (Facts.isEmpty()){
                //Run OTree.depthFirst()
            }else{
                //If Facts are too big, kill them off with Tod()
                if (Facts.size() > maxPopulation){
                    Tod();
                }
                else{
                    //Randomly choose between Mutation and Combination in search control
                    if(new Random().nextInt(1) == 1){
                        //Mutation return a newFact
                        Fact newFact = Mutation();
                        //Calculate the soft constraint of the newFact
                        newFact.setEvaluation(Eval(newFact));
                        //Add to our current set of solution
                        //TODO: Only add to Facts if newFact.evaluation < currentBestSolution/some other values
                        Facts.add(newFact);
                    }else{
                        //Combination return an array of newFact
                        Fact[] newFact = Combination();
                        //A loop to calculate the soft constraint for newFact[i]
                        for (int i = 0; i < newFact.length; i++){
                            newFact[i].setEvaluation(Eval(newFact[i]));
                            //TODO: Only add to Facts if newFact.evaluation < currentBestSolution/some other values
                            Facts.add(newFact[i]);
                        }
                    }
                }
            }
            //Calculate the variance of our current Facts, if variance < threshold quit the setBased
            float currentVariance = getVariance(Facts);
            if (currentVariance <= threshold) {
                System.out.println("The best solutions are found, terminating the setBased");
                break;
            }
        }
    }

    @Override
    //TODO: Implement toString to properly display the result after we finish searching, return a String of all
    //TODO: the current set of solution in Facts as well as the evaluation in correct format
    public String toString(){
        return "Facts" + "Evaluation";
    }

}
