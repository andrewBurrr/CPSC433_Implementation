package Structures;

public class NotCompatible {
    private final Class c1;
    private final Class c2;

    public NotCompatible(Class c1, Class c2) {
        this.c1 = c1;
        this.c2 = c2;
    }

    @Override
    public String toString() {
        return String.format("%s, %s\n", c1.toString(), c2.toString());
    }
    
    public Class getClass(int n){
        switch(n){
            case 0: return c1;
            case 1: return c2;
            default: return null;
        }
    }
}
