package org.upes.algo;

import org.upes.model.GridFeature;
import org.upes.model.SqlOp;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class NodeGrid implements Comparable<NodeGrid>{

    GridFeature gridFeature;
    List<GridFeature> neighbours = new ArrayList<>();

    NodeGrid parent = null;
    NodeGrid next = null;

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    double g = 0;
    double h = 0;
    double t = 0;
    double f = 0;
    double score =0;
    double x = 0;
    double y = 0;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public NodeGrid getParent() {
        return parent;
    }

    public void setParent(NodeGrid parent) {
        this.parent = parent;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getG() {
        return g;
    }
    public double getF() {
        return f;
    }

    public GridFeature getGridFeature() {
        return gridFeature;
    }

    public NodeGrid (GridFeature gridFeature,double x, double y)
    {
        this.gridFeature = gridFeature;
        this.x = x;
        this.y = y;
    }


    public double getH()
    {
        return this.h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void setF(double f) {
        this.f = f;
    }

    public String getId()
    {
        return gridFeature.getCurrFeatureId();
    }


    public double getX()
    {
        return x;
    }
    public double getY() { return y;}
    public double calculateF (double g,double h,int min,int max,int rank)
    {
        min = min*1000;
        max = max*1000;
        t= rank*2000;
        double temp = abs(((min+max)/2)-g-h)+t;
        return temp;
    }

    @Override
    public int compareTo(NodeGrid o) {
        if (this.f-o.getF() < 0)
            return -1;
        else if (this.getId().equals(o.getId()) && this.getF() == o.getF())
            return 0;
        else
            return 1;
    }

    public boolean equals(Object o)
    {
        if (o.getClass().equals(this.getClass()))
        {
            NodeGrid temp = (NodeGrid) o;
            return(this.getId().equals(temp.getId()));
        }
        return false;
    }
}
