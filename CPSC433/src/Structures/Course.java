package Structures;

public class Course {

    private String identifier;

    public Course(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return String.format("%s\n", identifier);
    }

    public String getIdentifier() { return identifier; }
}
