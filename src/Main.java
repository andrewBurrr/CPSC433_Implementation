import OrTree.OTreeModel;
import OrTree.Prob;
import Parser.Reader;
import SetBased.setBased;
import SetBased.Fact;
import Structures.Assignment;
import Structures.Course;
import Structures.Lecture;
import Structures.Slot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        File folder = new File("src/InputFiles/");
        File[] listOfTest = folder.listFiles();
        
        File error = new File("errors.txt");
        error.delete();
        
        for(File test: listOfTest){
            if(test.isFile() && !test.toString().contains("deptinst") && !test.toString().contains("/.") && !test.toString().contains("output")){
                String inputFile = test.toString();
                try {
                    Reader reader = new Reader(inputFile, false);
                    OTreeModel otree = new OTreeModel(reader);
                    Prob f = otree.depthFirst();
                    if (f == null){
                        System.out.printf("%s: UNSOLVED\n\n",reader.getName());
                        
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
                        writer.write("Status: UNSOLVED\n");
                       writer.close();
                    } else{
                        System.out.printf("%s: SOLVED\n%s\n",reader.getName(),f.toString());
                        
                        String outputFile = String.format("%s_output.txt", inputFile.replace(".txt", ""));
                        PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
                        writer.write("Status: SOLVED\n"+f.toString());
                        writer.close();
                    }
                } catch (Exception e){
                    try{
 //                       System.out.println("************Error***********");
                        FileWriter fileWriter = new FileWriter("errors.txt");
                        PrintWriter printWriter = new PrintWriter(fileWriter);
                        e.printStackTrace();
                        printWriter.append(e.toString());
                        printWriter.flush();
                        printWriter.append(e.getMessage());
                        printWriter.flush();
                     //   printWriter.append(Arrays.toString(e.getStackTrace()));
                        printWriter.flush();
                        printWriter.close();
                        Thread.sleep(10);
                    } catch (IOException ew){

                    } catch (InterruptedException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } 
        }
        
//        String inputFile = "src/InputFiles/shortExample.txt";
//        Reader reader = new Reader(inputFile);
//        OTreeModel otree = new OTreeModel(reader);
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        Prob f = otree.depthFirst();
//        System.out.println(f.toString());
//        System.out.printf("Unwantd:%s\n",reader.getUnwanted().toString().replace("[","\n").replace("]","").replace(", ",""));
//        System.out.printf("Not-Compatible:%s\n",reader.getNotCompatible().toString().replace("[","\n").replace("]","").replace(", ",""));
//        System.out.printf("PartAssing:%s\n",reader.getPartialAssignments().toString().replace("{","\n").replace("}","").replace("=","\n\t=").replace(", ","\n"));
//        
//        Iterator<Map.Entry<Course, Slot>> itor = f.getScheduel().entrySet().iterator();
//        LinkedList<Assignment> guide = new LinkedList();
//        while(itor.hasNext()){
//            Map.Entry<Course, Slot> entry = itor.next();
//            Assignment assign = new Assignment(entry.getKey(), entry.getValue());
//            guide.add(assign);
//            System.out.println(assign.toString());
//        }
//        
//        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        Prob p = otree.guided(guide);
//        System.out.println(p.toString());
//        System.out.printf("Unwantd:%s\n",reader.getUnwanted().toString().replace("[","\n").replace("]","").replace(", ",""));
//        System.out.printf("Not-Compatible:%s\n",reader.getNotCompatible().toString().replace("[","\n").replace("]","").replace(", ",""));
//        System.out.printf("PartAssing:%s\n",reader.getPartialAssignments().toString().replace("{","\n").replace("}","").replace("=","\n\t=").replace(", ","\n"));
    }
}
/**
 *
 * Or tree:
 *
 */
