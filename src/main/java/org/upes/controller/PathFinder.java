package org.upes.controller;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by This Pc on 18-07-2017.
 */
public class PathFinder {

    int matrix[][];
    Set<Integer> chowkis;
    Set<Integer> grids;
    int initialChowki;
    int n=54;
    int matWidth;
    int matHeight;

    public int[][] InitMatrix()
    {
        matrix= new int[][]{{10,28,42,43,41,36},
                            {19,3,8,16,38,6},
                            {22,1,2,17,13,7},
                            {31,15,21,9,4,26},
                            {37,23,32,14,5,34},
                            {49,39,11,27,20,33},
                            {25,30,12,35,24,44},
                            {45,29,18,46,47,50},
                            {40,48,52,51,53,54}};

        matWidth=matrix[0].length;
        matHeight=matrix.length;

        return matrix;
    }

    public  Set<Integer> InitChowkis()
    {
        chowkis=new HashSet<Integer>();
        chowkis.add(9);
        chowkis.add(11);
        chowkis.add(28);
        chowkis.add(32);
        chowkis.add(41);

        return chowkis;
    }

    public void CalculatePath()
    {

    }
}
