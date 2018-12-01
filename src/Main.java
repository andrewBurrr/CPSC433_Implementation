import Exceptions.InvalidSchedulingException;
import OrTree.OTreeModel;
import Parser.Reader;
import SetBased.Fact;
import SetBased.SetBased;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        File folder = new File("src/InputFiles/");
        File[] listOfTest = folder.listFiles();
        
        File error = new File("errors.txt");
        error.delete();
        
        for(File test: listOfTest){
            if(/*test.toString().contains("short") &&*/test.isFile() && !test.toString().contains("deptinst") && !test.toString().contains("/.") && !test.toString().contains("output")){
                String inputFile = test.toString();
                try {
                    Reader reader = new Reader(inputFile, false);
                    OTreeModel otree;
                    try {
                        otree = new OTreeModel(reader);
                    } catch(InvalidSchedulingException err){
                        System.out.printf("%s: UNSOLVED\n%s\n\n",reader.getName(),err.getMessage());
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                            writer.write("Status: UNSOLVED\n"+err.getMessage());
                        }
                        continue;
                    }
                    SetBased setBased = new SetBased(reader, otree);
                    Fact f = setBased.run();
                    
                    if (f == null){
                        System.out.printf("%s: UNSOLVED\n\n",reader.getName());
                        
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                            writer.write("Status: UNSOLVED\n");
                        }
                    } else{
                        System.out.printf("%s: SOLVED\n%s\n",reader.getName(),f.toString());
                        
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
