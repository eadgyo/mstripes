package org.upes.model;

import org.opengis.filter.identity.FeatureId;

import java.util.HashMap;

/**
 * Created by This Pc on 07-07-2017.
 */
public class Beat {

    private FeatureId id;
    private double    area;
    private double    value;
    /**
     * Score per layer
     */
    private HashMap<String, Double> scoreLayer = new HashMap<>();

    public Beat(FeatureId id)
    {
        this.id=id;
        area=0;
        value =0;
    }

    public FeatureId getId()
    {
        return id;
    }
    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void addScore(String layerName, double score)
    {
        Double aDouble = scoreLayer.get(layerName);
        if (aDouble == null)
            aDouble = 0.0;

        aDouble += score;
        scoreLayer.put(layerName, aDouble);
    }

    public double getScore()
    {
        double myScore = 0;

        for (Double aDouble : scoreLayer.values())
        {
            myScore += aDouble;
        }
        return myScore;
    }

    public double getScore(String layer)
    {
        return scoreLayer.get(layer);
    }

    public void addValue(double v)
    {
        value += v;
    }
}
