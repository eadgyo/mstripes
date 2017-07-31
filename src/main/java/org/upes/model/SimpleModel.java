package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.BoundingBox;
import org.upes.Constants;
import org.upes.MyStyleFactory;
import org.upes.PersonalConstants;

import javax.swing.table.TableModel;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by eadgyo on 12/07/17.
 */
public class SimpleModel
{
    protected File                sourceFile;
    protected SimpleFeatureSource featureSource;
    protected MapContent          map;
    protected MyTableModel        tableModel;

    protected String initPath = PersonalConstants.INIT_PATH;


    protected Classification classification= new Classification();
    protected MyStyleFactory               myStyleFactory;


    protected HashMap<String, String> layerToSourcePath = new HashMap<>();

    public SimpleModel()
    {
        map = new MapContent();
        tableModel = new MyTableModel();
        myStyleFactory=new MyStyleFactory();
    }

    public Classification getClassification()
    {
        return classification;
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

    public void addLayer(Layer layer)
    {
        if (!getMap().layers().contains(layer))
            getMap().addLayer(layer);

    }

    public void removeLayer(Layer layer)
    {
        if (!getMap().layers().contains(layer))
        {
            getMap().removeLayer(layer);
            tableModel.removeLayer(layer.getTitle());
            if (classification.getSupportive().contains(layer.getTitle()))
            {
                classification.getSupportive().removeElement(layer.getTitle());
            }
            else if(classification.getNeutral().contains(layer.getTitle()))
            {
                classification.getNeutral().removeElement(layer.getTitle());
            }
            else if(classification.getDefective().contains(layer.getTitle()))
            {
                classification.getDefective().removeElement(layer.getTitle());
            }
            layerToSourcePath.remove(layer.getTitle());
        }
    }

    public Layer getLayer(String layerName)
    {
        Iterator<Layer> iterator = getMap()
                .layers()
                .iterator();
        while (iterator.hasNext())
        {
            Layer next = iterator.next();
            if (next.getTitle().equals("BEAT"))
                return next;
        }

        return null;
    }

    public MapContent getMap()
    {
        return map;
    }

    public TableModel getTableModel()
    {
        return tableModel;
    }

    public Layer loadFile(File sourceFile) throws IOException
    {
        this.sourceFile = sourceFile;

        // Create the factory
        FileDataStore store = FileDataStoreFinder.getDataStore(sourceFile);
        featureSource = store.getFeatureSource();

        // Add the first layer to map
        Layer addedLayer = loadMap();

        // Compute road length if available
        if (addedLayer != null)
        {
            // Load the dbf file
            loadDbf(addedLayer);

            checkLayer(addedLayer);
        }

        layerToSourcePath.put(addedLayer.getTitle(), sourceFile.getPath());

        return addedLayer;
    }

    public void checkLayer(Layer addedLayer)
    {}

    public Layer loadMap() {

        Style style =myStyleFactory.setStyle(featureSource.getSchema());

        SimpleFeatureIterator features = null;
        try {
            features = featureSource.getFeatures().features();
            DefaultFeatureCollection featureColl = new DefaultFeatureCollection();
            SimpleFeature            next        = null;
            while (features.hasNext()) {
                next = features.next();
                Geometry geometry = (Geometry) next.getDefaultGeometry();
                if (geometry != null && geometry.isValid()) {

                    featureColl.add(next);
                }
            }
            Layer layer = new FeatureLayer(featureColl, style, next.getName().toString());
            map.layers().add(layer);

            return layer;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            features.close();
        }
        return null;
    }

    public void loadDbf(Layer addedLayer) throws IOException
    {
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = (FeatureCollection<SimpleFeatureType,
                SimpleFeature>) addedLayer.getFeatureSource().getFeatures();
        try (FeatureIterator<SimpleFeature> features = collection.features()) {

            while (features.hasNext())
            {
                SimpleFeature feature = features.next();
                Vector        vector  = new Vector<>();
                for (Property attribute : feature.getProperties())
                {
                    int columnRow = tableModel.addColumnIfNeeded(attribute.getName().toString());
                    while (columnRow >= vector.size())
                    {
                        vector.add("");
                    }
                    vector.set(columnRow, attribute.getValue());
                }
                tableModel.addRow(feature.getName().toString(), vector);
            }
        }
    }

    /**
     * Quick search using row index
     * @param layer used layer
     * @param rowIndex row relative to start of feature
     * @return row index
     */
    public Feature findFeatureFast(Layer layer, int rowIndex)
    {
        FeatureIterator<?> features = null;
        try
        {
            features = layer.getFeatureSource().getFeatures().features();
            for (int i = 0; i < rowIndex - 1; i++)
            {
                features.next();
            }
            return features.next();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Feature findFeature(Layer layer, String attributeName, String searchAttribute)
    {
        try
        {
            FeatureIterator<?> features = layer.getFeatureSource().getFeatures().features();
            while (features.hasNext())
            {
                SimpleFeature next      = (SimpleFeature) features.next();
                Object        attribute = next.getAttribute(attributeName);

                if (attribute != null && attribute.equals(searchAttribute))
                    return next;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void addToClassification(int selectedOption, Layer layer)
    {
        if (selectedOption == Constants.SUPPORTIVE)
        {
            classification.addSupportive(layer);
        }
        else if (selectedOption == Constants.COOPERATIVE)
        {
            classification.addCooperative(layer);
        }
        else if (selectedOption==Constants.DEFECTIVE)
        {
            classification.addDefective(layer);
        }
    }

    public SimpleFeatureCollection grabFeaturesInBoundingBox(BoundingBox bbox, Layer layer)
            throws Exception
    {
        if (layer==null)
            return null ;

        FilterFactory2 ff     = CommonFactoryFinder.getFilterFactory2();

        FeatureSource<?, ?> featureSource = layer.getFeatureSource();
        FeatureType         schema        = featureSource.getSchema();

        // usually "THE_GEOM" for shapefiles
        String                    geometryPropertyName = schema.getGeometryDescriptor().getLocalName();

        Filter filter = ff.bbox(ff.property(geometryPropertyName), bbox);
        return (SimpleFeatureCollection) featureSource.getFeatures(filter);
    }

    public Collection<String> getSourceLayers()
    {
        return layerToSourcePath.values();
    }
}
