package org.upes.algo;

import org.upes.model.Beat;

import java.util.ArrayList;
import java.util.List;

public class NodeBeat implements Cloneable
{
    public Beat beat;
    List<NodeBeat> nodes = new ArrayList<>();
    NodeBeat previous = null;
    NodeBeat next = null;

    // Weight = distance + sumBeatScore
    double distance     = 0;
    double sumBeatScore = 0;

    double x = 0;
    double y = 0;

    public NodeBeat(Beat beat, double x, double y)
    {
        this.beat = beat;
        this.x = x;
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void computeDisntance(NodeBeat previous)
    {
        distance = Dijkstra.euclidianDistance(this, previous) + previous.distance;
        sumBeatScore = beat.getScore() + previous.sumBeatScore;
    }

    public double getDistance()
    {
        return distance;
    }

    public double getSumBeatScore()
    {
        return sumBeatScore;
    }

    public double tScore()
    {
        return distance + sumBeatScore;
    }

    public void addNeighbour(NodeBeat neighbour)
    {
        nodes.add(neighbour);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        NodeBeat clone = (NodeBeat) super.clone();

        clone.nodes = new ArrayList<>(nodes);
        clone.previous = previous;
        clone.next = next;
        clone.distance = distance;
        clone.sumBeatScore = sumBeatScore;

        clone.x = x;
        clone.y = y;

        return clone;
    }
}
