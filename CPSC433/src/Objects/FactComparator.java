/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import Structures.Assignment;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Used by the PriorityQueue to order leaf nodes (Facts) based on or-tree 
 * search control.
 * @author thomasnewton
 */
public class FactComparator implements Comparator<Fact>{

    
    public FactComparator(ArrayList<Assignment> guide){
        
    }
    
    @Override
    public int compare(Fact o1, Fact o2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
