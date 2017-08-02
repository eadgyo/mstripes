package org.upes.algo;

import org.upes.model.Beat;

import java.util.*;

public class Dijsktra
{
    private HashSet<NodeBeat> openList;
    private HashSet<NodeBeat> closedList;
    private ArrayList<NodeBeat> openListT;
    private ArrayList<NodeBeat> closedListT;

    private NodeBeat start, end;
    private NodeBeat current;

    public void init(Collection<Beat> beats, Beat startB, Beat endB)
    {
        openList = new HashSet<>();
        closedList = new HashSet<>();
        openListT   = new ArrayList<>();
        closedListT = new ArrayList<>();

        // Create table to convert beat in NodeBeat
        Map<Beat, NodeBeat> tableBeat = new HashMap<>();

        for (Beat beat : tableBeat.keySet())
        {
            NodeBeat node = new NodeBeat(beat, 0, 0); //beat.getX(), beat.getY());
            tableBeat.put(beat, node);
        }

        // Create neighbours
        for (Beat beat : tableBeat.keySet())
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
        this.end = tableBeat.get(endB);
    }

    public List<Beat> pathFinding(Collection<Beat> beats, Beat startB, Beat endB)
    {
        init(beats, startB, endB);

        start.previous = null;
        current = null;
        start.computeDisntance(start);
        addNeighbours(current);
        addClosedList(current);

        while (current != end && openList.size() != 0)
        {
            current = bestOpenList();
            addNeighbours(current);
            addClosedList(current);
        }

        if(current == end)
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
            if(best.tScore() > tmp.tScore())
            {
                best = tmp;
            }
            else if(best.tScore() == tmp.tScore())
            {
                if(best.tScore() > tmp.tScore())
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
                if(clone.tScore() < nodes.get(i).tScore())
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
