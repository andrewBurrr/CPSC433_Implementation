package Structures;

public class Lab {
    private final String identifier;

    public Lab(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return String.format("%s\n", identifier);
    }

    public String getIdentifier() { return identifier; }
}
