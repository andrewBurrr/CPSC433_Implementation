package Structures;

import java.util.Objects;

public class Class {

    private final String identifier;

    public Class(String identifier) {
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
        if(obj instanceof Class){
            Class c = (Class) obj;
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
