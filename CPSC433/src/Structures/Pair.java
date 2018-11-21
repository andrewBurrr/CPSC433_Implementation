package Structures;

public class Pair {
    private final String xIdentifier;
    private final String yIdentifier;

    public Pair(String[] input) {
        this(input[0], input[1]);
    }

    public Pair( String xIdentifier, String yIdentifier ) {
        this.xIdentifier = xIdentifier;
        this.yIdentifier = yIdentifier;
    }

    @Override
    public String toString() {
        return String.format("%s, %s\n", xIdentifier, yIdentifier);
    }

    public String getxIdentifier() { return xIdentifier; }
    public String getyIdentifier() { return yIdentifier; }
}
