package Structures;

public class Lab extends Course {
    private final String lecture;
    
   public Lab(String name, String number, String type, String section) {
        super(name, number, type, section);
        lecture = null;
    }
   
   public Lab(String name, String number, String type, String section, String lecture) {
       super(name, number, type, section);
       this.lecture = lecture.trim();
   }
   
   public Lab(String[] input){
       super(input[0], input[1], input[input.length-2], input[input.length-1]);
       if(input.length ==6){
           this.lecture = input[3].trim();
       } else {
           this.lecture = null;
       }
   }
   
   public Lab(String input){
       this(input.trim().split("\\s+"));
   }
   
   public String getLecture(){
       return lecture;
   }
   
    @Override
   public String toString(){
       String s = super.toString();
       if(lecture!=null){
        return String.format("%s, LEC %s", s, lecture);
       } else {
           return s;
       }
   }
}
