package algoritmos;

import java.io.Serializable;

/**
 *
 * @author Pc
 */
public class ItemSim implements Comparable<ItemSim>, Serializable {
    private long id;
    private double sim;
    
    public ItemSim(long i, double s){id = i;sim = s;}
    public ItemSim(){id = 0;sim = 0;}
    public long getId() {return id;}
    public double getSim() {return sim;}

    @Override
    public int compareTo(ItemSim o) {
        if(sim < o.sim){return 1;}
        if (sim > o.sim){return -1;}
        if (sim == o.sim && id != o.id){return 1;}
        return 0;
    } 
}