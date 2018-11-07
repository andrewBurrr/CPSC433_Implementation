import Objects.Course;
import Objects.Lab;

import Structures.Pair;
import Structures.PartialAssignment;
import Structures.Preference;
import Structures.Slot;

import Exceptions.InvalidInputException;

import java.util.Set;
import java.util.LinkedHashSet;

import java.util.regex.Pattern;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


/**
 * File:    Reader.java
 * Author:  Andrew Burton
 * Section: CPSC 433, Fall 2018
 * Date:    October 27, 2018
 * Version: 1
 * Description:
 * Reader is the base class for the file for the parser, that initializes
 * Courses Union Labs
 * after the parser
 * @author Andrew Burton
 * @version 1
 *
 * */
public class Reader {

    // make objects for all item types
    // question: determine the difference between no more inputs and a parse exception

    // regular expressions
    private final Pattern sectionPattern = Pattern.compile("[\\s]*(Name|Course[\\s]+slots|Lab[\\s]+slots|Courses|Labs|Not[\\s]+compatible|Unwanted|Preferences|Pair|Partial[\\s]+assignments):[\\s]*");
    private final Pattern namePattern = Pattern.compile("[\\s]*[\\S]+[\\s]*");
    private final Pattern courseSlotPattern = Pattern.compile("[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*,[\\s]*\\d+[\\s]*,[\\s]*\\d+[\\s]*");
    private final Pattern labSlotPattern = Pattern.compile("[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*,[\\s]*\\d+[\\s]*,[\\s]*\\d+[\\s]*");
    private final Pattern coursePattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*");
    private final Pattern labPattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]+(LAB|TUT)[\\s]+\\d+[\\s*]");
    private final Pattern notCompatiblePattern1 = Pattern.compile(coursePattern + "," + coursePattern);
    private final Pattern notCompatiblePattern2 = Pattern.compile(coursePattern + "," + labPattern);
    private final Pattern notCompatiblePattern3 = Pattern.compile(labPattern + "," + labPattern);
    private final Pattern unwantedPattern1 = Pattern.compile(coursePattern + ",[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern unwantedPattern2 = Pattern.compile(labPattern + ",[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern preferencePattern1 = Pattern.compile("[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*," + coursePattern + ",[\\d]+");
    private final Pattern preferencePattern2 = Pattern.compile("[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*," + labPattern + ",[\\d]+");
    private final Pattern pairPattern1 = Pattern.compile(coursePattern + "[\\s]*,[\\s]*" + coursePattern);
    private final Pattern pairPattern2 = Pattern.compile(coursePattern + "[\\s]*,[\\s]*" + labPattern);
    private final Pattern pairPattern3 = Pattern.compile(labPattern + "[\\s]*,[\\s]*" + labPattern);
    private final Pattern partialAssignmentPattern1 = Pattern.compile(coursePattern + ",[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern partialAssignmentPattern2 = Pattern.compile(labPattern + ",[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");

    // named attributes
    private String name;
    private Set<Slot> courseSlots;
    private Set<Slot> labSlots;
    private Set<Course> courses; // generate objects
    private Set<Lab> labs; // generate objects
    private Set<NotCompatible> notCompatible;
    private Set<Unwanted> unwanted;
    private Set<Preference<String,String,String,String>> preferences;
    private Set<Pair<String,String>> pairs;
    private Set<PartialAssignment<String,String,String>> partialAssignments;

    // constructor
    public Reader(String fileName) {

        Scanner fileRead;
        String temp;

        try {

            fileRead = new Scanner(new File(fileName));

            while (fileRead.hasNext()) {
                temp = fileRead.nextLine().trim();
                switch (temp) {
                    case "Name:": readName(fileRead); break;
                    case "Course slots:": readCourseSlots(fileRead); break;
                    case "Lab slots:": readLabSlots(fileRead); break;
                    case "Courses:": readCourses(fileRead); break;
                    case "Labs:": readLabs(fileRead); break;
                    case "Not compatible:": readNotCompatible(fileRead); break;
                    case "Unwanted:": readUnwanted(fileRead); break;
                    case "Preferences:": readPreferences(fileRead); break;
                    case "Pairs:": readPairs(fileRead); break;
                    case "Partial assignments": readPartialAssignments(fileRead); break;
                    default: if (!temp.isEmpty()) throw new InvalidInputException(String.format("Could not parse: %s", temp)); // some condition implies the file is empty) break;
                }
                if (fileRead.nextLine().isEmpty()) break; // whitespaceexception

                // if all vars ae not null then quit
            }

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("The specified document, %s, does not exist", fileName);
            System.exit(-1);
        } catch (InvalidInputException invalidInputException) {
            System.out.printf("Could not parse line[%d] in ");
            invalidInputException.printStackTrace();
            // display message
        } catch (EOFException eOFException) {
            eOFException.printStackTrace();
            // display message
        } catch (IOException iOException) {
            iOException.printStackTrace();
            // display message
        }
    }

    private void readName(Scanner fileRead) throws IOException, InvalidInputException {
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + namePattern + "$")) {
                name = fileRead.next();
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException("Name could not be found");
            }
        }
    }

    // note, regex does not confirm valid course start time in this version
    private void readCourseSlots(Scanner fileRead) throws IOException, InvalidInputException {
        courseSlots = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + courseSlotPattern + "$")) { // 2 additional regexs for monday, then tuesday, else error
                courseSlots.add(new Slot(fileRead.next().split("[\\s]*,[\\s]*")));
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Course Slots: %s", fileRead.next()));
            }
        }
    }

    // note, regex does not confirm valid lab start time in this version
    public void readLabSlots(Scanner fileRead) throws IOException, InvalidInputException {
        labSlots = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + labSlotPattern + "$")) {
                labSlots.add(new Slot(fileRead.next().split("[\\s]*,[\\s]*")));
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Lab Slots: %s", fileRead.next()));
            }
        }

    }

    // compare regex against hard constraints
    public void readCourses(Scanner fileRead) throws InvalidInputException {
        courses = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + coursePattern + "$")) {
                courses.add(new Course(fileRead.next()));
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Courses: %s", fileRead.next()));
            }
        }
    }

    // compare regex against hard constraints
    public void readLabs(Scanner fileRead) throws InvalidInputException {
        labs = new LinkedHashSet<>();
        Pattern pattern = Pattern.compile("^[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]+(LAB|TUT)[\\s]+\\d+[\\s*]$");
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + labPattern + "$")) {
                labs.add(new Lab(fileRead.next()));
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Labs: %s", fileRead.next()));
            }
        }

    }

    // needs completion: 3 regex's for switch
    public void readNotCompatible(Scanner fileRead) throws InvalidInputException {
        notCompatible = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + notCompatiblePattern1 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + notCompatiblePattern2 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + notCompatiblePattern3 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Not Compatible: %s", fileRead.next()));
            }
        }
    }

    //needs completion: 2 regex's for switch
    public void readUnwanted(Scanner fileRead) throws InvalidInputException {
        unwanted = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + unwantedPattern1 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + unwantedPattern2 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Unwanted: %s", fileRead.next()));
            }
            break;
        }
    }

    // needs completion: 2 regex's for switch
    public void readPreferences(Scanner fileRead) throws InvalidInputException {
        preferences = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + preferencePattern1 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + preferencePattern2 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + sectionPattern + "$")){
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Preferences: %s", fileRead.next()));
            }
            break;
        }
    }

    // needs completion: 3 regex's for switch
    public void readPairs(Scanner fileRead)throws InvalidInputException {
        pairs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + pairPattern1 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + pairPattern2 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + pairPattern3 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Pair: %s", fileRead.next()));
            }
        }
    }

    // needs completion: 2 regex's for switch
    public void readPartialAssignments(Scanner fileRead) throws InvalidInputException {
        partialAssignments = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext("^" + partialAssignmentPattern1 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + partialAssignmentPattern2 + "$")) {
                // build object
            } else if (fileRead.hasNext("^" + sectionPattern + "$")) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Partial Assignments: %s", fileRead.next()));
            }
        }
    }
}
