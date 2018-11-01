package Utilities;

import java.io.*;
import java.lang.System;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager {
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private final String SLOTS_REGEX = "^[A-Z]{2}[\\s]*,[\\s]*[1-9]{1,2}:\\d{2}[\\s]*,[\\s]*\\d[\\s]*,[\\s]*\\d[\\s]*";
    private final String COURSES_REGEX = "^[A-Z]{4}[\\s]*\\d{3}[\\s]*LEC[\\s]*\\d{2}$";
    private final String LABS_REGEX = "^[A-Z]{4}[\\s]*\\d{3}[\\s]*[LEC[\\s]*\\d{2}[\\s]*]*(TUT|LAB)[\\s]*\\d{2}$";

    private final String NOT_COMPATIBLE_REGEX =  COURSES_REGEX + "," + COURSES_REGEX + "|" +
                                                    COURSES_REGEX + "," + LABS_REGEX + "|" +
                                                    LABS_REGEX + "," + LABS_REGEX ;
    public FileManager(){

    }

    // slots_pattern = "^[A-Z]{2}[\\s]*,[\\s]*[1-9]{1,2}:\\d{2}[\\s]*,[\\s]*\\d[\\s]*,[\\s]*\\d[\\s]*";
    // courses_pattern = "^[A-Z]{4}[\\s]*\\d{3}[\\s]*LEC[\\s]*\\d{2}[\\s]*"
    // labs_pattern = "^[A-Z]{4}[\\s]*\\d{3}[\\s]*LEC[\\s]*\\d{2}[\\s]*(TUT|LAB)[\\s]*\\d{2}[\\s]*"


    // not compatible = course pattern, course pattern
    //                                | course pattern, lab Pattern | lab pattern, lab patter

    // unwanted

    public void LoadConfig(String _configFileName){
        try{
            //Create file to read
            File inputFile = new File(_configFileName);
            //Create buffered reader
            reader = new BufferedReader(new FileReader(inputFile));

            Pattern coursesPattern = Pattern.compile(COURSES_REGEX);
            Pattern labsPattern = Pattern.compile(LABS_REGEX);

            //Line
            String line;
            while((line = reader.readLine()) != null){
                Matcher courseMatcher = coursesPattern.matcher(line);
                Matcher labMatcher = labsPattern.matcher(line);
                while(courseMatcher.find()) {
                    System.out.println("Course: " + line);
                }
                while(labMatcher.find()){
                    System.out.println("Lab: " + line);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(reader != null){
                    reader.close();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void LoadInput(){

    }

    public void WriteOutput(){

    }
}
