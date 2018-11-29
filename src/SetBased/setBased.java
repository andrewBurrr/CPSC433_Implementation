package SetBased;

import java.util.Random;
import java.util.Set;


public class setBased {
    private Set<Fact> Facts;

    //TODO: Implement Mutation according to setBasedBreakDown, return a Fact newFact
    private Fact Mutation(){
        return new Fact();
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
