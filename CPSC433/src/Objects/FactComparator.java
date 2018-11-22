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
    private ArrayList<Assignment> guide;
    private OTreeModel model;
    
    public FactComparator(ArrayList<Assignment> guide, OTreeModel model){
        this.guide = guide;
        this.model = model;
    }
    
    @Override
    public int compare(Fact o1, Fact o2) {
        int val1=0,val2=0;
        val1 = (model.solved(o1)) ? 0 : 1;
        val2 = (model.solved(o2)) ? 0 : 1;
        
    }
    
}
