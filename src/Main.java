import OrTree.OTreeModel;
import OrTree.Prob;
import Parser.Reader;
import SetBased.Fact;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        
        String inputFile = "src/InputFiles/deptinst1.txt";
        Reader reader = new Reader(inputFile);
        OTreeModel otree = new OTreeModel(reader);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Prob f = otree.depthFirst();
    }
}
/**
 *
 * Or tree:
 *
 */
