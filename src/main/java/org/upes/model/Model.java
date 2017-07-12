package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.AreaFunction;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.upes.Constants;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Created by eadgyo on 27/06/17.
 */
public class Model extends SimpleModel
{

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
                double status = (double) next.getAttribute("STATUS");
                double result = status * temp.getRoadLength();
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
                        SimpleFeatureType TYPE = DataUtilities.createType("roads", "line", "the_geom:MultiLineString");
                        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder((SimpleFeatureType) TYPE);
                        Geometry intersection = lineGeometry.intersection(beatGeometry);
                        featureBuilder.add(intersection);
                        SimpleFeature feature = featureBuilder.buildFeature(next.getID()+"line"+id);
                        id++;
                        lineLength+=intersection.getLength();
                        fcollect.add(feature);
                    }
                }
                currBeat.setRoadLength(lineLength);

                if(!fcollect.isEmpty())
                { count++;
                Style st= SLD.createLineStyle(Color.getHSBColor((count*2)%360,(count+10),(count)%100),3);
                newLayer=new FeatureLayer(fcollect,st,"newLayer"+count);

                        map.addLayer(newLayer);


                }
                linefeatures.close();
                beats.add(currBeat);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FactoryException e) {
            e.printStackTrace();
        } catch (TransformException e) {
            e.printStackTrace();
        } catch (SchemaException e) {
            e.printStackTrace();
        } finally
        {
            simpleFeatureIterator.close();
            linefeatures.close();
        }

        return beats;
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

