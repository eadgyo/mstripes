package org.upes.algo;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.JTS;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.operation.TransformException;
import org.upes.model.JsonOp;
import org.upes.model.SqlOp;

import javax.xml.soap.Node;
import java.util.*;

public class Astar {

    SqlOp sqlOp;
    JsonOp jsonOp;
    org.geotools.map.Layer layer;
    TreeMap<NodeGrid,String> openList;
    TreeMap<NodeGrid,String> closedList;
    NodeGrid startNode,endNode;
    int minDist,maxDist;
    NodeFactory nodeFactory;
    List<Double> scores;

    public Astar(SqlOp sqlOp, JsonOp jsonOp, org.geotools.map.Layer gridLayer)
    {
        this.sqlOp = sqlOp;
        this.jsonOp = jsonOp;
        this.layer = gridLayer;
        scores = sqlOp.getSortedScores();
    }

    public NodeGrid calculatePath(FeatureId startFeatureId,FeatureId endFeatureId,int minDist, int maxDist)
    {
        nodeFactory = new NodeFactory(sqlOp,jsonOp,layer);
        startNode = nodeFactory.createNodeGrid(startFeatureId.getID());
        endNode = nodeFactory.createNodeGrid(endFeatureId.getID());
        openList = new TreeMap<>();
        closedList = new TreeMap<>();
        nodeFactory.initStartGrid(startNode);
        openList.put(startNode,startFeatureId.getID());
        while(!openList.isEmpty())
        {
           NodeGrid q = openList.firstKey();

//           System.out.println("F: "+q.getGridFeature().getCurrFeatureId()+"  "+q.getF());
//            System.out.println("tree  "+openList.size());
//            for(Map.Entry<NodeGrid,String> entry : openList.entrySet()) {
//                double key = entry.getKey().getT();
//                String value = entry.getValue();
//                System.out.println(value + " => " + key);
//            }

           openList.remove(q);
           ArrayList<NodeGrid> templist = nodeFactory.createNeighbours(q);
           for (NodeGrid n : templist)
           {
               double g = q.getG()+nodeFactory.getDistance(q,n);
               double h = nodeFactory.getDistance(endNode,n);
               double f=n.calculateF(g,h,minDist,maxDist,(scores.size()-(scores.indexOf(n.getScore())+1)));
               if(n.getId().equals(endFeatureId.getID()))
               {    n.setG(g);
                   n.setH(h);
                   n.setF(f);
                   return n;}
               if(openList.containsValue(n.getId())) {
                   if (getKey(openList,n.getId()).getF() <= f) {
                       continue;
                   }
                   openList.remove(getKey(openList,n.getId()));
               }
               if(closedList.containsValue(n.getId()))
               {
                   if (getKey(closedList,n.getId()).getF() <= f) {
                       continue;
                   }
               }
               n.setG(g);
               n.setH(h);
               n.setF(f);
               openList.put(n,n.getId());
           }

           closedList.put(q,q.getId());

        }

        return null;
    }

    public static NodeGrid getKey(TreeMap<NodeGrid,String> map,String key)
    {
        for(Map.Entry<NodeGrid,String> entry:map.entrySet())
        {
            if(entry.getValue().equals(key))
            {
                return entry.getKey();
            }
        }
        return null;
    }


    public Set<FeatureId> getPath(NodeGrid end)
    {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
        Set<FeatureId> Ids= new HashSet<>();
        NodeGrid temp = end;
        while (temp.getParent()!=null)
        {
            Ids.add(ff.featureId(temp.getId()));
            temp = temp.getParent();
        }

        return Ids;
    }
}
