package Structures;

public class Pair<T1, T2> {
    private final T1 xIdentifier;
    private final T2 yIdentifier;

    public Pair( T1 xIdentifier, T2 yIdentifier ) {
        this.xIdentifier = xIdentifier;
        this.yIdentifier = yIdentifier;
    }
    public T1 getxIdentifier() { return xIdentifier; }
    public T2 getyIdentifier() { return yIdentifier; }
}
