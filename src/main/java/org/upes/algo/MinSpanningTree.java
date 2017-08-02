package org.upes.algo;

import org.upes.model.Beat;
import org.upes.model.Patrol;

import java.util.ArrayList;
import java.util.List;

public class MinSpanningTree
{
    /**
     * Create the minimal spanning tree
     * @param patrols list of patrols
     * @param beats sorted list of beats
     */
    public void kruskal(List<Patrol> patrols, List<Beat> beats)
    {
        if (patrols.size() == 0)
            return;

        // Add all beats to the grid storage
        ArrayList<Beat> gridStorage = new ArrayList<>();
        gridStorage.addAll(beats);

        // Create Patrol Chaukis as empty
        ArrayList<Patrol> patrolsChaukis = new ArrayList<>();

        // Set initial patrol and initial grid
        Patrol patrol = patrols.get(0);
        Beat grid = (Beat) patrol.getGridLocation();

        while (patrolsChaukis.size() < beats.size() - 1)
        {
            grid = gridStorage.remove(0);
            
        }
    }
}
