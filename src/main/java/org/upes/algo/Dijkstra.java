package org.upes.algo;

import org.upes.model.Beat;

import java.util.*;

public class Dijkstra
{
    private HashSet<NodeBeat> openList;
    private HashSet<NodeBeat> closedList;
    private ArrayList<NodeBeat> openListT;
    private ArrayList<NodeBeat> closedListT;

    private NodeBeat start, end;
    private NodeBeat current;

    private double factorScore;
    private double toTravel=0;

    public Dijkstra(double factorScore)
    {
        this.factorScore = factorScore;
    }

    private interface Fo
    {
        public boolean isFinished(NodeBeat current);
        public boolean isPathFound(NodeBeat current);
    }

    private class BeatFo implements Fo
    {
        @Override
        public boolean isFinished(NodeBeat current)
        {
            return end == current;
        }

        @Override
        public boolean isPathFound(NodeBeat current)
        {
            return end == current;
        }
    }

    private class DistanceFo implements Fo
    {
        double distance;

        public DistanceFo(double distance)
        {
            this.distance = distance;
        }

        @Override
        public boolean isFinished(NodeBeat current)
        {
            return  current.distance > distance;
        }

        @Override
        public boolean isPathFound(NodeBeat current)
        {
            return true;
        }
    }

    private class DistanceBeatFo implements Fo
    {
        double distance;

        public DistanceBeatFo(double distance)
        {
            this.distance = distance;
        }

        @Override
        public boolean isFinished(NodeBeat current)
        {
            return  (current.distance > distance || end == current);
        }

        @Override
        public boolean isPathFound(NodeBeat current)
        {
            return end == current;
        }
    }


    public void init(Collection<Beat> beats, Beat startB, Beat endB)
    {
        openList = new HashSet<>();
        closedList = new HashSet<>();
        openListT   = new ArrayList<>();
        closedListT = new ArrayList<>();

        // Create table to convert beat in NodeBeat
        Map<Beat, NodeBeat> tableBeat = new HashMap<>();

        for (Beat beat : beats)
        {
            NodeBeat node = new NodeBeat(beat, beat.getLongitude(), beat.getLatitude());
            tableBeat.put(beat, node);
        }

        // Create neighbours
        for (Beat beat : beats)
        {
            NodeBeat   nodeBeat   = tableBeat.get(beat);
            List<Beat> neighbours = beat.getNeighbours();
            for (Beat neighbour : neighbours)
            {
                NodeBeat neighbourNode = tableBeat.get(neighbour);
                nodeBeat.addNeighbour(neighbourNode);
            }
        }

        // Set start and end
        this.start = tableBeat.get(startB);
        if (endB == null)
        {
            this.end = null;
        }
        else
        {
            this.end = tableBeat.get(endB);
        }
    }

    public List<Beat> pathFinding(Collection<Beat> beats, Beat startB, Beat endB)
    {
        BeatFo beatFo = new BeatFo();
        return pathFinding(beats, startB, endB, beatFo);
    }

    public List<Beat> pathFinding(Collection<Beat> beats, Beat startB,Beat endB, double distance)
    {
        DistanceBeatFo distanceFo = new DistanceBeatFo(distance);
        toTravel=distance;
        return pathFinding(beats, startB, endB, distanceFo);
    }

    private List<Beat> pathFinding(Collection<Beat> beats, Beat startB, Beat endB, Fo condition)
    {
        init(beats, startB, endB);

        start.previous = null;
        current = start;
        start.computeDisntance(start);
        addNeighbours(current);
        addClosedList(current);

        while (!condition.isFinished(current) && openList.size() != 0)
        {
            current = bestOpenList();
            addNeighbours(current);
            addClosedList(current);
        }

        if(condition.isPathFound(current))
        {
            while(current.previous != null)
            {
                current.previous.next = current;
                current = current.previous;
            }
        }
        else
            start.next = null;
        return getPath();
    }

    public List<Beat> getPath()
    {
        List<Beat> path = new LinkedList<>();

        NodeBeat current = start;
        while (current != null)
        {
            path.add(current.beat);
            current = current.next;
        }
        return path;
    }

    public NodeBeat bestOpenList()
    {
        Iterator<NodeBeat> opLi = openList.iterator();
        NodeBeat           best = opLi.next();
        while(opLi.hasNext())
        {
            NodeBeat tmp = (NodeBeat) opLi.next();
            if(best.tScore(factorScore, end ,toTravel ) > tmp.tScore(factorScore , end , toTravel))
            {
                best = tmp;
            }
            else if(best.tScore(factorScore , end, toTravel) == tmp.tScore(factorScore , end , toTravel))
            {
                //if(best.tScore(factorScore , end) > tmp.tScore(factorScore , end))
                    best = tmp;
            }
        }

        openList.remove(best);
        openListT.remove(best);
        return best;
    }

    public void addNeighbours(NodeBeat current)
    {
        List<NodeBeat> nodes = current.nodes;
        for(int i=0; i<nodes.size(); i++)
        {
            if(openList.contains(nodes.get(i)))
            {
                NodeBeat clone = null;
                try
                {
                    clone = (NodeBeat) nodes.get(i).clone();
                }
                catch (CloneNotSupportedException e)
                {
                    e.printStackTrace();
                }
                clone.computeDisntance(current);
                if(clone.tScore(factorScore, end, toTravel) < nodes.get(i).tScore(factorScore, end, toTravel))
                {
                    nodes.get(i).previous = current;
                    nodes.get(i).next = null;
                    nodes.get(i).distance = clone.distance;
                    nodes.get(i).sumBeatScore = clone.sumBeatScore;
                }
            }
            else if(!closedList.contains(nodes.get(i)))
            {
                //on ajoute dans l'openList
                nodes.get(i).computeDisntance(current);
                nodes.get(i).previous = current;
                openList.add(nodes.get(i));
                openListT.add(nodes.get(i));
            }
        }
    }

    public void addClosedList(NodeBeat current)
    {
        openList.remove(current);
        closedList.add(current);
    }

    public static double euclidianDistance(NodeBeat a, NodeBeat b)
    {
        return Math.sqrt((b.getX() - a.getX())*(b.getX() - a.getX())
        + (b.getY() - a.getY())*(b.getY() - a.getY()));
    }

    public static double octileDistance(NodeBeat a, NodeBeat b)
    {
        double dx = Math.abs(a.getX() - b.getX());
        double dy = Math.abs(a.getY() - b.getY());
        return (dx + dy) + ((Math.sqrt(2) - 2)*Math.min(dx, dy));
    }


}
