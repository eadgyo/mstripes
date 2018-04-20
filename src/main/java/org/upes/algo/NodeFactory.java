package org.upes.algo;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.Layer;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;


import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.upes.model.GridFeature;
import org.upes.model.JsonOp;
import org.upes.model.SqlOp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NodeFactory {

    SqlOp sqlOp;
    JsonOp jsonOp;
    CoordinateReferenceSystem crs;
    SimpleFeatureCollection col;

    public NodeFactory ( SqlOp sqlOp, JsonOp jsonOp, Layer layer)
    {
        this.sqlOp = sqlOp;
        this.jsonOp = jsonOp;
        this.crs = layer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
        try {
            col = (SimpleFeatureCollection) layer.getFeatureSource().getFeatures();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NodeGrid createNodeGrid(String featureId)
    {
        ArrayList<String> tempArrayList = new ArrayList<>();
        tempArrayList.add(featureId);
        HashMap<String,GridFeature> map = sqlOp.findFeatures(tempArrayList);
        Double y = sqlOp.getLatitude(featureId);
        Double x = sqlOp.getLongitude(featureId);
        Double score = sqlOp.getScore(featureId);
        NodeGrid nodeGrid = new NodeGrid(map.get(featureId),x,y);
        nodeGrid.setScore(score);
        return nodeGrid;
    }

    public void initStartGrid(NodeGrid start)
    {
        start.f = 0;
    }

    public ArrayList<NodeGrid> createNeighbours(NodeGrid grid)
    {
        ArrayList<NodeGrid> nodes = new ArrayList<>();
        ArrayList<String> neighbours = grid.getGridFeature().getNeighbourList();
        for(String gridId:neighbours)
        {
            NodeGrid temp = createNodeGrid(gridId);
            if(temp.getGridFeature().getParentBeat().equals(grid.getGridFeature().getParentBeat()))
            {
                temp.setParent(grid);
                nodes.add(temp);
            }
        }

        return nodes;
    }

    public double getDistance(NodeGrid end,NodeGrid start)
    {
        try {

           Coordinate startcoor = new Coordinate(start.getY(),start.getX());
           Coordinate endcoor = new Coordinate(end.getY(),end.getX());
           double dist = JTS.orthodromicDistance(startcoor,endcoor,crs);
//           System.out.print(dist+" ");
           return dist;
        } catch (TransformException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
