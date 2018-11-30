import OrTree.OTreeModel;
import OrTree.Prob;
import Parser.Reader;
import SetBased.Fact;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        
        String inputFile = "src/InputFiles/shortExample.txt";
        Reader reader = new Reader(inputFile);
        OTreeModel otree = new OTreeModel(reader);
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Prob f = otree.depthFirst();
        System.out.println("\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println(f.toString());
        System.out.printf("Unwantd:%s\n",reader.getUnwanted().toString().replace("[","\n").replace("]","").replace(", ",""));
        System.out.printf("Not-Compatible:%s\n",reader.getNotCompatible().toString().replace("[","\n").replace("]","").replace(", ",""));
        System.out.printf("PartAssing:%s\n",reader.getPartialAssignments().toString().replace("{","\n").replace("}","").replace("=","\n\t=").replace(", ","\n"));
        
    }
}
/**
 *
 * Or tree:
 *
 */
