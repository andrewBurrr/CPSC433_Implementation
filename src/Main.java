import Exceptions.InvalidSchedulingException;
import OrTree.OTreeModel;
import Parser.Reader;
import SetBased.Fact;
import SetBased.SetBased;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        // Weights to be changed according to config file or 
        float wMin = 1;
        float wPref = 1;
        float wPair = 1;
        float wSecD = 1;
        float p_CMin = 1;
        float p_LMin = 1;
        
        
        File[] listOfInput;
        // Config 
        if(false){
            try {
                Scanner fileReader = new Scanner(new File("config.txt"));
                listOfInput = null;// Read config 
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else { // All files in InputFiles folder
            File folder = new File("src/InputFiles/");
            listOfInput= folder.listFiles();
        }
        
        File error = new File("errors.txt");
        error.delete();
        
        // Iterate over inputs, maybe combine this with the weights with non enhanced loop
        for(File test: listOfInput){
            if(/*test.toString().contains("6") &&*/ test.isFile() && !test.toString().contains("deptinst") && !test.toString().contains("/.") && !test.toString().contains("output")){
                String inputFile = test.toString();
                try {
                    System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                    System.out.printf("Status: Reading File - %s\n",inputFile);
                    Reader reader = new Reader(inputFile, false);
                    OTreeModel otree;
                    
                    try { // Try intializing OTree
                        System.out.println("Status: Initiating Or Tree Model");
                        otree = new OTreeModel(reader);
                    } catch(InvalidSchedulingException err){ // Catch Error in initialization and continue
                        System.out.printf("Name: %s\nStatus: UNSOLVED\n%s\n\n",reader.getName(),err.getMessage());
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                            writer.write("Status: UNSOLVED\n"+err.getMessage());
                        }
                        continue;
                    }
                    System.out.println("Status: Initiating Set Based Model");
                    System.out.println("Status: Reading Config");
                    // Set weights for setbased
                    float[] weights = (new float[]{wMin,wPref,wPair,wSecD,p_CMin,p_LMin});
                    SetBased setBased = new SetBased(reader, otree, weights);
                    System.out.println("Status: Begining Set Based Search");
                    Fact f = setBased.run();
                    
                    if (f == null){
                        System.out.printf("\nName: %s\nStatus: UNSOLVED\nError: Infeasible Problem\n\n",reader.getName());
                        
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                            writer.write("Status: UNSOLVED\n");
                        }
                    } else{
                        System.out.printf("\nName: %s\nStatus: SOLVED\nSolution:\n%s\n",reader.getName(),f.toString());
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                            writer.write("Status: SOLVED\n"+f.toString());
                        }
                    }
                } catch (Exception e){
                    try{
                        FileWriter fileWriter = new FileWriter("errors.txt");
                        try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
                            e.printStackTrace();
                            printWriter.append(e.toString());
                            printWriter.flush();
                            printWriter.append(e.getMessage());
                            printWriter.flush();
                            //   printWriter.append(Arrays.toString(e.getStackTrace()));
                            printWriter.flush();
                        }
                        Thread.sleep(10);
                    } catch (IOException ew){

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } 
        }
    }
}
/**
 *
 * Or tree:
 *
 */
