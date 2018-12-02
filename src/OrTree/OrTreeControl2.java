/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrTree;

import Structures.Assignment;
import java.util.Comparator;
import java.util.Random;

/**
 * Used by the PriorityQueue to order leaf nodes (Facts) based on or-tree 
 * search control.
 * @author thomasnewton
 */
public class OrTreeControl2 implements Comparator<Prob>{
    private final Assignment[] guide;
    private final int numPartAssign;
    
    public OrTreeControl2(Assignment[] guide, int numPartAssign){
        this.guide = guide;
        this.numPartAssign = numPartAssign;
    }
    
    @Override
    public int compare(Prob o1, Prob o2) {
        int val1,val2;
        int max = 4*guide.length;
        // 1st priority solved problems
        val1 = (o1.isSolved()) ? 0:max;
        val2 = (o2.isSolved()) ? 0:max;
        // 2nd priority unsolvable problems
        val1 += (o1.isUnsolvable()) ? 0:max/2;
        val2 += (o2.isUnsolvable()) ? 0:max/2;
        // 3rd priority guide problems
        Assignment g = guide[o1.getScheduel().size()-numPartAssign-1];
        val1 += (o1.getScheduel().get(g.getCourse()).equals(g.getSlot())) ? 0:max/4;
        g = guide[o2.getScheduel().size()-numPartAssign-1];
        val2 += (o2.getScheduel().get(g.getCourse()).equals(g.getSlot())) ? 0:max/4;
        // 4th priority deep problems
        val1 += o1.getScheduel().size()/2;
        val2 += o2.getScheduel().size()/2;
        
        return val1-val2;
    }
    
}
