package Parser;
// container types
import Structures.Slot;
import Structures.Lecture;
import Structures.Lab;
import Structures.NotCompatible;
import Structures.Unwanted;
import Structures.Preference;
import Structures.Pair;
// exceptions
import Exceptions.InvalidInputException;
import Structures.Course;
// java libraries
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;



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
    private HashMap<Course, Slot> partialAssignments;
    private HashMap<Lecture, Set<Lab>> courseLabs;


    public Reader(String fileName) {

        courseLabs = new HashMap();
        
        Scanner fileRead;
        String temp;

        try {

            fileRead = new Scanner(new File(fileName)).useDelimiter("\\n");

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
                temp = fileRead.next(courseSlotPattern).trim();
                courseSlots.add(new Slot(temp.split(",\\s*")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!(fileRead.next().trim().isEmpty())){
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
            } else if (!(fileRead.next().trim().isEmpty())){
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
                Lecture temp = new Lecture(fileRead.next().trim());
                courses.add(temp);
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if(!fileRead.next().trim().isEmpty()) {
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
                Lab tempLab = new Lab(fileRead.next().trim());
                labs.add(tempLab);
                Lecture tempLec = new Lecture(tempLab.getName(), tempLab.getNumber(),"LEC", tempLab.getSection());
                if(courseLabs.containsKey(tempLec)){
                    courseLabs.get(tempLec).add(tempLab);
                } else {
                    LinkedHashSet<Lab> temp = new LinkedHashSet();
                    temp.add(tempLab);
                    courseLabs.put(tempLec, temp);
                }
                
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.next().trim().isEmpty()) {
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
                notCompatible.add(new NotCompatible(fileRead.next().trim().split(",")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.next().trim().isEmpty()){
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
                unwanted.add(new Unwanted(fileRead.next().trim().split(",")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.next().trim().isEmpty()) {
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
                preferences.add(new Preference(fileRead.next().trim().split(",")));
            } else if (fileRead.hasNext(SECTION)){
                break;
            } else  if (!fileRead.next().trim().isEmpty()) {
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
                pairs.add(new Pair(fileRead.next().trim().split(",")));
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.next().trim().isEmpty()) {
                throw new InvalidInputException(String.format("Failed To Parse Line In Pair: %s", fileRead.next()));
            }
        }
        System.out.print(pairs.toString().replace("[", "").replace(", ", "").replace("]", ""));
    }

    // needs completion: 2 regex's for switch
    private void readPartialAssignments(Scanner fileRead) throws InvalidInputException {
        Pattern partialAssignmentPattern = Pattern.compile("((" + COURSE + "," + DAY_COURSE + ")|(" + LAB + "," + DAY_LAB +"))" + "," + TIME);
        partialAssignments = new HashMap<>();
        while (fileRead.hasNext()) {
            if (fileRead.hasNext(partialAssignmentPattern)) {
                String[] line = fileRead.next().trim().split(",");
                if(line[0].matches(".*(TUT|LAB).*")) {
                    partialAssignments.put(new Lab(line[0]), new Slot(Arrays.copyOfRange(line, 1, 3)));
                } else {
                    partialAssignments.put(new Lecture(line[0]), new Slot(Arrays.copyOfRange(line, 1, 3)));
                }
            } else if (fileRead.hasNext(SECTION)) {
                break;
            } else if (!fileRead.next().trim().isEmpty()) {
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
    public HashMap<Course, Slot> getPartialAssignments(){ return partialAssignments; }
    public HashMap<Lecture, Set<Lab>> getCourseLabs(){ return courseLabs; }
}