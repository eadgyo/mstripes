package org.upes.model;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.AreaFunction;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.upes.Constants;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.IOException;
import java.util.*;

/**
 * Created by eadgyo on 27/06/17.
 */

public class ComputeModel extends SimpleModel
{
    public List<Patrol> patrols = new ArrayList<Patrol>();

    public ComputeModel(JsonOp jsonOp, SqlOp sqlOp) {
        super(jsonOp , sqlOp);
    }

    public FeatureId getStartGrid() {
        return startGrid;
    }

    public void setStartGrid(FeatureId startGrid) {
        this.startGrid = startGrid;
    }

    public FeatureId getEndGrid() {
        return endGrid;
    }

    public void setEndGrid(FeatureId endGrid) {
        this.endGrid = endGrid;
    }

    private enum GeomType {
        POLYGON,
        LINE,
        POINT
    }

    private LinkedList<Beat> scoreResult = null;
    private ArrayList<Beat> sortedBeats = null;
    private org.opengis.geometry.Envelope region=null;
    private FeatureId startGrid=null;
    private FeatureId endGrid=null;

    public void setRegion(org.opengis.geometry.Envelope eve)
    {
        region=eve;
    }

    public org.opengis.geometry.Envelope getRegion()
    {
        return region;
    }

    public Patrol getPatrol(int n)
    {
        return patrols.get(n);
    }

    @Override
    public void checkLayer(Layer addedLayer)
    {
    //      initScoreData(addedLayer);
   //     checkLayerRoad(addedLayer);
   //     updateRoadLength(addedLayer, null);
    }

    public ArrayList<Beat> getSortedBeats()
    {
        return sortedBeats;
    }

    public void checkLayerRoad(Layer testedLayer)
    {
        if (featureSource.getSchema().getTypeName().equalsIgnoreCase(Constants.ROAD_NAME_FILE))
        {
            // Add column
            tableModel.addColumn("RoadLength");

            // Compute road length for already added layer
            java.util.List<Layer> layers = map.layers();
            for (Layer layer : layers)
            {
                updateRoadLength(layer, testedLayer);
            }
        }
    }

    public boolean isRoadLengthNecessary(Layer addedLayer)
    {
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
        try
        {
            collection = (FeatureCollection<SimpleFeatureType, SimpleFeature>) addedLayer.getFeatureSource().getFeatures();
            try (FeatureIterator<SimpleFeature> features = collection.features())
            {
                while (features.hasNext())
                {
                    SimpleFeature feature = features.next();
                    Vector        vector  = new Vector<>();
                    if (feature.getAttribute(Constants.ATTRIBUTE_STATUS) != null)
                        return true;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public void updateRoadLength(Layer addedLayer, Layer layerRoad)
    {
        if (layerRoad == null)
            layerRoad = getLayer(Constants.ROAD_NAME_FILE);

        if (layerRoad == null || addedLayer == layerRoad || !isRoadLengthNecessary(addedLayer))
            return;

        calculate_road_length(layerRoad, addedLayer);
        updateRoadTable(addedLayer, scoreResult);
    }

    public void updateRoadTable(Layer addedLayer, LinkedList<Beat> beats)
    {
        Iterator<Beat> beatIterator = beats.iterator();

        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
        try
        {
            collection = (FeatureCollection<SimpleFeatureType, SimpleFeature>) addedLayer.getFeatureSource()
                                                                                         .getFeatures();
            FeatureIterator<SimpleFeature> features = collection.features();
            int roadColumn = tableModel.addColumnIfNeeded("RoadLength");
            int layerStartIndex = tableModel.getLayerStartIndex(addedLayer.getTitle());
            int layerEndIndex = tableModel.getLayerEndIndex(addedLayer.getTitle());
            for (int row = layerStartIndex; row < layerEndIndex; row++)
            {
                SimpleFeature next = features.next();
                Beat temp=beatIterator.next();
                Object        statusValue = next.getAttribute(Constants.ATTRIBUTE_STATUS);
                double status;
                if (statusValue instanceof Integer)
                {
                    double statusValueT = (Integer) statusValue;
                    statusValue = statusValueT;
                }
                status = (double) statusValue;
                double result = status * temp.getValue();
                tableModel.setValueAt(result, row, roadColumn);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void calculate_road_length(Layer roadLayer, Layer layer)
    {
        SimpleFeatureIterator linefeatures=null;
        SimpleFeatureIterator simpleFeatureIterator=null;
        AreaFunction areaFunction=new AreaFunction();
        Beat currBeat;
        try {
            simpleFeatureIterator= (SimpleFeatureIterator) layer.getFeatureSource().getFeatures().features();
            Iterator<Beat> beatScoreIter = scoreResult.iterator();
            while (simpleFeatureIterator.hasNext())
            {
                currBeat = beatScoreIter.next();

                SimpleFeature next=simpleFeatureIterator.next();
                Geometry beatGeometry= (Geometry) next.getDefaultGeometry();
                linefeatures = (SimpleFeatureIterator) roadLayer.getFeatureSource().getFeatures().features();
                DefaultFeatureCollection fcollect=new DefaultFeatureCollection();
                CoordinateReferenceSystem beatCRS = layer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                CoordinateReferenceSystem lineCRS = roadLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                MathTransform transform=null;
                transform = CRS.findMathTransform(lineCRS, beatCRS,true);
                currBeat.setArea(areaFunction.getArea(beatGeometry));
                double lineLength=0;
                while (linefeatures.hasNext())
                {
                    SimpleFeature lineFeature=linefeatures.next();
                    Geometry temp=(Geometry) lineFeature.getDefaultGeometry();
                    Geometry lineGeometry = JTS.transform(temp,transform);

                    if (beatGeometry.intersects(lineGeometry))
                    {
//                        SimpleFeatureType TYPE = DataUtilities.createType("roads", "line", "the_geom:MultiLineString");
//                        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder((SimpleFeatureType) TYPE);
                        Geometry intersection = lineGeometry.intersection(beatGeometry);
 //                       featureBuilder.add(intersection);
  //                      SimpleFeature feature = featureBuilder.buildFeature(next.getID()+"line"+id);
                        lineLength+=intersection.getLength();
  //                      fcollect.add(feature);
                    }
                }
                currBeat.setValue(lineLength);

//                if(!fcollect.isEmpty())
//                { count++;
//                Style st= SLD.createLineStyle(Color.getHSBColor((count*2)%360,(count+10),(count)%100),3);
//                newLayer=new FeatureLayer(fcollect,st,"newLayer"+count);
//
//                        map.layers().add(newLayer);
//
//
//                }
                linefeatures.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (TransformException e) {
            e.printStackTrace();
        } finally
        {
            simpleFeatureIterator.close();
            linefeatures.close();
        }

    }

    public LinkedList<Beat> getScoreResult()
    {
        return scoreResult;
    }

    public void calculateAndStore()
    {
        java.util.List<Layer> layers = map.layers();
        Layer beatLayer = layers.get(layers.indexOf(getLayer(jsonOp.getBEAT_NAME())));
        Layer gridLayer = layers.get(layers.indexOf(getLayer(jsonOp.getGRID_NAME())));
        MathTransform mathTransform = getTransformGeometry(beatLayer, gridLayer);
        try {
            SimpleFeatureIterator it= (SimpleFeatureIterator) beatLayer.getFeatureSource().getFeatures().features();
            while (it.hasNext())
            {
                SimpleFeature next= (SimpleFeature) it.next();
                CoordinateReferenceSystem gridCRS=gridLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                ReferencedEnvelope ref=((ReferencedEnvelope) next.getBounds()).transform(gridCRS,true);
                SimpleFeatureCollection col = grabFeaturesInBoundingBox(ref, getLayer(jsonOp.getGRID_NAME()));
                SimpleFeatureIterator gridIter =col.features();

                ArrayList<ArrayList<String>> listoflist = new ArrayList<>();
                int a = 0;
                Geometry beat = (Geometry) next.getDefaultGeometry();
                beat = JTS.transform(beat,mathTransform);

                while (gridIter.hasNext())
                {
                    SimpleFeature grid = gridIter.next();
                    Geometry gridGeom = (Geometry) grid.getDefaultGeometry();
                    double latitude = gridGeom.getCentroid().getX();
                    double longitude = gridGeom.getCentroid().getY();
                    if(gridGeom.intersects(beat))
                    {
//                        a++;
                        ArrayList<String> temp= (ArrayList<String>) getNeighbours(gridGeom,grid.getID(),gridCRS,jsonOp.getGRID_NAME());
//                        System.out.println(getNeighbours(gridGeom,grid.getID(),gridCRS,jsonOp.getGRID_NAME()).size());
                        if(temp.size()!=9)
                        {
                            while (temp.size()!=9)
                            {
                                temp.add(null);
                            }
                        }

                        temp.add(String.valueOf(latitude));
                        temp.add(String.valueOf(longitude));
                        listoflist.add(temp);
                    }

                }
//                System.out.println(a);
                sqlOp.insertNeighbours((String) next.getAttribute("BEAT_N"),listoflist);
            }
        System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//       Layer beatLayer = layers.get(layers.indexOf());
    }

    public void calculateScore()
    {
        if(sqlOp.isDbEmpty())
        {
            calculateAndStore();
        }
        calculate();

    }

    public void updateTableScore()
    {
        if (scoreResult == null)
            return;

        Layer beat = getLayer(jsonOp.getBEAT_NAME());

        int roadColumn = tableModel.addColumnIfNeeded("Score");
        int layerStartIndex = tableModel.getLayerStartIndex(beat.getTitle());
        int layerEndIndex = tableModel.getLayerEndIndex(beat.getTitle());
        Iterator<Beat>                 iterator = scoreResult.iterator();
        for (int row = layerStartIndex; row < layerEndIndex; row++)
        {
            Beat next = iterator.next();
            tableModel.setValueAt(next.getGlobalScore(), row, roadColumn);
        }
    }

    private MathTransform getTransformGeometry(Layer originLayerCRS, Layer destLayerCRS)
    {
        CoordinateReferenceSystem originCRS = originLayerCRS.getFeatureSource().getSchema().getCoordinateReferenceSystem();
        CoordinateReferenceSystem destCRS = destLayerCRS.getFeatureSource().getSchema().getCoordinateReferenceSystem();
        MathTransform transform= null;
        try {
            transform = CRS.findMathTransform(originCRS, destCRS, true);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        return transform;
    }


    private void registerIntersection(GeomType type, Beat currBeat,
                                        Layer layer,
                                        Geometry lineGeometry,
                                        Geometry beatGeometry)
    {
        double v = 0;
        double score = 0;
        if (beatGeometry.intersects(lineGeometry))
        {
            Geometry intersection = lineGeometry.intersection(beatGeometry);
            switch (type)
            {
                case POLYGON:
                    double area = intersection.getArea();
                    score = area * classification.getScore(layer.getTitle()) / currBeat.getArea();
                    break;
                case LINE:
                    v = intersection.getLength();
                        score = classification.getScore(layer.getTitle());
                    break;
                case POINT:
                    score = classification.getScore(layer.getTitle());
                    if(layer.getTitle().equalsIgnoreCase(Constants.PATROL_CHOWKIS) || layer.getTitle().equalsIgnoreCase(Constants.WIRELESS_CHOWKI))
                        addToPatrol(currBeat,lineGeometry);
                    break;
            }
        }
        currBeat.addValue(v);
        currBeat.addScore(layer.getTitle(), score);
    }

    public SimpleFeatureCollection getFeaturesinCircle(Double latitude,Double longitude,int radius)
    {
        GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
        shapeFactory.setNumPoints(32);
        shapeFactory.setCentre(new Coordinate(latitude,longitude));
        shapeFactory.setSize(radius*1000*2);
        Geometry circle = shapeFactory.createCircle();
        SimpleFeatureCollection col = getCollidingFeature(circle,getLayer(jsonOp.getGRID_NAME()),getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getCoordinateReferenceSystem());
        SimpleFeatureIterator itr = col.features();
        DefaultFeatureCollection circlefeatures = new DefaultFeatureCollection();
       while (itr.hasNext())
       {
           SimpleFeature feature = itr.next();
           Geometry geom = (Geometry) feature.getDefaultGeometry();
           if (circle.contains(geom))
           {
               circlefeatures.add(feature);
           }
       }
       return circlefeatures;
    }

    public SimpleFeatureCollection getCollidingFeature(Geometry beatGeometry, Layer layer,
                                                        CoordinateReferenceSystem ref)
    {

        Envelope envelopeInternal = beatGeometry.getEnvelopeInternal();
        ReferencedEnvelope referencedEnvelope = new ReferencedEnvelope(ref);
        referencedEnvelope.expandToInclude(envelopeInternal);
        try
        {
            return grabFeaturesInBoundingBox(referencedEnvelope, layer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public int TestCompare(double vC,
                           GeomType type,
                           Beat currBeat,
                           Layer layer,
                           Geometry beatGeometry)
    {
        int numberOfFeatures = 0;
        FeatureIterator<?> layerIter = null;
        try
        {
            layerIter = layer.getFeatureSource().getFeatures().features();

            double v = 0;
            while (layerIter.hasNext())
            {

                SimpleFeature lineFeature = (SimpleFeature) layerIter.next();
                Geometry lineGeometry = (Geometry) lineFeature.getDefaultGeometry();
                registerIntersection(type, currBeat, layer, lineGeometry, beatGeometry);
            }

            if (v != vC)
            {
                System.out.println("Difference: " + "            " + v + "  " + vC);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return 0;
        }
        return numberOfFeatures;
    }

    public void initScoreData(Layer addedLayer)
    {
        if(addedLayer==null)
           return;

        if (!addedLayer.getTitle().equals(jsonOp.getGRID_NAME()))
            return;

        scoreResult = new LinkedList<>();
        sortedBeats = new ArrayList<>();

        SimpleFeatureIterator beatIter=null;
        AreaFunction areaFunction = new AreaFunction();

        try {

           CoordinateReferenceSystem gridCRS=addedLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();

            ReferencedEnvelope ref=((ReferencedEnvelope) getRegion()).transform(gridCRS,true);
            SimpleFeatureCollection beatcol= grabFeaturesInBoundingBox(ref,addedLayer);
            beatIter = beatcol.features();
            while (beatIter.hasNext())
            {
                SimpleFeature next = beatIter.next();
                Geometry tempBeatGeometry = (Geometry) next.getDefaultGeometry();

                Beat currBeat = new Beat(next.getIdentifier());
                currBeat.setArea(areaFunction.getArea(tempBeatGeometry));
                currBeat.setGeometry(tempBeatGeometry);
                currBeat.setLongitude(tempBeatGeometry.getCentroid().getCoordinate().x);
                currBeat.setLatitude(tempBeatGeometry.getCentroid().getCoordinate().y);

                scoreResult.add(currBeat);
                sortedBeats.add(currBeat);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            beatIter.close();
            System.out.println(scoreResult.size());
        }

    }

    public void calculate()
    {
        Layer gridLayer = getLayer(jsonOp.getGRID_NAME());

        try {
                Iterator<Layer> iterator = map.layers().iterator();
                FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
                deletedFeatures = sqlOp.getDeletedFeatures();

                while(iterator.hasNext())
                {
                    HashMap<FeatureId,Double> ParentSet = new HashMap<>();
                    Layer next = iterator.next();
                    GeomType geomType = getGeometryType(next);
                    HashMap<String,Double> featureSet = new HashMap<>();
                    String layerType = jsonOp.getType(layerToSourcePath.get(next.getTitle()));
                    if(jsonOp.isCalcRequired(next))
                    {
                        SimpleFeatureCollection features = (SimpleFeatureCollection) next.getFeatureSource().getFeatures();
                        SimpleFeatureIterator featureIterator = features.features();
                        Double jsonscore = Double.valueOf(jsonOp.getIndiScores().get(next.getTitle()));

                        while (featureIterator.hasNext())
                        {
                            Double score = jsonscore;
                            SimpleFeature tempFeature = featureIterator.next();
                            String name = tempFeature.getID();
                            int r = sqlOp.ifFeatureCalcRequired(name,score);
                            if(r == Constants.NOT_REGISTERED || r == Constants.SCORE_CHANGED)
                            {
                                if (r == Constants.SCORE_CHANGED)
                                { score = sqlOp.getNewScore(name,score);
                                }

                                MathTransform transform = getTransformGeometry(next,gridLayer);
                                Geometry currFeatGeom = (Geometry) tempFeature.getDefaultGeometry();
                                Geometry tempGeom = JTS.transform(currFeatGeom,transform);
                                SimpleFeatureCollection grids = getCollidingFeature(tempGeom,gridLayer,gridLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem());
                                ArrayList<String> Ids = new ArrayList<>();
                                FeatureIterator colIter = grids.features();
                                while (colIter.hasNext())
                                {
                                    Ids.add(((SimpleFeature)colIter.next()).getID());
                                }

                                ParentSet.putAll(registerScore(tempGeom,gridLayer,Ids, ff, geomType, score));
                                featureSet.put(name,jsonscore);
                            }
                        }
                        //Update Score
                        sqlOp.updateScore(ParentSet);
                        sqlOp.updateFeatures(featureSet,layerType);
                        sqlOp.removeDeletedFeatures(deletedFeatures);
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformException e) {
            e.printStackTrace();
        }
    }

    public HashMap<FeatureId, Double> registerScore(Geometry geometry, Layer gridLayer, ArrayList<String> Ids, FilterFactory2 ff, GeomType geomType, Double score) throws IOException {
        HashMap<String,GridFeature> gridFeatureMap = sqlOp.findFeatures(Ids);
        HashMap<FeatureId,Double> set = new HashMap<>();
        SimpleFeatureCollection col = (SimpleFeatureCollection) gridLayer.getFeatureSource().getFeatures();
        int count = 0;
        String startPoint = null;
        while(startPoint == null)
        {
            Filter filter = ff.id(ff.featureId(Ids.get(count)));
            SimpleFeatureCollection subcol = col.subCollection(filter);
            if(geometry.intersects((Geometry) subcol.features().next().getDefaultGeometry()))
            {
             startPoint = Ids.get(count);
            }
            count++;
        }

        set=fill(geometry,col,startPoint,gridFeatureMap,set,ff,geomType,score);
        return set;
    }

    public HashMap<FeatureId, Double> fill(Geometry geometry, FeatureCollection col, String currentPos, HashMap<String, GridFeature> list, HashMap<FeatureId, Double> set, FilterFactory2 ff, GeomType geomType, Double score)
    {
        FeatureId current=ff.featureId(currentPos);
        Filter filter = ff.id(current);
        SimpleFeatureCollection subcol = (SimpleFeatureCollection) col.subCollection(filter);
        Geometry grid = (Geometry) subcol.features().next().getDefaultGeometry();
        if(set.containsKey(current) || !(geometry.intersects((grid))))
            return set;

        switch(geomType)
        {
            case POINT:
                set.put(current,score);
            break;
            case LINE:
                set.put(current,score);
            break;
            case POLYGON:
                Geometry intersecton = geometry.intersection(grid);
                Double tempscore = score*intersecton.getArea()/grid.getArea();
                set.put(current,tempscore);
        }

        ArrayList<String> arrayList = list.get(currentPos).getNeighbourList();
        for(String neighbour : arrayList)
        {
            set=fill(geometry,col,neighbour,list,set,ff,geomType,score);
        }
        return set;
    }

    public void calculate(Layer layer)
    {
        GeomType type = getGeometryType(layer);

        Layer beatLayer = getLayer(jsonOp.getGRID_NAME());


        if (beatLayer == null || beatLayer == layer)
            return;

        SimpleFeatureIterator layerIter=null;
        SimpleFeatureIterator beatIter=null;
        AreaFunction areaFunction = new AreaFunction();
        Beat currBeat;

        MathTransform mathTransform = getTransformGeometry(layer, beatLayer);

        CoordinateReferenceSystem layerCRS = layer.getFeatureSource().getSchema()
                                                    .getCoordinateReferenceSystem();

        try {
            Iterator<Beat> beatScoreIter = scoreResult.iterator();
            CoordinateReferenceSystem gridCRS=beatLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
            ReferencedEnvelope ref=((ReferencedEnvelope) getRegion()).transform(gridCRS,true);
            beatIter = grabFeaturesInBoundingBox(ref, getLayer(jsonOp.getGRID_NAME())).features();
            System.out.print(" === "+scoreResult.size()+" === "+grabFeaturesInBoundingBox(ref, getLayer(jsonOp.getGRID_NAME())).size());
            while (beatIter.hasNext())
            {
                currBeat = beatScoreIter.next();

                SimpleFeature next = beatIter.next();
                Geometry beatGeometry = (Geometry) next.getDefaultGeometry();

                MathTransform inverse = mathTransform.inverse();
                Geometry tempBeatGeometry = JTS.transform(beatGeometry, inverse);
                layerIter = getCollidingFeature(tempBeatGeometry, layer, layerCRS).features();

                while (layerIter.hasNext())
                {
                    SimpleFeature lineFeature=layerIter.next();
                    Geometry lineGeometry = (Geometry) lineFeature.getDefaultGeometry();
                    lineGeometry = JTS.transform(lineGeometry, mathTransform);
                    registerIntersection(type, currBeat, layer, lineGeometry, beatGeometry);
                }
                //TestCompare(v, type, currBeat, layer, beatGeometry);
                layerIter.close();
            }
        } catch (IOException | TransformException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally
        {
            beatIter.close();
        }
    }

    private GeomType getGeometryType(Layer layer)
    {
        GeometryDescriptor geomDesc = layer.getFeatureSource().getSchema().getGeometryDescriptor();
        String geometryAttributeName = geomDesc.getLocalName();

        Class<?> clazz = geomDesc.getType().getBinding();
        if (Polygon.class.isAssignableFrom(clazz) ||
                MultiPolygon.class.isAssignableFrom(clazz)) {
            return GeomType.POLYGON;

        } else if (LineString.class.isAssignableFrom(clazz) ||
                MultiLineString.class.isAssignableFrom(clazz)) {

            return GeomType.LINE;

        } else {
            return GeomType.POINT;
        }
    }

    public void calculate_area(Layer layer)
    {
        FeatureLayer             layer1     = (FeatureLayer) layer;
        FeatureSource<?, ?>      featureSource = layer.getFeatureSource();

        try
        {
            FeatureIterator<?>      iterator = featureSource.getFeatures().features();
            while (iterator.hasNext())
            {
                SimpleFeature next = (SimpleFeature) iterator.next();
                Geometry geometry = (Geometry) next.getDefaultGeometry();
                double   area     = geometry.getArea();

              //  System.out.println(area);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void addToPatrol(Beat beat,Geometry point)
    {
           Patrol patrol=new Patrol();
           patrol.setGridLocation(beat);
           patrol.setLongitude(point.getCoordinate().x);
           patrol.setLatitude(point.getCoordinate().y);
            //System.out.println(point.getCoordinate().x+"  "+point.getCoordinate().y);
           patrols.add(patrol);
    }

    public List<String> getNeighbours(Geometry geom,String featureID,CoordinateReferenceSystem crs,String layername)
    {
            Layer layer = getLayer(layername);
            ReferencedEnvelope bbox = new ReferencedEnvelope(crs);
            bbox.expandToInclude(geom.getEnvelopeInternal());
        try {
            SimpleFeatureCollection col=grabFeaturesInBoundingBox(bbox,layer);
            SimpleFeatureIterator itr = col.features();
            List<String> neighbours = new ArrayList<>();
            neighbours.add(featureID);
            while(itr.hasNext())
            {
                SimpleFeature feature = itr.next();
                if(!featureID.equals(feature.getID()))
                {
                    neighbours.add(feature.getID());
                }
            }
//            System.out.println(col.size());

            return neighbours;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void findNeighbours(String layername) {
        try {
            Layer layer = getLayer(layername);
            Iterator<Beat> beatitr = scoreResult.iterator();
            while (beatitr.hasNext()) {
                Beat currbeat = beatitr.next();
                ReferencedEnvelope bbox=new ReferencedEnvelope(layer.getFeatureSource().getSchema().getCoordinateReferenceSystem());
                bbox.expandToInclude(currbeat.getGeometry().getEnvelopeInternal());
                SimpleFeatureCollection col=grabFeaturesInBoundingBox(bbox,layer);
                SimpleFeatureIterator innerBeat = col.features();

//                    System.out.print(currbeat.getId() + "  :-");
                while (innerBeat.hasNext()) {

                    SimpleFeature next=innerBeat.next();
                    int index=-1;
                        for (Beat b : scoreResult) {
                            if ((b.getId().toString().equals(next.getID().toString())) && (!currbeat.getId().toString().equals(b.getId().toString()))) {
                                index = scoreResult.indexOf(b);
                                break;
                            }
                        }
                        if (index >= 0)
                            currbeat.addNeighbour(scoreResult.get(index));
//                            System.out.print(beat.getID() + "  ");
                }
//                    System.out.println("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Sortbyroll implements Comparator<Beat>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Beat a, Beat b)
        {
            return (int)(a.getGlobalScore() - b.getGlobalScore());
        }
    }


    public ArrayList<Integer> regroupPerBlock(ArrayList<Beat> beatList)
    {
        ArrayList<Integer> beatsPerBlock = new ArrayList<>();
        beatsPerBlock.add(0);
        int i;
        for (i=1; i < beatList.size(); i++)
        {
            while (i < beatList.size() && beatList.get(i).getGlobalScore() == beatList.get(i - 1).getGlobalScore())
            {
                i++;
            }
            beatsPerBlock.add(i);
        }

        // If last not added
        if (i <= beatList.size() && beatList.size() != 0)
        {
            // Add in a new block
            beatsPerBlock.add(i);
        }

        return beatsPerBlock;
    }

    public void updateRankAndSort()
    {
        // Sort beats per score
        sortedBeats = new ArrayList<>();
        sortedBeats.addAll(scoreResult);
        sortedBeats.sort(new Sortbyroll());

        // Regroup per block, beat with the same score
        ArrayList<Integer> blocks = regroupPerBlock(sortedBeats);

        int colorChange = (int) Math.ceil(sortedBeats.size() / Constants.NUMBER_RANKS);

        int blockIndex = 1;
        for (int rank = 0; rank < Constants.NUMBER_RANKS - 1; rank++)
        {
            for (int ci=0; blockIndex < blocks.size() && ci <= colorChange &&
                    (ci == 0 || ci + blocks.get(blockIndex) - blocks.get(blockIndex-1) < colorChange); blockIndex++)
            {
                // Add all element with the same score
                for (int beatIndex=blocks.get(blockIndex-1); beatIndex < blocks.get(blockIndex); beatIndex++,
                        ci++)
                {
                    sortedBeats.get(beatIndex).setRank(rank);
                }
            }
        }

        // Add the rest of the elements
        for (;blockIndex < blocks.size(); blockIndex++)
        {
            for (int beatIndex=blocks.get(blockIndex-1); beatIndex < blocks.get(blockIndex); beatIndex++)
            {
                sortedBeats.get(beatIndex).setRank(Constants.NUMBER_RANKS-1);
            }
        }
    }

}

