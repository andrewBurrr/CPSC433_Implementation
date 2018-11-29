/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package OrTree;

import static java.lang.Integer.max;
import java.util.Comparator;
import java.util.Random;

/**
 * Used by the PriorityQueue to order leaf nodes (Facts) based on or-tree 
 * search control.
 * @author thomasnewton
 */
public class OrTreeControl1 implements Comparator<Prob>{    
    @Override
    public int compare(Prob o1, Prob o2) {
        int val1,val2;
        int max = 4*max(o1.getScheduel().size(),o2.getScheduel().size());
        // 1st priority solved problems
        val1 = (o1.isSolved()) ? 0:max;
        val2 = (o2.isSolved()) ? 0:max;
        // 2nd priority unsolvable problems
        val1 += (o1.isUnsolvable()) ? 0:max/2;
        val2 += (o2.isUnsolvable()) ? 0:max/2;
        // 3rd priority deep problems
        val1 += o1.getScheduel().size();
        val2 += o2.getScheduel().size();
        // Add some random value 
        Random rand = new Random();
        val1 += rand.nextInt(max/4)-1;
        val2 += rand.nextInt(max/4)-1;
        
        return val1-val2;
    }
    
}
