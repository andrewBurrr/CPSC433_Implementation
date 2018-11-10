package Parser;

import Structures.Slot;
import Structures.Course;
import Structures.Lab;
import Structures.NotCompatible;
import Structures.Unwanted;
import Structures.Preference;
import Structures.Pair;
import Structures.PartialAssignment;

import Exceptions.InvalidInputException;

import java.util.Set;
import java.util.Scanner;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;

import java.io.FileNotFoundException;



/**
 * File:    Parser.Reader.java
 * Author:  Andrew Burton
 * Section: CPSC 433, Fall 2018
 * Date:    October 27, 2018
 * Version: 1
 * Description:
 * Read input file to objects and apply regex and validate hard constraints are satisfied
 * Please leave since and author along with brief notes on changes made after each edit
 * @since 2018-11-08
 * @author Andrew Burton
 *
 * */
public class Reader {

    // make objects for all item types
    // question: determine the difference between no more inputs and a parse exception

    // regular expressions
    private final Pattern sectionPattern = Pattern.compile("[\\s]*(Name|Course[\\s]+slots|Lab[\\s]+slots|Courses|Labs|Not[\\s]+compatible|Unwanted|Preferences|Pair|Partial[\\s]+assignments):[\\s]*");
    private final Pattern namePattern = Pattern.compile("[\\s]*[A-Za-z0-9]+[\\s]*");
    private final Pattern courseSlotPattern = Pattern.compile("[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*,[\\s]*\\d+[\\s]*,[\\s]*\\d+[\\s]*");
    private final Pattern labSlotPattern = Pattern.compile("[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*,[\\s]*\\d+[\\s]*,[\\s]*\\d+[\\s]*");
    private final Pattern coursePattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*");
    private final Pattern labPattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+((LEC)[\\s]+\\d+[\\s]+)?(LAB|TUT)[\\s]+\\d+[\\s]*");
    private final Pattern notCompatiblePattern = Pattern.compile("(" + coursePattern + "|" + labPattern + "),(" + coursePattern + "|" + labPattern + ")");
    private final Pattern unwantedPattern1 = Pattern.compile(coursePattern + ",[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern unwantedPattern2 = Pattern.compile(labPattern + ",[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern preferencePattern1 = Pattern.compile("[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*," + coursePattern + ",[\\s]*[\\d]+[\\s]*");
    private final Pattern preferencePattern2 = Pattern.compile("[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*," + labPattern + ",[\\s]*[\\d]+[\\s]*");
    private final Pattern pairPattern = Pattern.compile("(" + coursePattern + "|" + labPattern + "),(" + coursePattern + "|" + labPattern + ")");
    private final Pattern partialAssignmentPattern1 = Pattern.compile(coursePattern + ",[\\s]*(MO|TU)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern partialAssignmentPattern2 = Pattern.compile(labPattern + ",[\\s]*(MO|TU|FR)[\\s]*,[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");

    // named attributes
    private String name;
    private Set<Slot> courseSlots;
    private Set<Slot> labSlots;
    private Set<Course> courses;
    private Set<Lab> labs;
    private Set<NotCompatible> notCompatible;
    private Set<Unwanted> unwanted;
    private Set<Preference> preferences;
    private Set<Pair> pairs;
    private Set<PartialAssignment> partialAssignments;


    public Reader(String fileName) {

        Scanner fileRead;
        String temp;

        try {

            fileRead = new Scanner(new File(fileName)).useDelimiter("\n");

            while (fileRead.hasNext()) {
                temp = fileRead.nextLine().trim();
                System.out.println(temp);
                switch (temp) {
                    case "Name:": readName(fileRead); break;
                    case "Course slots:": readCourseSlots(fileRead); break;
                    case "Lab slots:": readLabSlots(fileRead); break;
                    case "Courses:": readCourses(fileRead); break;
                    case "Labs:": readLabs(fileRead); break;
                    case "Not compatible:": readNotCompatible(fileRead); break;
                    case "Unwanted:": readUnwanted(fileRead); break;
                    case "Preferences:": readPreferences(fileRead); break;
                    case "Pair:": readPairs(fileRead); break;
                    case "Partial assignments:": readPartialAssignments(fileRead); break;
                    default: if (!temp.isEmpty()) throw new InvalidInputException(String.format("Could not parse: %s", temp));
                }
            }
            fileRead.close();

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.printf("The specified document, %s, does not exist\n", fileName);
            fileNotFoundException.printStackTrace();
            System.exit(-1);
        } catch (InvalidInputException invalidInputException) {
            System.out.println("Parser input error");
            invalidInputException.printStackTrace();
        }
    }

    private void readName(Scanner fileRead) throws InvalidInputException {
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(namePattern) & name == null) {
                name = fileRead.nextLine();
                break;
            } else if (fileRead.hasNext(sectionPattern) & name != null) {
                break;
            } else {
                throw new InvalidInputException("Name could not be found");
            }
        }
        System.out.println(name);
    }

    // note, regex does not confirm valid course start time in this version
    private void readCourseSlots(Scanner fileRead) throws InvalidInputException {
        courseSlots = new LinkedHashSet<>();
        String temp;
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(courseSlotPattern)) { // 2 additional regexs for monday, then tuesday, else error
                temp = fileRead.next(courseSlotPattern);
                courseSlots.add(new Slot(temp.split(",")));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if (!fileRead.next().isEmpty()){
                throw new InvalidInputException(String.format("Failed To Parse Line In Course Slots: %s", fileRead.next()));
            }
        }
        System.out.print(courseSlots.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // note, regex does not confirm valid lab start time in this version
    public void readLabSlots(Scanner fileRead) throws InvalidInputException {
        labSlots = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(labSlotPattern)) {
                labSlots.add(new Slot(fileRead.nextLine().split(",")));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else {
                throw new InvalidInputException(String.format("Failed To Parse Line In Lab Slots: %s", fileRead.next()));
            }
        }
        System.out.print(labSlots.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // compare regex against hard constraints
    public void readCourses(Scanner fileRead) throws InvalidInputException {
        courses = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(coursePattern)) {
                courses.add(new Course(fileRead.next()));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if(!fileRead.next().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Courses: %s", fileRead.next()));
            }
        }
        System.out.print(courses.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // compare regex against hard constraints
    public void readLabs(Scanner fileRead) throws InvalidInputException {
        labs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(labPattern)) {
                labs.add(new Lab(fileRead.next()));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if (!fileRead.next().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Labs: %s", fileRead.next()));
            }
        }
        System.out.print(labs.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 3 regex's for switch
    public void readNotCompatible(Scanner fileRead) throws InvalidInputException {
        notCompatible = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext( notCompatiblePattern)) {
                notCompatible.add(new NotCompatible(fileRead.next().split(",")));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if (!fileRead.next().isEmpty()){
                throw new InvalidInputException(String.format("Failed To Parse Line In Not Compatible: %s", fileRead.next()));
            }
        }
        System.out.print(notCompatible.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    //needs completion: 2 regex's for switch
    public void readUnwanted(Scanner fileRead) throws InvalidInputException {
        unwanted = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(unwantedPattern1)) {
                unwanted.add(new Unwanted(fileRead.next().split(",")));
            } else if (fileRead.hasNext(unwantedPattern2)) {
                unwanted.add(new Unwanted(fileRead.next().split(",")));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if (!fileRead.next().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Unwanted: %s", fileRead.next()));
            }
        }
        System.out.print(unwanted.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    public void readPreferences(Scanner fileRead) throws InvalidInputException {
        preferences = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(preferencePattern1)) {
                preferences.add(new Preference(fileRead.next().split(",")));
            } else if (fileRead.hasNext(preferencePattern2)) {
                preferences.add(new Preference(fileRead.next().split(",")));
            } else if (fileRead.hasNext(sectionPattern)){
                break;
            } else  if (!fileRead.next().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Preferences: %s", fileRead.next()));
            }
        }
        System.out.print(preferences.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 3 regex's for switch
    public void readPairs(Scanner fileRead)throws InvalidInputException {
        pairs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(pairPattern)) {
                pairs.add(new Pair(fileRead.next().split(",")));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if (!fileRead.next().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Pair: %s", fileRead.next()));
            }
        }
        System.out.print(pairs.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    public void readPartialAssignments(Scanner fileRead) throws InvalidInputException {
        partialAssignments = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(partialAssignmentPattern1)) {
                partialAssignments.add(new PartialAssignment(fileRead.next().split(",")));
            } else if (fileRead.hasNext(partialAssignmentPattern2)) {
                partialAssignments.add(new PartialAssignment(fileRead.next().split(",")));
            } else if (fileRead.hasNext(sectionPattern)) {
                break;
            } else if (!fileRead.next().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Partial Assignments: %s", fileRead.next()));
            }
        }
        System.out.print(partialAssignments.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    public String getName() { return name; }
    public Set<Slot> getCourseSlots() { return courseSlots; }
    public Set<Slot> getLabSlots() { return labSlots; }
    public Set<Course> getCourses() { return courses; }
    public Set<Lab> getLabs() { return labs; }
    public Set<NotCompatible> getNotCompatible() { return notCompatible; }
    public Set<Unwanted> getUnwanted() { return unwanted; }
    public Set<Preference> getPreferences() { return preferences; }
    public Set<Pair> getPairs() { return pairs; }
    public Set<PartialAssignment> getPartialAssignments(){ return partialAssignments; }
}