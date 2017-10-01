package org.upes.model;

import org.upes.Constants;

import java.util.HashMap;
import java.util.Map;

public class Score {

    /**
     * Name of layer link to score
     */
    Map<String,Integer> Scores =new HashMap<>();

    public Score()
    {
        Scores.put(Constants.StrSUPPORTIVE,3);
        Scores.put(Constants.StrNEUTRAL,0);
        Scores.put(Constants.StrDEFECTIVE,-5);
    }

    public int getNeutralScore() { return Scores.get(Constants.StrNEUTRAL); }
    public int getSupportiveScore()
    {
        return Scores.get(Constants.StrSUPPORTIVE);
    }
    public int getDefectiveScore()
    {
        return Scores.get(Constants.StrDEFECTIVE);
    }

}
