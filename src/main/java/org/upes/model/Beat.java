package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.opengis.filter.identity.FeatureId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by This Pc on 07-07-2017.
 */
public class Beat {

    private List<Beat> neighbours = new ArrayList<>();
    private FeatureId id;
    private Geometry geometry;
    private double    area;
    private double    value;
    private int       rank;
    private double    globalScore;

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

    public List<Beat> getNeighbours()
    {
        return neighbours;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
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
        this.globalScore += score;


        scoreLayer.put(layerName, aDouble);
    }

    public double getGlobalScore()
    {
        return globalScore;
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

    public void addNeighbour(Beat beat)
    {this.neighbours.add(beat);}

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
