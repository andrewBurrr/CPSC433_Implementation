package Utilities;

import java.io.*;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager {
    private BufferedReader configReader = null;
    private BufferedReader inputReader = null;
    private BufferedWriter writer = null;

    private final Map<String, String> HEADERS = new HashMap<String, String>();

    private final String SLOT_REGEX = "[A-Z]{2}[\\s]*,[\\s]*\\d{1,2}:\\d{2}[\\s]*,[\\s]*\\d[\\s]*,[\\s]*\\d";
    private final String COURSE_REGEX = "[A-Z]{4}[\\s]*\\d{3}[\\s]*LEC[\\s]*\\d{2}";
    private final String LAB_REGEX = "[A-Z]{4}[\\s]*\\d{3}[\\s]*[LEC[\\s]*\\d{2}[\\s]*]*(TUT|LAB)[\\s]*\\d{2}";
    private final String NOT_COMPATIBLE_REGEX = (COURSE_REGEX + "[\\s]*,[\\s]*" + COURSE_REGEX) + "|" +
                                                (COURSE_REGEX + "[\\s]*,[\\s]*" + LAB_REGEX) + "|" +
                                                (LAB_REGEX + "[\\s]*,[\\s]*" + COURSE_REGEX) + "|" +
                                                (LAB_REGEX + "[\\s]*,[\\s]*" + LAB_REGEX) ;
    private final String UNWANTED_REGEX = "[A-Z]{4}[\\s]*\\d{3}[\\s]*(LEC|LAB|TUT)[\\s]*\\d{2}[\\s]*,[\\s]*[A-Z]{2}[\\s]*,[\\s]*\\d{1,2}:\\d{2}";
    private final String PREFERENCE_REGEX = "[A-Z]{2}[\\s]*,[\\s]*\\d{1,2}[\\s]*:\\d{2}[\\s]*,[\\s]*" + (COURSE_REGEX + "|" + LAB_REGEX) + "[\\s]*,[\\s]*\\d*";
    private final String PAIR_REGEX =   (COURSE_REGEX + "[\\s]*,[\\s]*" + COURSE_REGEX) + "|" +
                                        (COURSE_REGEX + "[\\s]*,[\\s]*" + LAB_REGEX) + "|" +
                                        (LAB_REGEX + "[\\s]*,[\\s]*" + COURSE_REGEX) + "|" +
                                        (LAB_REGEX + "[\\s]*,[\\s]*" + LAB_REGEX) ;
    private final String PARTIAL_ASSIGNMENT_REGEX = (COURSE_REGEX + "|" + LAB_REGEX) + "[\\s]*,[\\s]*[A-Z]{2}[\\s]*,[\\s]*\\d{1,2}:\\d{2}";

    public FileManager(){
        InitializeHeaders();
    }

    private void InitializeHeaders(){
        //HEADERS.put("Name:", "Name");
        HEADERS.put("Course slots:", SLOT_REGEX + "$");
        HEADERS.put("Lab slots:", SLOT_REGEX + "$");
        HEADERS.put("Courses:", COURSE_REGEX + "$");
        HEADERS.put("Labs:", LAB_REGEX + "$");
        HEADERS.put("Not compatible:", NOT_COMPATIBLE_REGEX + "$");
        HEADERS.put("Unwanted:", UNWANTED_REGEX + "$");
        HEADERS.put("Preferences:", PREFERENCE_REGEX + "$");
        HEADERS.put("Pair:", PAIR_REGEX + "$");
        HEADERS.put("Partial assignments:", PARTIAL_ASSIGNMENT_REGEX + "$");
    }

    public void LoadConfig(String _configFileName){
        try{
            //Create file to read
            File configFile = new File(_configFileName);
            //Initialize buffered reader
            configReader = new BufferedReader(new FileReader(configFile));
            //Read lines
            String line;
            while((line = configReader.readLine()) != null){
                LoadInput("src/InputFiles/" + line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(configReader != null){
                    configReader.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void LoadInput(String _inputFileName){
        System.out.println("Loading File: " + _inputFileName);
        try{
            //Create file to read
            File inputFile = new File(_inputFileName);
            //Create buffered reader
            inputReader = new BufferedReader(new FileReader(inputFile));
            //Line
            String line;
            String regex = "";
            String header = "";
            Pattern pattern;
            Matcher matcher;
            while((line = inputReader.readLine()) != null){
                //If the current line is a header
                if(HEADERS.containsKey(line)) {
                    //Get the regex for that header
                    regex = HEADERS.get(line);
                    //Save the header to know data type
                    header = line;
                } else {
                    //If the line is not a header
                    //get the regex pattern and start matching
                    pattern = Pattern.compile(regex);
                    matcher = pattern.matcher(line);
                    //If the line matches the pattern, then print it with the data type
                    if(matcher.find()){
                        System.out.println(header + " " + line);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(inputReader != null){
                    inputReader.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void WriteOutput(){

    }
}
