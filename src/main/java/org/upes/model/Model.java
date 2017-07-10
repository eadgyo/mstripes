package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.AreaFunction;
import org.geotools.geometry.jts.JTS;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.upes.MyStyleFactory;

import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by eadgyo on 27/06/17.
 */
public class Model
{
    private File                sourceFile;
    private SimpleFeatureSource featureSource;
    private MapContent          map;
    private MyTableModel        tableModel;
    private String initPath= "F:/intern/Basemaps";
    private MyStyleFactory               myStyleFactory;
    private AbstractGridCoverage2DReader reader;
    private StyleFactory sf = new StyleFactoryImpl();

    LinkedList<Beat> beats=new LinkedList<Beat>();

    public Model()
    {
        map = new MapContent();
        tableModel = new MyTableModel();
        myStyleFactory=new MyStyleFactory();
    }
    public void loadFile(File sourceFile) throws IOException
    {
        this.sourceFile = sourceFile;

        // Create the factory
        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
        featureSource = store.getFeatureSource();

        // Add the first layer to map
        loadMap();

        // Load the dbf file
        loadDbf();

    }

    public String getInitPath()
    {
        return initPath;
    }

    public void setInitPath(String path)
    {
        if(!path.isEmpty())
        initPath=path;
    }

    public void loadMap() {

        Style style=myStyleFactory.setStyle(featureSource.getSchema());

        SimpleFeatureIterator features = null;
        try {
            features = featureSource.getFeatures().features();
            DefaultFeatureCollection featureColl = new DefaultFeatureCollection();
            SimpleFeature next = null;
            while (features.hasNext()) {
                next = features.next();
                Geometry geometry = (Geometry) next.getDefaultGeometry();
                if (geometry != null && geometry.isValid()) {

                    featureColl.add(next);
                }
            }
            Layer layer = new FeatureLayer(featureColl, style, next.getName().toString());
            map.layers().add(layer);

            if (featureSource.getSchema().getTypeName().equalsIgnoreCase("forst_road_core"))
            {calculate_road_length(featureSource);}

            Iterator<Beat> beatIterator=beats.iterator();
            while (beatIterator.hasNext())
            {
                Beat temp=beatIterator.next();
                System.out.println("id: "+temp.getId()+" area: "+temp.getArea()+" road: "+temp.getRoadLength());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            features.close();
        }
    }

    public void loadDbf() throws IOException
    {
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = featureSource.getFeatures();
        try (FeatureIterator<SimpleFeature> features = collection.features()) {

            while (features.hasNext())
            {
                SimpleFeature feature = features.next();
                Vector vector = new Vector<>();
                for (Property attribute : feature.getProperties())
                {
                    if (isValidInfo(attribute.getType()))
                    {
                        int columnRow = tableModel.getColumn(attribute.getName().toString());
                        while (columnRow >= vector.size())
                        {
                            vector.add("");
                        }
                        vector.set(columnRow, attribute.getValue());
                    }
                }
                tableModel.addRow(feature.getName().toString(), vector);
            }
        }
    }

    public boolean isValidInfo(PropertyType propertyType)
    {
        Class<?> binding = propertyType.getBinding();
        if (propertyType.getName().equals("the_geom"))
            return false;
        return true;
    }

    public MapContent getMap()
    {
        return map;
    }

    public TableModel getTableModel()
    {
        return tableModel;
    }

    public void removeLayer(String layerName)
    {
        tableModel.removeLayer(layerName);
    }

    // method for calculating area
    public void calculate_area(SimpleFeatureSource sf)
    {
        SimpleFeatureIterator features = null;

        try {
            features=sf.getFeatures().features();
            AreaFunction areaFunction=new AreaFunction();
            SimpleFeature next = null;

            while(features.hasNext())
            {
                next=features.next();
                Geometry geometry=(Geometry) next.getDefaultGeometry();
           //     System.out.println(areaFunction.getArea(geometry));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            features.close();
        }
    }

    public void calculate_road_length(SimpleFeatureSource sf)
    {

        ListIterator it=map.layers().listIterator();
        int flag=0;
        Layer beatLayer=null;
        while (it.hasNext()) {
            beatLayer = (Layer) it.next();
            if (beatLayer.getTitle().equalsIgnoreCase("BEAT")) {
                flag = 1;
                break;
            }
        }
            if(flag==1)
            {
                SimpleFeatureIterator linefeatures=null;
                SimpleFeatureIterator simpleFeatureIterator=null;
                Layer newLayer=null;
                AreaFunction areaFunction=new AreaFunction();
                Beat currBeat=null;
                Iterator<Beat> beatIterator=null;
                try {
                    simpleFeatureIterator= (SimpleFeatureIterator) beatLayer.getFeatureSource().getFeatures().features();
                    int count=0;
                    if(!beats.isEmpty())
                    {
                        beatIterator=beats.iterator();
                    }
                    while (simpleFeatureIterator.hasNext())
                    {
                        SimpleFeature next=simpleFeatureIterator.next();
                        Geometry beatGeometry= (Geometry) next.getDefaultGeometry();
                        linefeatures=sf.getFeatures().features();
                        DefaultFeatureCollection fcollect=new DefaultFeatureCollection();
                        CoordinateReferenceSystem beatCRS = beatLayer.getFeatureSource().getSchema().getCoordinateReferenceSystem();
                        CoordinateReferenceSystem lineCRS = sf.getSchema().getCoordinateReferenceSystem();
                        MathTransform transform=null;

                        transform= CRS.findMathTransform(lineCRS,beatCRS,true);

                        if(beats.size()!=beatLayer.getFeatureSource().getFeatures().size()) {
                            currBeat = new Beat(next.getID());
                            currBeat.setArea(areaFunction.getArea(beatGeometry));
                        }
                        else
                        {currBeat=beatIterator.next();}

                        int id=0;
                        double lineLength=0;
                        while (linefeatures.hasNext())
                        {
                            SimpleFeature lineFeature=linefeatures.next();
                            Geometry temp=(Geometry) lineFeature.getDefaultGeometry();
                            Geometry lineGeometry = JTS.transform(temp,transform);

                            if (beatGeometry.intersects(lineGeometry))
                            {
                                SimpleFeatureType TYPE = DataUtilities.createType(lineFeature.getName().toString(), lineFeature.getName().toString(), "the_geom:"+lineGeometry.getGeometryType());
                                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder((SimpleFeatureType) TYPE);
                                featureBuilder.add(lineGeometry.intersection(beatGeometry));
                                SimpleFeature feature = featureBuilder.buildFeature(next.getID()+lineFeature.getName().toString()+id);
                                id++;
                                lineLength+=lineGeometry.getLength();
                                fcollect.add(feature);
                            }
                        }
                        currBeat.setRoadLength(lineLength);

                        if(!fcollect.isEmpty())
                        { count++;
                        Style st= SLD.createLineStyle(Color.getHSBColor((count*2)%360,(count+10),(count)%100),3);
                        newLayer=new FeatureLayer(fcollect,st,"newLayer"+count);

                                map.layers().add(newLayer);


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
                } finally {
                    simpleFeatureIterator.close();
                    linefeatures.close();
                }

        }

    }

    public void computeArea(Layer layer)
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
