import Parser.Reader;

public class Main {

    public static void main(String[] args) {
        // steps:
        // see config file (not started)
        // read the input file (working untested)
        // build the map of partial assignments
        // initialize or tree with the root populated with partial assignments
        // use OR tree  to run n times, to initialize set search
        // call set search:
        // initialize inner or trees
        // see paper for explanation on how set based works
        String inputFile = "src/InputFiles/deptinst1.txt";
        Reader reader = new Reader(inputFile);
        inputFile = "src/InputFiles/deptinst2.txt";
        reader = new Reader(inputFile);
    }
}
/**
 *
 * Or tree:
 *
 */
