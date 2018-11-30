package Structures;

public class Course {

    private final String name;
    private final String number;
    private final String type;
    private final String section;

    public Course(String name, String number, String type, String section) {
        this.name = name.trim();
        this.number = number.trim();
        this.type = type.trim();
        this.section = section.trim();
    }
    
    public Course(String input){
            this(input.trim().split("\\s+"));
    }
    
    public Course(String[] input){
        this(input[0].trim(), input[1].trim(), input[2].trim(), input[3].trim());
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s %s", name, number, type, section);
    }

    public String getName() { return name; }
    public String getNumber() { return number; }
    public String getType() { return type; }
    public String getSection() { return section; }
}
