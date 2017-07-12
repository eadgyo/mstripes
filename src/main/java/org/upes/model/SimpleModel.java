package org.upes.model;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.*;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.upes.MyStyleFactory;
import org.upes.PersonalConstants;

import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
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
    protected MyStyleFactory               myStyleFactory;

    protected StyleFactory   sf = CommonFactoryFinder.getStyleFactory();
    protected FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    public SimpleModel()
    {
        map = new MapContent();
        tableModel = new MyTableModel();
        myStyleFactory=new MyStyleFactory();
    }

    public StyleFactory getSf()
    {
        return sf;
    }

    public FilterFactory2 getFf()
    {
        return ff;
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
        if (getMap().layers().contains(layer))
            getMap().removeLayer(layer);

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

    public void loadFile(File sourceFile) throws IOException
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
            checkLayer(addedLayer);

            // Load the dbf file
            loadDbf(addedLayer);
        }
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

    public Rule createRule(RuleEntry ruleEntry, String geomAttributeName)
    {
        return createRule(ruleEntry.outlineColor, ruleEntry.fillColor, ruleEntry.lineWidth, geomAttributeName);
    }

    public Rule createRule(Color outlineColor, Color fillColor, double lineWidth, String geometryAttributeName)
    {
        Symbolizer                  symbolizer = null;
        Fill                        fill       = null;
        org.geotools.styling.Stroke stroke     = sf.createStroke(ff.literal(outlineColor), ff.literal(lineWidth));

        // Polygon type
        fill = sf.createFill(ff.literal(fillColor), ff.literal(1.0));
        symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    public Style createSelectedStyle(RuleEntry defaultEntry, RuleEntry selectedEntry, Set<FeatureId> IDs, String
            geometryAttributeName)
    {
        Rule selectedRule = createRule(selectedEntry, geometryAttributeName);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(defaultEntry, geometryAttributeName);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Create a default Style for feature display
     * @param geometryAttributeName
     */
    public Style createDefaultStyle(RuleEntry ruleEntry, String geometryAttributeName)
    {
        Rule rule = createRule(ruleEntry, geometryAttributeName);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
}
