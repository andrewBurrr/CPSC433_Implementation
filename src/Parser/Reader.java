package Parser;
// container types
import Structures.*;
// exceptions
import Exceptions.InvalidInputException;
import org.omg.CORBA.DynAnyPackage.Invalid;
// java libraries
import java.util.*;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;



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
    private Set<Lecture> courses;
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
                courseSlots.add(new Slot(temp.split(",")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Course Slots: %s", fileRead.next()));
            }
        }
        System.out.print(courseSlots.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // note, regex does not confirm valid lab start time in this version
    private void readLabSlots(Scanner fileRead) throws InvalidInputException {
        Pattern labSlotPattern = Pattern.compile(DAY_LAB + "," + TIME + "," + VALUE + "," + VALUE);
        labSlots = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(labSlotPattern)) {
                labSlots.add(new Slot(fileRead.nextLine().split(",")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Lab Slots: %s", fileRead.next()));
            }
        }
        System.out.print(labSlots.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // compare regex against hard constraints
    private void readCourses(Scanner fileRead) throws InvalidInputException {
        Pattern coursePattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+(LEC)[\\s]+\\d+[\\s]*");
        courses = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(coursePattern)) {
                courses.add(new Lecture(fileRead.next()));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Courses: %s", fileRead.next()));
            }
        }
        System.out.print(courses.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // compare regex against hard constraints
    private void readLabs(Scanner fileRead) throws InvalidInputException {
        Pattern labPattern = Pattern.compile("[\\s]*(CPSC|SENG)[\\s]+\\d+[\\s]+((LEC)[\\s]+\\d+[\\s]+)?(LAB|TUT)[\\s]+\\d+[\\s]*");
        labs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(labPattern)) {
                labs.add(new Lab(fileRead.next()));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Labs: %s", fileRead.next()));
            }
        }
        System.out.print(labs.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 3 regex's for switch
    private void readNotCompatible(Scanner fileRead) throws InvalidInputException {
        Pattern notCompatiblePattern = Pattern.compile("(" + COURSE + "|" + LAB + "),(" + COURSE + "|" + LAB + ")");
        notCompatible = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext( notCompatiblePattern)) {
                String[] notCompatibleList = fileRead.next().split(",");
                if ((notCompatibleList[1].contains("TUT")) || (notCompatibleList[1].contains("LAB"))){
                    if ((notCompatibleList[0].contains("TUT")) || (notCompatibleList[0].contains("LAB"))){
                        Lab lab1 = new Lab(notCompatibleList[0]);
                        Lab lab2 = new Lab(notCompatibleList[1]);
                        if ((labs.equals(lab1)) && (labs.equals(lab2))){
                            HashMap<Lab, Lab> labLab = new HashMap<>();
                            labLab.put(lab1, lab2);
                            notCompatible.add(new NotCompatible(null, null, labLab));
                        }
                        else{
                            throw new InvalidInputException("At least 1 lab could not be found in Labs");
                        }
                    } else {
                        Course course = new Course(notCompatibleList[0]);
                        Lab lab = new Lab(notCompatibleList[1]);
                        if ((labs.equals(course)) && (labs.equals(lab))){
                            HashMap<Course, Lab> courseLab = new HashMap<>();
                            courseLab.put(new Course(notCompatibleList[0]), new Lab(notCompatibleList[1]));
                            notCompatible.add(new NotCompatible(null, courseLab, null));
                        }else{
                            System.out.println("Test");
                            throw new InvalidInputException("Either Course or Lab could not be found");
                        }
                    }
                } else {
                    Course course1 = new Course(notCompatibleList[0]);
                    Course course2 = new Course(notCompatibleList[1]);
                    if ((courses.equals(course1)) && (courses.equals(course2))){
                        HashMap<Course, Course> courseCourse = new HashMap<>();
                        courseCourse.put(new Course(notCompatibleList[0]), new Course(notCompatibleList[1]));
                        notCompatible.add(new NotCompatible(courseCourse, null, null));
                    }else{
                        System.out.println("Course 1 string: " + course1.getIdentifier());
                        System.out.println("Course 2 string: " + course2.getIdentifier());
                        System.out.println("Course 1 in courses: " + courses.contains(course1));
                        System.out.println("Course 2 in courses: " + courses.contains(course2));
                        throw new InvalidInputException("At least 1 Course could not be found");
                    }
                }
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.nextLine().equals("")){
                throw new InvalidInputException(String.format("Failed To Parse Line In Not Compatible: %s", fileRead.next()));
            }
        }
        System.out.print(notCompatible.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    //needs completion: 2 regex's for switch
    private void readUnwanted(Scanner fileRead) throws InvalidInputException {
        Pattern unwantedPattern = Pattern.compile("((" + COURSE + "," + DAY_COURSE + ")|(" + LAB + "," + DAY_LAB +"))," + TIME);
        unwanted = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(unwantedPattern)) {
                //unwantedList = [Course/Lab Indentifier, Slot Day, Slot Time]
                String [] unwantedList = fileRead.next().split(",");
                // if unwanted[0] contains "TUT" or "LAB" it is a Lab
                if ((unwantedList[0].contains("TUT")) || (unwantedList[0].contains("LAB"))){
                    Lab lab = new Lab(unwantedList[0]);
                    if (labs.equals(lab)){
                        for(Slot slot:labSlots){
                            //Search through the labSlot and find the one that matches Day and Time
                            if((slot.getDay().equals(unwantedList[1])) && (slot.getTime()) == unwantedList[2]){
                                //Add to unwanted set
                                unwanted.add(new Unwanted(lab, slot));
                            } else{
                                throw new InvalidInputException("There is no labSlot that matches the input");
                            }
                        }
                    } else{
                        throw new InvalidInputException("There is no Lab that matches the input");
                    }
                } else { //otherwise it is a Course Identifier
                    Course course = new Course(unwantedList[0]);
                        //Search through the course to find the one that matches with Course Identifier
                    if(courses.equals(course)){
                        for(Slot slot:courseSlots){
                            //Search through courseSlot to find the one that matches Day and Time
                            if ((slot.getDay().equals(unwantedList[1])) && (slot.getTime() == unwantedList[2])){
                                unwanted.add(new Unwanted(course, slot));
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
                throw new InvalidInputException(String.format("Failed To Parse Line In Unwanted: %s", fileRead.next()));
            }
        }
        System.out.print(unwanted.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    private void readPreferences(Scanner fileRead) throws InvalidInputException {
        Pattern preferencePattern = Pattern.compile("((" + DAY_COURSE + "," + TIME + "," + COURSE + ")|(" + DAY_LAB + "," + TIME + "," + LAB + "))," + VALUE);
        preferences = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(preferencePattern)) {
                String [] preferenceList = fileRead.next().split(",");
                if ((preferenceList[2].contains("TUT")) || (preferenceList[2].contains("LAB"))){
                    Lab lab = new Lab(preferenceList[2]);
                    if (labs.equals(lab)){
                        for(Slot slot:labSlots){
                            if ((slot.getDay().equals(preferenceList[0])) && (slot.getTime().equals(preferenceList[1]))){
                                preferences.add(new Preference(lab, slot, preferenceList[3]));
                            } else{
                                throw new InvalidInputException("Error incorrect Slot input");
                            }
                        }
                    } else{
                        throw new InvalidInputException("Error incorrect Lab input");
                    }
                }else{
                    Course course = new Course(preferenceList[2]);
                    if (courses.equals(course)){
                        for (Slot slot:courseSlots){
                            if ((slot.getDay().equals(preferenceList[0])) && (slot.getTime().equals(preferenceList[1]))){
                                preferences.add(new Preference(course, slot, preferenceList[3]));
                            } else{
                                throw new InvalidInputException("Error incorrect Slot input");
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
        System.out.print(preferences.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 3 regex's for switch
    private void readPairs(Scanner fileRead)throws InvalidInputException {
        Pattern pairPattern = Pattern.compile("(" + COURSE + "|" + LAB + "),(" + COURSE + "|" + LAB + ")");
        pairs = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(pairPattern)) {
                String[] pairList = fileRead.next().split(",");
                if ((pairList[1].contains("TUT")) || (pairList[1].contains("LAB"))){
                    if ((pairList[0].contains("TUT")) || (pairList[0].contains("LAB"))){
                        Lab lab1 = new Lab(pairList[0]);
                        Lab lab2 = new Lab(pairList[1]);
                        if ((labs.equals(lab1)) && (labs.equals(lab2))){
                            HashMap<Lab, Lab> labLab = new HashMap<>();
                            labLab.put(lab1, lab2);
                            notCompatible.add(new NotCompatible(null, null, labLab));
                        }
                        else{
                            throw new InvalidInputException("At least 1 lab could not be found in Labs");
                        }
                    } else {
                        Course course = new Course(pairList[0]);
                        Lab lab = new Lab(pairList[1]);
                        if ((labs.equals(course)) && (labs.equals(lab))){
                            HashMap<Course, Lab> courseLab = new HashMap<>();
                            courseLab.put(new Course(pairList[0]), new Lab(pairList[1]));
                            notCompatible.add(new NotCompatible(null, courseLab, null));
                        }else{
                            throw new InvalidInputException("Either Course or Lab could not be found");
                        }
                    }
                } else {
                    Course course1 = new Course(pairList[0]);
                    Course course2 = new Course(pairList[1]);
                    if ((courses.equals(course1)) && (courses.equals(course2))){
                        HashMap<Course, Course> courseCourse = new HashMap<>();
                        courseCourse.put(new Course(pairList[0]), new Course(pairList[1]));
                        notCompatible.add(new NotCompatible(courseCourse, null, null));
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
        System.out.print(pairs.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    private void readPartialAssignments(Scanner fileRead) throws InvalidInputException {
        Pattern partialAssignmentPattern = Pattern.compile("((" + COURSE + "," + DAY_COURSE + ")|(" + LAB + "," + DAY_LAB +"))" + "," + TIME);
        partialAssignments = new LinkedHashSet<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(partialAssignmentPattern)) {
                //partAssignList = [Course/Lab Indentifier, Slot Day, Slot Time]
                String [] partAssignList = fileRead.next().split(",");
                // if partAssignList[0] contains "TUT" or "LAB" it is a Lab
                if ((partAssignList[0].contains("TUT")) || (partAssignList[0].contains("LAB"))){
                    Lab lab = new Lab(partAssignList[0]);
                    if (labs.equals(lab)){
                        for(Slot slot:labSlots){
                            //Search through the labSlot and find the one that matches Day and Time
                            if((slot.getDay().equals(partAssignList[1])) && (slot.getTime()) == partAssignList[2]){
                                //Add to unwanted set
                                unwanted.add(new Unwanted(lab, slot));
                            } else{
                                throw new InvalidInputException("There is no labSlot that matches the input");
                            }
                        }
                    } else{
                        throw new InvalidInputException("There is no Lab that matches the input");
                    }
                } else { //otherwise it is a Course Identifier
                    Course course = new Course(partAssignList[0]);
                    if(courses.equals(course)){
                        for(Slot slot:courseSlots){
                            //Search through courseSlot to find the one that matches Day and Time
                            if ((slot.getDay().equals(partAssignList[1])) && (slot.getTime() == partAssignList[2])){
                                unwanted.add(new Unwanted(course, slot));
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
        System.out.print(partialAssignments.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    public String getName() { return name; }
    public Set<Slot> getCourseSlots() { return courseSlots; }
    public Set<Slot> getLabSlots() { return labSlots; }
    public Set<Lecture> getCourses() { return courses; }
    public Set<Lab> getLabs() { return labs; }
    public Set<NotCompatible> getNotCompatible() { return notCompatible; }
    public Set<Unwanted> getUnwanted() { return unwanted; }
    public Set<Preference> getPreferences() { return preferences; }
    public Set<Pair> getPairs() { return pairs; }
    public Set<PartialAssignment> getPartialAssignments(){ return partialAssignments; }
}
