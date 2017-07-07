package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.AreaFunction;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryImpl;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyType;
import org.upes.MyStyleFactory;

import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Created by eadgyo on 27/06/17.
 */
public class Model
{
    private File                sourceFile;
    private SimpleFeatureSource featureSource;
    private MapContent          map;
    private MyTableModel        tableModel;
    private String initPath="/";
    private MyStyleFactory               myStyleFactory;
    private AbstractGridCoverage2DReader reader;
    private StyleFactory sf = new StyleFactoryImpl();

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

            //Calculating area just for BEAT file
            if (featureSource.getSchema().getTypeName().equalsIgnoreCase("BEAT"))
            {calculate_area(featureSource);}

            if (featureSource.getSchema().getTypeName().equalsIgnoreCase("forst_road_core"))
            {calculate_road_length(featureSource);}

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
                System.out.println(areaFunction.getArea(geometry));
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
                try {
                    simpleFeatureIterator= (SimpleFeatureIterator) beatLayer.getFeatureSource().getFeatures().features();
                    DefaultFeatureCollection fcollect=new DefaultFeatureCollection();

                    while (simpleFeatureIterator.hasNext())
                    {
                        SimpleFeature next=simpleFeatureIterator.next();
                        Geometry beatGeometry= (Geometry) next.getDefaultGeometry();
                        linefeatures=sf.getFeatures().features();

                        while (linefeatures.hasNext())
                        {
                            SimpleFeature lineFeature=linefeatures.next();
                            Geometry lineGeometry=(Geometry) lineFeature.getDefaultGeometry();

                            if (beatGeometry.intersects(lineGeometry))
                            {
                                System.out.println("here");
                                Style st= SLD.createLineStyle(Color.ORANGE,3);
                                SimpleFeature f= (SimpleFeature) beatGeometry.intersection(lineGeometry);
                                fcollect.add(f);
                                Layer newLayer=new FeatureLayer(fcollect,st,"new layer");
                                map.layers().add(newLayer);
                                return;
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
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

                System.out.println(area);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
