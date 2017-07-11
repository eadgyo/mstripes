package org.upes.controller;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.upes.Constants;
import org.upes.model.Model;
import org.upes.model.MyTableModel;
import org.upes.view.MapPanel;
import org.upes.view.View;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Created by eadgyo on 27/06/17.
 */
public class Controller
{
    private View     view;
    private MapPanel mapPanel;
    private Model    model;

    private LoadAction loadAction = new LoadAction();
    private AddAction addAction = new AddAction();
    private OkAction okAction = new OkAction();
    private  DeleteAction deleteAction= new DeleteAction();
    private MulAction mulAction = new MulAction();

    private MyTableListener tableListener = new MyTableListener();

    public Controller(View view, Model model)
    {
        this.view = view;
        this.mapPanel = view.mapPanel;
        this.model = model;

        // Set actions
        mapPanel.loadButton.setAction(loadAction);
        mapPanel.addButton.setAction(addAction);
        view.layerDialog.okButton.setAction(okAction);
        mapPanel.deleteButton.setAction(deleteAction);
        mapPanel.multiplyButton.setAction(mulAction);

        addAction.setEnabled(false);
        deleteAction.setEnabled(false);

        for(int i=0; i< mapPanel.toolBar.getComponentCount();i++)
        {
            if(mapPanel.toolBar.getComponentAtIndex(i).getClass().equals(JButton.class))
                mapPanel.toolBar.getComponentAtIndex(i).setEnabled(false);
        }
        // Link map content
        mapPanel.mapPane.setMapContent(model.getMap());
        view.layerDialog.mapLayerTable.setMapPane(view.mapPanel.mapPane);

        // Link Table
        view.mapPanel.table.setModel(model.getTableModel());

        // Add listener
        mapPanel.mapPane.addMouseListener(new MouseMapListener());
        mapPanel.table.getSelectionModel().addListSelectionListener(tableListener);
    }

    private class AddAction extends AbstractAction {
        public AddAction()
        {
            super(Constants.NAME_ADD_MAP);
            this.putValue(SHORT_DESCRIPTION, Constants.DESC_ADD_MAP);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            JFileChooser chooser=new JFileChooser(model.getInitPath());
            FileFilter filter = new FileNameExtensionFilter("ESRI Shapefile(*.shp)","shp");
            chooser.setFileFilter(filter);
            chooser.showOpenDialog(view);
            File sourceFile=chooser.getSelectedFile();

            if (sourceFile == null)
                return;

            mapPanel.mapPane.setMapContent(null);

            try
            {
                model.loadFile(sourceFile);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(view, Constants.TITLE_NOT_VALID_SHP, Arrays.toString(e.getStackTrace()),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            model.setInitPath(sourceFile.getParent());
            mapPanel.mapPane.setMapContent(model.getMap());
            mapPanel.mapPane.repaint();

        }
    }

    private class MulAction extends AbstractAction {
        public MulAction() {
            super(Constants.NAME_MUL);
            this.putValue(SHORT_DESCRIPTION, Constants.DESC_MUL);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            // Ask for the multiply factor
            String s = JOptionPane.showInputDialog(Constants.QUESTION_MUL, 2);
            if (s == null || s.equals(""))
                return;

            double d = 0;

            // Try to convert string to number
            try
            {
                d = Double.parseDouble(s);
            }
            catch(NumberFormatException nfe)
            {
                return;
            }

            // Get selected rows
            int selectedColumn = mapPanel.table.getSelectedColumn();
            int[] selectedRows = mapPanel.table.getSelectedRows();

            for (int selectedRow : selectedRows)
            {
                model.multiplyColumn(d, selectedRow, selectedColumn);
            }
        }
    }

    private class LoadAction extends AbstractAction {
        public LoadAction() {
            super(Constants.NAME_LOAD_MAP);
            this.putValue(SHORT_DESCRIPTION, Constants.DESC_LOAD_MAP);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            JFileChooser chooser=new JFileChooser(model.getInitPath());
            FileFilter filter = new FileNameExtensionFilter("ESRI Shapefile(*.shp)","shp");
            chooser.setFileFilter(filter);
            chooser.showOpenDialog(view);
            File sourceFile=chooser.getSelectedFile();

            if (sourceFile == null)
                return;

            try
            {
                model.loadFile(sourceFile);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(view, Constants.TITLE_NOT_VALID_SHP, Arrays.toString(e.getStackTrace()),
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }

            model.setInitPath(sourceFile.getParent());
            addAction.setEnabled(true);
            deleteAction.setEnabled(true);
            loadAction.setEnabled(false);
            for(int i=0;i<mapPanel.toolBar.getComponentCount();i++)
            {
                if(mapPanel.toolBar.getComponentAtIndex(i).getClass().equals(JButton.class))
                    mapPanel.toolBar.getComponentAtIndex(i).setEnabled(true);
            }
            mapPanel.mapPane.setMapContent(model.getMap());

            mapPanel.mapPane.repaint();
        }
    }

    private class OkAction extends AbstractAction {
        public OkAction() {
            super(Constants.NAME_OK_DIALOG);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            int layerCount=view.mapPanel.mapPane.getMapContent().layers().size();
            if (layerCount==0)
            {
                loadAction.setEnabled(true);
                addAction.setEnabled(false);
                for(int i=0;i<mapPanel.toolBar.getComponentCount();i++)
                {
                    if(mapPanel.toolBar.getComponentAtIndex(i).getClass().equals(JButton.class))
                        mapPanel.toolBar.getComponentAtIndex(i).setEnabled(false);
                }
                deleteAction.setEnabled(false);
            }
           view.layerDialog.setVisible(false);
        }
    }

    private  class DeleteAction extends AbstractAction
    {
        public DeleteAction() {
            super(Constants.NAME_DEL_MAP);
            this.putValue(SHORT_DESCRIPTION, Constants.DESC_DEL_MAP);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<Layer> oldLayers = new ArrayList<>(model.getMap().layers());

            view.layerDialog.setVisible(true);
            okAction.actionPerformed(e);

            // Get all removed layers
            List<Layer> layers = model.getMap().layers();
            for (Layer oldLayer : oldLayers)
            {
                if (!layers.contains(oldLayer))
                {
                    model.removeLayer(oldLayer.getTitle());
                }
            }
        }
    }

    private class MouseMapListener implements MapMouseListener
    {

        SimpleFeatureCollection grabFeaturesInBoundingBox(MapMouseEvent ev)
                throws Exception {
            FilterFactory2 ff     = CommonFactoryFinder.getFilterFactory2();
            Layer beatLayer = getBeatLayer();
            if (beatLayer==null)
                return null ;

            FeatureSource<?, ?> featureSource = beatLayer.getFeatureSource();
            FeatureType    schema = featureSource.getSchema();

            // usually "THE_GEOM" for shapefiles
            String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
            CoordinateReferenceSystem targetCRS = mapPanel.mapPane.getMapContent().getCoordinateReferenceSystem();

            ReferencedEnvelope bbox = ev.getEnvelopeByPixels(2);

            Filter filter = ff.bbox(ff.property(geometryPropertyName), bbox);
            return (SimpleFeatureCollection) featureSource.getFeatures(filter);
        }


        @Override
        public void onMouseClicked(MapMouseEvent mapMouseEvent)
        {

        }

        @Override
        public void onMouseDragged(MapMouseEvent mapMouseEvent)
        {

        }

        @Override
        public void onMouseEntered(MapMouseEvent mapMouseEvent)
        {

        }

        @Override
        public void onMouseExited(MapMouseEvent mapMouseEvent)
        {

        }

        @Override
        public void onMouseMoved(MapMouseEvent mapMouseEvent)
        {

        }

        @Override
        public void onMousePressed(MapMouseEvent ev)
        {
          if(ev.getSource().getCursorTool()==null)
           {
               try {
                   SimpleFeatureCollection simpleFeatureCollection = grabFeaturesInBoundingBox(ev);
                   if (simpleFeatureCollection==null)
                       return;
                   SimpleFeatureIterator features = simpleFeatureCollection.features();

                   while (features.hasNext()) {
                       SimpleFeature fa = features.next();
                       Geometry geometry = (Geometry) fa.getDefaultGeometry();
                       if (fa.getName().toString().equals("BEAT"))
                       {
                           int col_index = view.mapPanel.table.getColumn("BEAT_N").getModelIndex();
                           int index = -1;
                           for (int i = 0; i < model.getTableModel().getRowCount(); i++) {
                               if (view.mapPanel.table.getValueAt(i, col_index) != null) {
                                   String temp = view.mapPanel.table.getValueAt(i, col_index).toString();
                                   if (temp.equalsIgnoreCase(fa.getAttribute("BEAT_N").toString())) {
                                       index = i;
                                       break;
                                   }
                               }
                           }
                           if (index >= 0)
                               view.mapPanel.table.changeSelection(index, col_index, false, false);
                       }
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
        }

        @Override
        public void onMouseReleased(MapMouseEvent mapMouseEvent)
        {

        }

        @Override
        public void onMouseWheelMoved(MapMouseEvent mapMouseEvent)
        {

        }
    }

    public class MyTableListener implements ListSelectionListener
    {

        @Override
        public void valueChanged(ListSelectionEvent listSelectionEvent)
        {
            updateSelectedLayers();
            mapPanel.mapPane.setMapContent(null);
            mapPanel.mapPane.setMapContent(model.getMap());
            mapPanel.mapPane.repaint();
        }
    }

    /**
     * Quick search using row index
     * @param layer used layer
     * @param rowIndex row relative to start of feature
     * @return row index
     */
    private Feature findFeatureFast(Layer layer, int rowIndex)
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

    private Feature findFeature(Layer layer, String attributeName, String searchAttribute)
    {
        try
        {
            FeatureIterator<?> features = layer.getFeatureSource().getFeatures().features();
            while (features.hasNext())
            {
                SimpleFeature next = (SimpleFeature) features.next();
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

    private Layer getBeatLayer()
    {
        Iterator<Layer> iterator = mapPanel.mapPane.getMapContent()
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

    private void removeLayer(Layer layer)
    {
        if (model.getMap().layers().contains(layer))
            model.getMap().removeLayer(layer);

    }

    private void addLayer(Layer layer)
    {
        if (!model.getMap().layers().contains(layer))
            model.getMap().addLayer(layer);

    }

    private void updateSelectedLayers()
    {
        Layer        beatLayer    = getBeatLayer();
        if (beatLayer == null)
            return;

        MyTableModel tableModel   = (MyTableModel) model.getTableModel();
        int[]        selectedRows = mapPanel.table.getSelectedRows();
        int          column       = tableModel.getColumn("BEAT_N");
        int          startBeat    = tableModel.getLayerStartIndex("BEAT");
        int          endBeat      = tableModel.getLayerEndIndex("BEAT");

        for (int selectedRow : selectedRows)
        {
            Object valueAt = tableModel.getValueAt(selectedRow, column);
            Layer                    layerSelection = model.getLayerSelection();
            DefaultFeatureCollection featureSource  = null;
            featureSource = model.getFeatureCollection();

            try
            {
                // If it's a row from beat layer
                if (selectedRow >= startBeat && selectedRow < endBeat)
                {
                    // Get layer feature
                    SimpleFeature featureFast = (SimpleFeature) findFeatureFast(beatLayer, selectedRow - startBeat);
                    featureFast = (SimpleFeature) findFeature(beatLayer, "BEAT_N", (String) valueAt);

                    // Remove feature
                    featureSource.clear();

                    // Add feature to this layer
                    SimpleFeatureBuilder     featureBuilder = new SimpleFeatureBuilder(featureFast.getFeatureType());
                    featureBuilder.add(featureFast.getDefaultGeometry());

                    int count = featureSource.getCount();
                    featureSource.add(featureBuilder.buildFeature("Selection" + count));
                }

                if (featureSource.getCount() != 0)
                {
                    addLayer(layerSelection);
                }


                //mapPanel.mapPane.getRenderer().in
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }
}
