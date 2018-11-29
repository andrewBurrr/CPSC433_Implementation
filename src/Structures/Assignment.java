/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structures;

/**
 *
 * @author thomasnewton
 */
public class Assignment {
    private final Class course;
    private final Slot slot;
    
    public Assignment(){
        this.course = null;
        this.slot = null;
    }
    
    public Assignment(Class course, Slot slot){
        this.course = course;
        this.slot = slot;
    }
    
    public Class getCourse(){
        return course;
    }
    
    public Slot getSlot(){
        return slot;
    }
}
