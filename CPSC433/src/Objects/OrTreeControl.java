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
public class OrTreeControl implements Comparator<Fact>{
    private ArrayList<Assignment> guide;
    private OTreeModel model;
    
    public OrTreeControl(ArrayList<Assignment> guide, OTreeModel model){
        this.guide = guide;
        this.model = model;
    }
    
    @Override
    public int compare(Fact o1, Fact o2) {
        int val1,val2;
        int max = 4*guide.size();
        // 1st priority solved problems
        val1 = (model.solved(o1)) ? 0:max;
        val2 = (model.solved(o2)) ? 0:max;
        // 2nd priority unsolvable problems
        val1 += (model.unsolvable(o1)) ? 0:max/2;
        val2 += (model.unsolvable(o2)) ? 0:max/2;
        // 3rd priority guide problems
        val1 += (o1.getScheduel().containsKey(guide.get(o1.getScheduel().size()-1))) ? 0:max/2-1;
        val2 += (o2.getScheduel().containsKey(guide.get(o2.getScheduel().size()-1))) ? 0:max/2-1;
        // 4th priority deep problems
        val1 += o1.getScheduel().size();
        val2 += o2.getScheduel().size();
        
        return val1-val2;
    }
    
}
