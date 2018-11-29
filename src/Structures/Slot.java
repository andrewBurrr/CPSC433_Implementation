package Structures;

import java.util.Objects;

public class Slot {
    private final String day;
    private final String time;
    private final int max;
    private final int min;

    public Slot(String[] input) {
        this.day = input[0].trim();
        this.time = input[1].trim();
        if(input.length==4){
            this.max = Integer.parseInt(input[2].trim());
            this.min = Integer.parseInt(input[3].trim());
        } else{
            this.max = Integer.MAX_VALUE;
            this.min = 0;
        }
    }

    public Slot(String day, String time){
        this.day = day.trim();
        this.time = time.trim();
        this.max = Integer.MAX_VALUE;
        this.min = 0;
    }
    
    public Slot( String day, String time, String max, String min ) {
        this.day = day.trim();
        this.time = time.trim();
        this.max = Integer.parseInt(max.trim());
        this.min = Integer.parseInt(min.trim());
    }
    
    public Slot( String day, String time, int max, int min ) {
        this.day = day.trim();
        this.time = time.trim();
        this.max = max;
        this.min = min;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.day);
        hash = 47 * hash + Objects.hashCode(this.time);
        hash = 47 * hash + this.max;
        hash = 47 * hash + this.min;
        return hash;
    }

        
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Slot){
            Slot s = (Slot) obj;
            if((this.day.equals(s.day))&&(this.time.equals(s.time))){
                return true;
            }
        }
        return false;
    }
        
        
    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s", day, time, max, min);
    }

    public String getDay() { return this.day; }
    public String getTime() { return this.time; }
    public int getMax() { return this.max; }
    public int getMin() {return this.min; }
}
