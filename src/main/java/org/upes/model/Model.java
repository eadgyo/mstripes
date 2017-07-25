package org.upes.model;

import com.vividsolutions.jts.geom.*;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
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
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.upes.Constants;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;


/**
 * Created by eadgyo on 27/06/17.
 */
public class Model extends SimpleModel
{


    private enum GeomType {
        POLYGON,
        LINE,
        POINT
    }

    @Override
    public void checkLayer(Layer addedLayer)
    {
        checkLayerRoad(addedLayer);
        updateRoadLength(addedLayer, null);
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
                    if (feature.getAttribute("STATUS") != null)
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

        LinkedList<Beat> beats = calculate_road_length(layerRoad, addedLayer);

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
                Object        statusValue = next.getAttribute("STATUS");
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

    public LinkedList<Beat> calculate_road_length(Layer roadLayer, Layer layer)
    {
        LinkedList<Beat> beats=new LinkedList<Beat>();
        SimpleFeatureIterator linefeatures=null;
        SimpleFeatureIterator simpleFeatureIterator=null;
        Layer newLayer=null;
        AreaFunction areaFunction=new AreaFunction();
        Beat currBeat;
        try {
            simpleFeatureIterator= (SimpleFeatureIterator) layer.getFeatureSource().getFeatures().features();
            int count=0;
            while (simpleFeatureIterator.hasNext())
            {
                SimpleFeature next=simpleFeatureIterator.next();
                Geometry beatGeometry= (Geometry) next.getDefaultGeometry();
                linefeatures = (SimpleFeatureIterator) roadLayer.getFeatureSource().getFeatures().features();
                DefaultFeatureCollection fcollect=new DefaultFeatureCollection();
                CoordinateReferenceSystem beatCRS = layer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                CoordinateReferenceSystem lineCRS = roadLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                MathTransform transform=null;
                currBeat=new Beat(next.getID());
                transform= CRS.findMathTransform(lineCRS,beatCRS,true);
                currBeat.setArea(areaFunction.getArea(beatGeometry));
                int id=0;
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
                        id++;
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
                beats.add(currBeat);
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

        return beats;
    }

    public void calculateForAll()
    {
        Iterator<Layer> iterator = map.layers().iterator();
        while (iterator.hasNext())
        {
            Layer next = iterator.next();

            System.out.println();
            if(classification.getDefective().contains(next.getFeatureSource().getName().toString()) ||
                    classification.getNeutral().contains(next.getFeatureSource().getName().toString()) ||
                       classification.getSupportive().contains(next.getFeatureSource().getName().toString()))
            {
                LinkedList<Beat> calculate = calculate(next);
                System.out.println(next.getTitle());
//                CalculateCostFactor(calculate,next);
                for (Beat beat : calculate) {
                    System.out.println("For ID" + beat.getId() + "  Score --> " + beat.getScore());
                }
            }
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
                    v = area;
                    break;
                case LINE:
                    v = intersection.getLength();
                    break;
                case POINT:
                    v = 1;
                    score = classification.getScore(layer.getTitle());
                    break;
            }
        }
        currBeat.addValue(v);
        currBeat.addScore(layer.getTitle(), score);
    }

    private SimpleFeatureCollection getCollidingFeature(Geometry beatGeometry, Layer layer,
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

    public LinkedList<Beat> calculate(Layer layer)
    {
        GeomType type = getGeometryType(layer);

        Layer beatLayer = getLayer("BEAT");
        LinkedList<Beat> beats=new LinkedList<Beat>();

        if (beatLayer == null || beatLayer == layer)
            return beats;

        SimpleFeatureIterator layerIter=null;
        SimpleFeatureIterator beatIter=null;
        AreaFunction areaFunction = new AreaFunction();
        Beat currBeat;

        MathTransform mathTransform = getTransformGeometry(beatLayer, layer);

        CoordinateReferenceSystem layerCRS = layer.getFeatureSource().getSchema()
                                                    .getCoordinateReferenceSystem();

        try {
            beatIter= (SimpleFeatureIterator) beatLayer.getFeatureSource().getFeatures().features();
            while (beatIter.hasNext())
            {
                SimpleFeature next = beatIter.next();
                Geometry tempBeatGeometry = (Geometry) next.getDefaultGeometry();
                Geometry beatGeometry = JTS.transform(tempBeatGeometry, mathTransform);

                layerIter = getCollidingFeature(beatGeometry, layer, layerCRS).features();
                currBeat = new Beat(next.getID());
                currBeat.setArea(areaFunction.getArea(beatGeometry));

                double v = 0;
                while (layerIter.hasNext())
                {
                    SimpleFeature lineFeature=layerIter.next();
                    Geometry lineGeometry = (Geometry) lineFeature.getDefaultGeometry();

                    registerIntersection(type, currBeat, layer, lineGeometry, beatGeometry);
                }
                //TestCompare(v, type, currBeat, layer, beatGeometry);

                layerIter.close();
                beats.add(currBeat);
            }
        } catch (IOException | TransformException e) {
            e.printStackTrace();
        }
        finally
        {
            beatIter.close();
        }

        return beats;
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


}

