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
import java.util.LinkedList;
import java.util.List;
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


        List<String> listOfInput = new LinkedList<>();
        float[][] weightsArr;
        // Config
//        try {
//            Scanner fileReader = new Scanner(new File("config.txt"));
//            int numInputs = Integer.parseInt(fileReader.next());
//            listOfInput = new File[numInputs];
//            weights = new float[numInputs][6];
//            int i =0;
//            while(fileReader.hasNext()){
//                String[] line = fileReader.next().split(":");
//                listOfInput[i] = new File(line[0]);
//                weights[i] = new float[6];
//            }
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);.
        try {
            Scanner config = new Scanner(new File("config.txt")).useDelimiter("\\n");
            while(config.hasNext()){
                listOfInput.add("src/InputFiles/"+config.nextLine());
            }
            config.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        }
        File error = new File("errors.txt");
        error.delete();

        // Iterate over inputs, maybe combine this with the weights with non enhanced loop
        for(String line: listOfInput){
//            if(/*test.toString().contains("6") &&*/ test.isFile() && !test.toString().contains("deptinst") && !test.toString().contains("/.") && !test.toString().contains("output")){
            if(!line.equals("")){
                String [] stringSplit = line.split(" ");
                String inputFile = stringSplit[0];
                wMin = Float.parseFloat(stringSplit[1]);
                wPref = Float.parseFloat(stringSplit[2]);
                wPair = Float.parseFloat(stringSplit[3]);
                wSecD = Float.parseFloat(stringSplit[4]);
                p_CMin = Float.parseFloat(stringSplit[5]);
                p_LMin = Float.parseFloat(stringSplit[6]);
                System.out.println(wMin);
                System.out.println(wPref);
                System.out.println(wPair);
                System.out.println(wSecD);
                System.out.println(p_CMin);
                System.out.println(p_LMin);
                try {
                    System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                    System.out.printf("Status: Reading File - %s\n",inputFile);
                    Reader reader = new Reader(inputFile, false);
                    System.out.printf("Course Slots:%d\n",reader.getCourseSlots().size());
                    System.out.printf("Lab Slots:%d\n",reader.getLabSlots().size());
                    System.out.printf("Courses:%d\n",reader.getCourses().size());
                    System.out.printf("Labs:%d\n",reader.getLabs().size());
                   // System.exit(0);
                    File file = new File(inputFile);
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
