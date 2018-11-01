package Utilities;

import java.io.*;
import java.lang.System;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileManager {
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    private final Map<String, String> HEADERS = new HashMap<String, String>();

    private final String SLOT_REGEX = "^[A-Z]{2}[\\s]*,[\\s]*[1-9]{1,2}:\\d{2}[\\s]*,[\\s]*\\d[\\s]*,[\\s]*\\d[\\s]*";
    private final String COURSE_REGEX = "^[A-Z]{4}[\\s]*\\d{3}[\\s]*LEC[\\s]*\\d{2}$";
    private final String LAB_REGEX = "^[A-Z]{4}[\\s]*\\d{3}[\\s]*[LEC[\\s]*\\d{2}[\\s]*]*(TUT|LAB)[\\s]*\\d{2}$";

    private final String NOT_COMPATIBLE_REGEX =  COURSE_REGEX + "," + COURSE_REGEX + "|" +
                                                    COURSE_REGEX + "," + LAB_REGEX + "|" +
                                                    LAB_REGEX + "," + LAB_REGEX ;
    public FileManager(){

    }

    private void InitializeHeaders(){
        HEADERS.put("Name:", "Name");
        HEADERS.put("Course slots:", "Course Slots");
        HEADERS.put("Lab slots:", "Lab Slots");
        HEADERS.put("Courses:", "Courses");
        HEADERS.put("Labs:", "Labs");
        HEADERS.put("Not compatible:", "Not Compatible");
        HEADERS.put("Unwanted:", "Unwanted");
        HEADERS.put("Preference:", "Preference");
        HEADERS.put("Pair:", "Pair");
        HEADERS.put("Partial assignments:", "Partial Assignments");
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

            Pattern coursesPattern = Pattern.compile(COURSE_REGEX);
            Pattern labsPattern = Pattern.compile(LAB_REGEX);

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
