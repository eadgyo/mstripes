package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.PropertyType;
import org.upes.view.View;

import javax.swing.table.TableModel;
import java.io.File;
import java.io.IOException;
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

    public Model()
    {
        map = new MapContent();
        tableModel = new MyTableModel();
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
        Style style = SLD.createSimpleStyle(featureSource.getSchema());
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
                tableModel.addRow(vector);
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
}
