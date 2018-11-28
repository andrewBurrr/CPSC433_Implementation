package Structures;

import java.util.Objects;

public class Course {

    private String identifier;

    public Course(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.identifier);
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof Course){
            Course c = (Course) obj;
            return this.identifier.equals(c.identifier);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("%s\n", identifier);
    }

    public String getIdentifier() { return identifier; }
}
