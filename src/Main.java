import Utilities.FileManager;

public class Main {

    private static final String CONFIG_FILE_NAME = "src/config.txt";

    public static void main(String[] args) {
        //Create file manager
        FileManager fileManager = new FileManager();

        //Load the config settings
        fileManager.LoadConfig(CONFIG_FILE_NAME);

        //Write output
        fileManager.WriteOutput();
    }
}
