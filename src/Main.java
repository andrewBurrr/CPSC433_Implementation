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
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        boolean useConfig = false;
        if(args.length == 1){
            useConfig = args[0].equals("y");
        }
        
        // Weights to be changed according to configReader file or
        float wMin = 1;
        float wPref = 1;
        float wPair = 1;
        float wSecD = 1;
        float p_CMin = 1;
        float p_LMin = 1;
        float p_Pair = 1;
        List<String> listOfInput = new LinkedList<>();
        boolean richout = true;
        
        // Config
        File config = new File("config.txt");
        if(config.exists() && config.length()>0 && useConfig){ // If config is present and not empty 
            System.out.println("Status: Reading Config");
            try (Scanner configReader = new Scanner(config).useDelimiter("\\n")) {
                while(configReader.hasNext()){
                    listOfInput.add("InputFiles/"+configReader.nextLine());
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try { 
                File folder = new File("InputFiles/Tests");
                File[] listOfFiles = folder.listFiles();
                for(File test: listOfFiles){
                    if(/*test.toString().contains("6") &&*/ test.isFile()
                            && !test.toString().contains("/.") 
                            && !test.toString().contains("output")
                            && !test.toString().contains("log")){
                        listOfInput.add(test.toString() + " 1 1 1 1 1 1");
                    }
                }
            } catch (Exception e) {
                System.out.println(e.toString());
                System.exit(0);
            }
        }

        File error = new File("errors.txt");
        error.delete();
        
        // Iterate over inputs, maybe combine this with the weights with non enhanced loop
        for(String line: listOfInput){
            if(!line.equals("")){
                String [] stringSplit = line.split(" ");
                String inputFile = stringSplit[0];
                wMin = Float.parseFloat(stringSplit[1]);
                wPref = Float.parseFloat(stringSplit[2]);
                wPair = Float.parseFloat(stringSplit[3]);
                wSecD = Float.parseFloat(stringSplit[4]);
                p_CMin = Float.parseFloat(stringSplit[5]);
                p_LMin = Float.parseFloat(stringSplit[6]);
                float[] weights = (new float[]{wMin,wPref,wPair,wSecD,p_CMin,p_LMin});
                
                solveProb(inputFile, weights, richout);
            }
        }
        
        
    }
    
    public static void solveProb(String inputFile, float[] weights, boolean richout){
        String log = inputFile.replace(".", "_log.");
        String output = inputFile.replace(".", "_output.");
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
        System.out.printf("Status: Reading File - %s\n",inputFile);
        System.out.printf("Weights:\n"+"\tminFilled:%2.2f\n" + "\tpref:%2.2f\n" 
                + "\tpair:%2.2f\n" + "\tsecdiff:%2.2f\n"  + "\tpen_CourseMin:%2.2f\n"
                + "\tpen_LabMin:%2.2f\n",weights[0],weights[1],weights[2],weights[3],weights[4],weights[5]);
        
        try {
            try (PrintWriter outWriter = new PrintWriter(new FileWriter(log))) {
                outWriter.printf("Status: Reading File - %s\n",inputFile);
                outWriter.printf("Weights:\n"+"\tminFilled:%2.2f\n" + "\tpref:%2.2f\n"
                        + "\tpair:%2.2f\n" + "\tsecdiff:%2.2f\n"  + "\tpen_CourseMin:%2.2f\n"
                        + "\tpen_LabMin:%2.2f\n",weights[0],weights[1],weights[2],weights[3],weights[4],weights[5]);
                outWriter.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            Reader reader = new Reader(inputFile, false);
            System.out.printf("Number of Course Slots: %d\n",reader.getCourseSlots().size());
            System.out.printf("Number of Lab Slots: %d\n",reader.getLabSlots().size());
            System.out.printf("Number of Courses: %d\n",reader.getCourses().size());
            System.out.printf("Number of Lab: %d\n",reader.getLabs().size());
            try (PrintWriter outWriter = new PrintWriter(new FileWriter(log),true)) {
                outWriter.printf("Number of Course Slots: %d\n",reader.getCourseSlots().size());
                outWriter.printf("Number of Lab Slots: %d\n",reader.getLabSlots().size());
                outWriter.printf("Number of Courses: %d\n",reader.getCourses().size());
                outWriter.printf("Number of Lab: %d\n",reader.getLabs().size());
                outWriter.flush();
                outWriter.close();
            }
            File file = new File(inputFile);
            OTreeModel otree;

            try { // Try intializing OTree
                System.out.println("Status: Initiating Or Tree Model");
                try (PrintWriter outWriter = new PrintWriter(new FileWriter(log,true))) {
                    outWriter.println("Status: Initiating Or Tree Model");
                }
                otree = new OTreeModel(reader, inputFile);
            } catch(InvalidSchedulingException err){ // Catch Error in initialization and continue
                System.out.printf("Name: %s\nStatus: UNSOLVED\n%s\n\n",reader.getName(),err.getMessage());
                try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
                    writer.write("Status: UNSOLVED\n"+err.getMessage());
                }
                return; // If Otree was not initialized then move to next input
            }

            System.out.println("Status: Initiating Set Based Model");
            try (PrintWriter outWriter = new PrintWriter(new FileWriter(log,true))) {
                outWriter.println("Status: Initiating Set Based Model");
            }
            SetBased setBased = new SetBased(reader, otree, weights, inputFile, richout);
            System.out.println("Status: Begining Set Based Search");
            try (PrintWriter outWriter = new PrintWriter(new FileWriter(log,true))) {
                outWriter.println("Status: Begining Set Based Search");
            }
            Fact f = setBased.run();

            // If f is null then there no solution was found
            if (f == null){
                System.out.printf("\nName: %s\nStatus: UNSOLVED\nReason: Infeasible Problem\n\n",reader.getName());
                try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
                    writer.write("Status: UNSOLVED\nReason: Infeasible Problem\n\n");
                }
            } else{ // Solution found, print it out and write it to file
                System.out.printf("\nName: %s\nStatus: SOLVED\nSolution:\n%s\n",reader.getName(),f.toString());
                try (PrintWriter writer = new PrintWriter(new FileWriter(output))) {
                    writer.write("Status: SOLVED\n"+f.toString());
                }
            }
            
        } catch (Exception e){ // If any other error occurs catch it and write to file
            try{
                System.out.printf("Error in:%s\nOutput saved to %s_log.txt",inputFile, inputFile);
                FileWriter fileWriter = new FileWriter(log,true);
                try (PrintWriter printWriter = new PrintWriter(fileWriter)) {
                    printWriter.append("Error in: "+inputFile+"\n");
                    printWriter.append("Message: " +  e.toString()+"\n");
                    printWriter.append("\nStack Trace:\n");
                    printWriter.append(Arrays.toString(e.getStackTrace()).replace(", ", "\n").replace("[","").replace("] ","\n\n"));
                    printWriter.flush();
                    printWriter.close();
                } 
                Thread.sleep(10); // So print isnt polluted
            } catch (IOException ew){

            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
/**
 *
 * Or tree:
 *
 */
