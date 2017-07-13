package org.upes.controller;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.upes.Constants;
import org.upes.model.Classification;
import org.upes.model.Model;
import org.upes.model.MyTableModel;
import org.upes.model.RuleEntry;
import org.upes.view.MapPanel;
import org.upes.view.View;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * Created by eadgyo on 27/06/17.
 */
public class Controller
{
    private View     view;
    private MapPanel mapPanel;
    private Model    model;

    private LoadAction    loadAction    = new LoadAction();
    private AddAction     addAction     = new AddAction();
    private OkLayerAction okLayerAction = new OkLayerAction();
    private DeleteAction  deleteAction  = new DeleteAction();
    private OkClassificationAction okClassification =new OkClassificationAction();
    private MyTableListener tableListener = new MyTableListener();


    public Controller(View view, Model model)
    {
        this.view = view;
        this.mapPanel = view.mapPanel;
        this.model = model;

        // Set actions
        mapPanel.loadButton.setAction(loadAction);
        mapPanel.addButton.setAction(addAction);
        view.layerDialog.okButton.setAction(okLayerAction);
        mapPanel.deleteButton.setAction(deleteAction);
        view.optionsDialog.ok.setAction(okClassification);

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

        Classification classification = model.getClassification();
        view.mapPanel.classificationView.cooperativeList.setModel(classification.getNeutral());
        view.mapPanel.classificationView.supportiveList.setModel(classification.getSupportive());
        view.mapPanel.classificationView.defectiveList.setModel(classification.getDefective());

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
            view.optionsDialog.setVisible(true);
            int selectedOption = view.optionsDialog.getSelectedOption();
            if (sourceFile == null)
                return;

            Layer layer = null;
            try
            {
                layer = model.loadFile(sourceFile);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(view, Constants.TITLE_NOT_VALID_SHP, Arrays.toString(e.getStackTrace()),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            model.addToClassification(selectedOption, layer);
            model.setInitPath(sourceFile.getParent());
            repaint();

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

            mapPanel.mapPane.repaint();
        }
    }

    private class OkLayerAction extends AbstractAction {
        public OkLayerAction() {
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

    private class OkClassificationAction extends AbstractAction {
        public OkClassificationAction()
        {
            super(Constants.NAME_OK_DIALOG);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            view.optionsDialog.setVisible(false);
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
            List<Layer> oldLayers = new ArrayList<>(getMap().layers());

            view.layerDialog.setVisible(true);
            okLayerAction.actionPerformed(e);

            // Get all removed layers
            List<Layer> layers = getMap().layers();
            for (Layer oldLayer : oldLayers)
            {
                if (!layers.contains(oldLayer))
                {
                    model.removeLayer(oldLayer);
                }
            }
        }
    }

    private class MouseMapListener implements MapMouseListener
    {

        SimpleFeatureCollection grabFeaturesInBoundingBox(MapMouseEvent ev)
                throws Exception
        {
            FilterFactory2 ff     = CommonFactoryFinder.getFilterFactory2();
            Layer beatLayer = model.getLayer("BEAT");
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
                           MyTableModel tableModel = (MyTableModel) model.getTableModel();
                           int index = -1;

                           int startIndex = tableModel.getLayerStartIndex("BEAT");
                           int endIndex       = tableModel.getLayerEndIndex("BEAT");

                           for (int row = startIndex; row < endIndex; row++)
                           {
                               if (tableModel.getValueAt(row, col_index) != null)
                               {
                                   String temp = tableModel.getValueAt(row, col_index).toString();
                                   if (temp.equalsIgnoreCase(fa.getAttribute("BEAT_N").toString()))
                                   {
                                       index = row;
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
               repaint();
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
            if (!listSelectionEvent.getValueIsAdjusting())
            {
                updateSelectedLayers();
            }
        }
    }



    public void updateSelectedLayers()
    {
        Layer        beatLayer    = model.getLayer("BEAT");
        if (beatLayer == null)
            return;

        MyTableModel tableModel   = (MyTableModel) model.getTableModel();
        int[]        selectedRows = mapPanel.table.getSelectedRows();
        int          column       = tableModel.getColumn("BEAT_N");
        int          startBeat    = tableModel.getLayerStartIndex("BEAT");
        int          endBeat      = tableModel.getLayerEndIndex("BEAT");

        for (int selectedRow : selectedRows)
        {
            Object         valueAt          = tableModel.getValueAt(selectedRow, column);
            Set<FeatureId> selectedFeatures = new HashSet<>();

            // If it's a row from beat layer
            if (selectedRow >= startBeat && selectedRow < endBeat)
            {
                // Get layer feature
                SimpleFeature featureFast = (SimpleFeature) model.findFeature(beatLayer, "BEAT_N", (String) valueAt);

                // Change color of this feature
                selectedFeatures.add(featureFast.getIdentifier());
            }
            String geometryAttributeName = beatLayer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
            displaySelectedFeatures(beatLayer, selectedFeatures, geometryAttributeName);
        }
    }

    public void displaySelectedFeatures(Layer layer, Set<FeatureId> IDs, String geometryAttributeName)
    {
        Style style;

        RuleEntry defaultEntry = new RuleEntry(Color.BLACK, Color.LIGHT_GRAY, 1.0);
        RuleEntry selectedEntry = new RuleEntry(Color.BLACK, Color.RED, 1.2);

        if (IDs.isEmpty())
            style = model.createDefaultStyle(defaultEntry, geometryAttributeName);
        else
            style = model.createSelectedStyle(defaultEntry, selectedEntry, IDs, geometryAttributeName);

        ((FeatureLayer) layer).setStyle(style);
        repaint();
    }

    public void repaint()
    {
        mapPanel.mapPane.repaint();
    }

    public MapContent getMap()
    {
        return mapPanel.mapPane.getMapContent();
    }
}
