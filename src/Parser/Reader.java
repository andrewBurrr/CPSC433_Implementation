package Parser;
// container types
import Structures.*;
// exceptions
import Exceptions.InvalidInputException;
import org.omg.CORBA.DynAnyPackage.Invalid;
// java libraries
import java.sql.SQLOutput;
import java.util.*;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * Read input file to objects and apply regex and validate hard constraints are satisfied
 * Please leave since and author along with brief notes on changes made after each edit
 * @since 2018-11-13
 * @author Andrew Burton
 **/
public class Reader {

    // shared regular expressions
    private final Pattern DAY_COURSE = Pattern.compile("[\\s]*(MO|TU)[\\s]*");
    private final Pattern DAY_LAB = Pattern.compile("[\\s]*(MO|TU|FR)[\\s]*");
    private final Pattern TIME = Pattern.compile("[\\s]*([0-1]?[0-9]|[2][0-3]):([0-5][0-9])[\\s]*");
    private final Pattern VALUE = Pattern.compile("[\\s]*\\d+[\\s]*");
    private final Pattern SECTION = Pattern.compile("[\\s]*(Name|Course[\\s]+slots|Lab[\\s]+slots|Courses|Labs|Not[\\s]+compatible|Unwanted|Preferences|Pair|Partial[\\s]+assignments):[\\s]*");
    private final Pattern COURSE = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*");
    private final Pattern LAB = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+((LEC)[\\s]+\\d+[\\s]+)?(LAB|TUT)[\\s]+\\d+[\\s]*");
    // MO, 17:00, CPSC 203 LEC 95 TUT 95, 25

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
                temp = temp.replaceAll("\\r", "");
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
            System.exit(-1);
        }
    }

    private void readName(Scanner fileRead) throws InvalidInputException {
        Pattern namePattern = Pattern.compile("[\\s]*[\\S]+[\\s]*");
        if (fileRead.hasNext(namePattern) & name == null) {
            name = fileRead.nextLine();
        } else {
            throw new InvalidInputException("Name could not be found");
        }
        System.out.println(name);
    }

    // note, regex does not confirm valid course start time in this version
    private void readCourseSlots(Scanner fileRead) throws InvalidInputException {
        Pattern courseSlotPattern = Pattern.compile(DAY_COURSE + "," + TIME + "," + VALUE + "," + VALUE);
        courseSlots = new LinkedHashSet<>();
        String temp;
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(courseSlotPattern)) { // 2 additional regexs for monday, then tuesday, else error
                temp = fileRead.next(courseSlotPattern);
                System.out.println(temp);
                temp = temp.replaceAll("\\s*","");
                courseSlots.add(new Slot(temp.split(",\\s*")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                System.out.println(fileRead.next());
                System.out.println(fileRead.next());
                throw new InvalidInputException(String.format("Failed To Parse Line In Course Slots: %s", fileRead.next()));
            }
        }
        System.out.print(courseSlots.toString().replace("[", "").replace(", ", " ").replace("]", "") + " ");
    }

    // note, regex does not confirm valid lab start time in this version
    private void readLabSlots(Scanner fileRead) throws InvalidInputException {
        Pattern labSlotPattern = Pattern.compile(DAY_LAB + "," + TIME + "," + VALUE + "," + VALUE);
        labSlots = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(labSlotPattern)) {
                labSlots.add(new Slot(fileRead.nextLine().split(",\\s*")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Lab Slots: %s", fileRead.next()));
            }
        }
        System.out.print(labSlots.toString().replace("[", "").replace(", ", " ").replace("]", "")+ " ");
    }

    // compare regex against hard constraints
    private void readCourses(Scanner fileRead) throws InvalidInputException {
        Pattern coursePattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*");
        courses = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(coursePattern)) {
                courses.add(new Course(fileRead.next().replaceAll("\\r","")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Courses: %s", fileRead.next()));
            }
        }
        System.out.print(courses.toString().replace("[", "").replace(", ", " ").replace("]", "") + " ");
    }

    // compare regex against hard constraints
    private void readLabs(Scanner fileRead) throws InvalidInputException {
        Pattern labPattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+((LEC)[\\s]+\\d+[\\s]+)?(LAB|TUT)[\\s]+\\d+[\\s]*");
        labs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(labPattern)) {
                labs.add(new Lab(fileRead.next().replaceAll("\\r","")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Labs: %s", fileRead.next()));
            }
        }
        System.out.print(labs.toString().replace("[", "").replace(", ", " ").replace("]", "") + " ");
    }

    // needs completion: 3 regex's for switch
    private void readNotCompatible(Scanner fileRead) throws InvalidInputException {
        Pattern notCompatiblePattern = Pattern.compile("(" + COURSE + "|" + LAB + "),(" + COURSE + "|" + LAB + ")");
        notCompatible = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext( notCompatiblePattern)) {
                // notCompatible = [Class/Lab, Class/Lab]
                String[] notCompatibleList = fileRead.next().replaceAll("\\r","").split(",\\s*");
                if ((notCompatibleList[1].contains("TUT")) || (notCompatibleList[1].contains("LAB"))){
                    // if it is [Lab, Lab]
                    if ((notCompatibleList[0].contains("TUT")) || (notCompatibleList[0].contains("LAB"))){
                        Lab lab1 = new Lab(notCompatibleList[0]);
                        Lab lab2 = new Lab(notCompatibleList[1]);
                        if ((labs.contains(lab1)) && (labs.contains(lab2))) {
                            HashMap<Class, Class> labLab = new HashMap<Class, Class>();
                            labLab.put(lab1, lab2);
                            notCompatible.add(new NotCompatible(labLab));
                        } else{
                            throw new InvalidInputException("Error at least 1 lab could not be found");
                        }
                    } else {
                        //[Class, Lab]
                        Class course = new Class(notCompatibleList[0]);
                        Lab lab = new Lab(notCompatibleList[1]);
                        //If valid input
                        if ((courses.contains(course)) && (labs.contains(lab))){
                            HashMap<Class, Class> courseLab = new HashMap<Class, Class>();
                            courseLab.put(course, lab);
                            notCompatible.add(new NotCompatible(courseLab));
                        }else{
                            throw new InvalidInputException("Either Course or Lab could not be found");
                        }
                    }
                } else if ((notCompatibleList[0].contains("TUT")) || (notCompatibleList[0].contains("LAB"))) {
                    //[Lab, Class]
                    Class course = new Class(notCompatibleList[1]);
                    Lab lab = new Lab(notCompatibleList[0]);
                    //If valid input
                    if ((courses.contains(course)) && (labs.contains(lab))){
                        HashMap<Class, Class> courseLab = new HashMap<Class, Class>();
                        courseLab.put(course, lab);
                        notCompatible.add(new NotCompatible(courseLab));
                    }else{
                        throw new InvalidInputException("Either Lab or Course could not be found");
                    }
                } else{
                    //[Class, Class]
                    Class course1 = new Class(notCompatibleList[0]);
                    Class course2 = new Class(notCompatibleList[1]);
                    if ((courses.contains(course1)) && (courses.contains(course2))) {
                        HashMap<Class, Class> courseCourse = new HashMap<Class, Class>();
                        courseCourse.put(course1, course2);
                        notCompatible.add(new NotCompatible(courseCourse));
                    } else{
                        throw new InvalidInputException("At least one course could not be found");
                    }
                }
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Not Compatible: %s", fileRead.next()));
            }
        }
        System.out.print(notCompatible.toString().replace("[", "").replace(", ", " ").replace("]", "") + " ");
    }

    //needs completion: 2 regex's for switch
    private void readUnwanted(Scanner fileRead) throws InvalidInputException {
        Pattern unwantedPattern = Pattern.compile("((" + COURSE + "," + DAY_COURSE + ")|(" + LAB + "," + DAY_LAB +"))," + TIME);
        unwanted = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(unwantedPattern)) {
                //unwantedList = [Class/Lab Indentifier, Slot Day, Slot Time]
                String [] unwantedList = fileRead.next().replaceAll("\\r","").split(",\\s*");
                // if unwanted[0] contains "TUT" or "LAB" it is a Lab
                if ((unwantedList[0].contains("TUT")) || (unwantedList[0].contains("LAB"))){
                    Lab lab = new Lab(unwantedList[0]);
                    if (labs.contains((Lab) lab)){
                        System.out.println("Pass");
                        for(Slot slot:labSlots){
                            //Search through the labSlot and find the one that matches Day and Time
                            if((slot.getDay().equals(unwantedList[1])) && (slot.getTime()).equals(unwantedList[2])){
                                //Add to unwanted set
                                unwanted.add(new Unwanted(lab, slot));
                            } else{
                                throw new InvalidInputException("There is no labSlot that matches the input");
                            }
                        }
                    } else{
                        throw new InvalidInputException("There is no Lab that matches the input");
                    }
                } else { //otherwise it is a Class Identifier
                    Class course = new Class(unwantedList[0]);
                        //Search through the course to find the one that matches with Class Identifier
                    if(courses.contains(course)){
                        for(Slot slot:courseSlots){
                            //Search through courseSlot to find the one that matches Day and Time
                            if ((slot.getDay().equals(unwantedList[1])) && (slot.getTime().equals(unwantedList[2]))){
                                unwanted.add(new Unwanted(course, slot));
                            }
                        }
                    }else{
                        throw new InvalidInputException("No course was found");
                    }
                }
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Unwanted: %s", fileRead.next()));
            }
        }
        System.out.print(unwanted.toString().replace("[", "").replace(", ", " ").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    private void readPreferences(Scanner fileRead) throws InvalidInputException {
        Pattern preferencePattern = Pattern.compile("((" + DAY_COURSE + "," + TIME + "," + COURSE + ")|(" + DAY_LAB + "," + TIME + "," + LAB + "))," + VALUE);
        preferences = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(preferencePattern)) {
                //preferenceList = [Day, Time, Class/Lab, Value]
                String [] preferenceList = fileRead.next().replaceAll("\\r","").split(",\\s*");
                // If it is a Lab
                if ((preferenceList[2].contains("TUT")) || (preferenceList[2].contains("LAB"))){
                    Lab lab = new Lab(preferenceList[2]);
                    if (labs.contains(lab)){
                        for(Slot slot:labSlots){
                            //Valid Slot
                            if ((slot.getDay().equals(preferenceList[0])) && (slot.getTime().equals(preferenceList[1]))){
                                preferences.add(new Preference(lab, slot, preferenceList[3]));
                            }
                        }
                    } else{
                        throw new InvalidInputException("Error incorrect Lab input");
                    }
                }else{
                    //if it is a course
                    Class course = new Class(preferenceList[2]);
                    if (courses.contains(course)){
                        for (Slot slot:courseSlots){
                            //Check for valid input
                            if ((slot.getDay().equals(preferenceList[0])) && (slot.getTime().equals(preferenceList[1]))){
                                preferences.add(new Preference(course, slot, preferenceList[3]));
                            }
                        }
                    }else{
                        throw new InvalidInputException("Error incorrect Course Input");
                    }
                }
            } else if (fileRead.hasNext(SECTION)){
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Preferences: %s", fileRead.next()));
            }
        }
        System.out.print(preferences.toString().replace("[", "").replace(", ", " ").replace("]", "") + " ");
    }

    // needs completion: 3 regex's for switch
    private void readPairs(Scanner fileRead)throws InvalidInputException {
        Pattern pairPattern = Pattern.compile("(" + COURSE + "|" + LAB + "),(" + COURSE + "|" + LAB + ")");
        pairs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(pairPattern)) {
                // pairList = [Class/Lab, Class/Lab]
                String[] pairList = fileRead.next().replaceAll("\\r","").split(",\\s*");
                if ((pairList[1].contains("TUT")) || (pairList[1].contains("LAB"))){
                    //[Lab, Lab]
                    if ((pairList[0].contains("TUT")) || (pairList[0].contains("LAB"))){
                        Lab lab1 = new Lab(pairList[0]);
                        Lab lab2 = new Lab(pairList[1]);
                        if ((labs.contains(lab1)) && (labs.contains(lab2))){
                            HashMap<Class, Class> labLab = new HashMap<>();
                            labLab.put(lab1, lab2);
                            pairs.add(new Pair(labLab));
                        }
                        else{
                            throw new InvalidInputException("At least 1 lab could not be found in Labs");
                        }
                    } else {
                        Class course = new Class(pairList[0]);
                        Lab lab = new Lab(pairList[1]);
                        if ((labs.contains(course)) && (labs.contains(lab))){
                            HashMap<Class, Class> courseLab = new HashMap<>();
                            courseLab.put(new Class(pairList[0]), new Lab(pairList[1]));
                            pairs.add(new Pair(courseLab));
                        }else{
                            throw new InvalidInputException("Either Course or Lab could not be found");
                        }
                    }
                } else {
                    Class course1 = new Class(pairList[0]);
                    Class course2 = new Class(pairList[1]);
                    if ((courses.contains(course1)) && (courses.contains(course2))){
                        HashMap<Class, Class> courseCourse = new HashMap<>();
                        courseCourse.put(new Class(pairList[0]), new Class(pairList[1]));
                        pairs.add(new Pair(courseCourse));
                    }else{
                        throw new InvalidInputException("At least 1 Course could not be found");
                    }
                }
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Pair: %s", fileRead.next()));
            }
        }
        System.out.print(pairs.toString().replace("[", "").replace(", ", " ").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    private void readPartialAssignments(Scanner fileRead) throws InvalidInputException {
        Pattern partialAssignmentPattern = Pattern.compile("((" + COURSE + "," + DAY_COURSE + ")|(" + LAB + "," + DAY_LAB +"))" + "," + TIME);
        partialAssignments = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(partialAssignmentPattern)) {
                //partAssignList = [Class/Lab Indentifier, Slot Day, Slot Time]
                String [] partAssignList = fileRead.next().replaceAll("\\r","").split(",\\s*");
                // if partAssignList[0] contains "TUT" or "LAB" it is a Lab
                if ((partAssignList[0].contains("TUT")) || (partAssignList[0].contains("LAB"))){
                    Lab lab = new Lab(partAssignList[0]);
                    if (labs.contains(lab)){
                        for(Slot slot:labSlots){
                            //Search through the labSlot and find the one that matches Day and Time
                            if((slot.getDay().equals(partAssignList[1])) && (slot.getTime()) == partAssignList[2]){
                                //Add to unwanted set
                                partialAssignments.add(new PartialAssignment(lab, slot));
                            } else{
                                throw new InvalidInputException("There is no labSlot that matches the input");
                            }
                        }
                    } else{
                        throw new InvalidInputException("There is no Lab that matches the input");
                    }
                } else { //otherwise it is a Class Identifier
                    Class course = new Class(partAssignList[0]);
                    if(courses.contains(course)){
                        for(Slot slot:courseSlots){
                            //Search through courseSlot to find the one that matches Day and Time
                            if ((slot.getDay().equals(partAssignList[1])) && (slot.getTime() == partAssignList[2])){
                                partialAssignments.add(new PartialAssignment(course, slot));
                            } else{
                                throw new InvalidInputException("There is no courseSlot that matches input");
                            }
                        }
                    } else{
                        throw new InvalidInputException("There is no Course that matches input");
                    }
                }
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Partial Assignments: %s", fileRead.next()));
            }
        }
        System.out.print(partialAssignments.toString().replace("[", "").replace(", ", " ").replace("]", "") + "\t");
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
    public HashMap<Course, Set<Lab>> getCourseLabs(){ return null;}
}
