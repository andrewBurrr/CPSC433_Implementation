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
       super(input[0], input[1], input[2], input[3]);
       if(input.length ==6){
           this.lecture = input[5].trim();
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
}
