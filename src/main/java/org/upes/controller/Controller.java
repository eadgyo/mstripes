package org.upes.controller;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;
import org.upes.Constants;
import org.upes.algo.Dijkstra;
import org.upes.model.*;
import org.upes.utils.MapServer;
import org.upes.utils.StyleUtils;
import org.upes.utils.ZipMem;
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
    private View         view;
    private MapPanel     mapPanel;
    private ComputeModel computeModel;

    private LoadAction    loadAction    = new LoadAction();
    private AddAction     addAction     = new AddAction();
    private OkLayerAction okLayerAction = new OkLayerAction();
    private DeleteAction  deleteAction  = new DeleteAction();
    private OkClassificationAction okClassification =new OkClassificationAction();
    private MyTableListener tableListener = new MyTableListener();
    private CalcAction calcAction = new CalcAction();
    private PathAction pathAction = new PathAction();

    protected MapServer mapServer;

    public Controller(View view, ComputeModel computeModel)
    {
        this.view = view;
        this.mapPanel = view.mapPanel;
        this.computeModel = computeModel;

        // Set actions
        mapPanel.loadButton.setAction(loadAction);
        mapPanel.addButton.setAction(addAction);
        view.layerDialog.okButton.setAction(okLayerAction);
        mapPanel.deleteButton.setAction(deleteAction);
        view.optionsDialog.ok.setAction(okClassification);
        mapPanel.calculateButton.setAction(calcAction);
        mapPanel.pathButton.setAction(pathAction);

        addAction.setEnabled(false);
        deleteAction.setEnabled(false);

        for(int i=0; i< mapPanel.toolBar.getComponentCount();i++)
        {
            if(mapPanel.toolBar.getComponentAtIndex(i).getClass().equals(JButton.class))
                mapPanel.toolBar.getComponentAtIndex(i).setEnabled(false);
        }
        // Link map content
        mapPanel.mapPane.setMapContent(computeModel.getMap());
        view.layerDialog.mapLayerTable.setMapPane(view.mapPanel.mapPane);

        // Link Table
        view.mapPanel.table.setModel(computeModel.getTableModel());

        Classification classification = computeModel.getClassification();
        view.mapPanel.classificationView.neutralList.setModel(classification.getNeutral());
        view.mapPanel.classificationView.supportiveList.setModel(classification.getSupportive());
        view.mapPanel.classificationView.defectiveList.setModel(classification.getDefective());

        // Add listener
        mapPanel.mapPane.addMouseListener(new MouseMapListener());
        mapPanel.table.getSelectionModel().addListSelectionListener(tableListener);

        try
        {
            mapServer = new MapServer(Constants.SERVER_PORT);
            mapServer.addContext("/", new ShapeFileHandler());
            mapServer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
            JFileChooser chooser=new JFileChooser(computeModel.getInitPath());
            FileFilter filter = new FileNameExtensionFilter("ESRI Shapefile(*.shp)","shp");
            chooser.setFileFilter(filter);
            int result=chooser.showOpenDialog(view);

            File sourceFile=chooser.getSelectedFile();

            if (sourceFile == null)
                return;

            if (result==JFileChooser.CANCEL_OPTION)
                return;

            view.optionsDialog.setVisible(true);
            int selectedOption = view.optionsDialog.getSelectedOption();

            Layer layer = null;
            try
            {
                layer = computeModel.loadFile(sourceFile);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(view, Constants.TITLE_NOT_VALID_SHP, Arrays.toString(e.getStackTrace()),
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            computeModel.addToClassification(selectedOption, layer);
            computeModel.setInitPath(sourceFile.getParent());
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
            JFileChooser chooser=new JFileChooser(computeModel.getInitPath());
            FileFilter filter = new FileNameExtensionFilter("ESRI Shapefile(*.shp)","shp");
            chooser.setFileFilter(filter);
            int resullt=chooser.showOpenDialog(view);
            File sourceFile=chooser.getSelectedFile();

            if (sourceFile == null)
                return;

            if (resullt==JFileChooser.CANCEL_OPTION)
                return;

            try
            {
                computeModel.loadFile(sourceFile);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(view, Constants.TITLE_NOT_VALID_SHP, Arrays.toString(e.getStackTrace()),
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }

            computeModel.setInitPath(sourceFile.getParent());
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
                    computeModel.removeLayer(oldLayer);
                }
            }
        }
    }

    private class CalcAction extends AbstractAction
    {
        public CalcAction(){
         super(Constants.NAME_CALC_INTERSECT);
         this.putValue(SHORT_DESCRIPTION,Constants.DESC_CALC_INTERSECT);
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            computeModel.calculateScore();
            displayCriticalColor();
        }
    }

    private class MouseMapListener implements MapMouseListener
    {

        SimpleFeatureCollection grabFeaturesInMouseBox(MapMouseEvent ev) throws Exception
        {
            ReferencedEnvelope bbox = ev.getEnvelopeByPixels(2);
            SimpleFeatureCollection coll;
            return computeModel.grabFeaturesInBoundingBox(bbox, computeModel.getLayer("BEAT"));
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
                   SimpleFeatureCollection simpleFeatureCollection = grabFeaturesInMouseBox(ev);
                   if (simpleFeatureCollection==null)
                       return;
                   SimpleFeatureIterator features = simpleFeatureCollection.features();

                   while (features.hasNext()) {
                       SimpleFeature fa = features.next();
                       Geometry geometry = (Geometry) fa.getDefaultGeometry();
                       Point         centroid = geometry.getCentroid();
                       System.out.println(centroid);
                       if (fa.getName().toString().equals("BEAT"))
                       {
                           int col_index = view.mapPanel.table.getColumn("BEAT_N").getModelIndex();
                           MyTableModel tableModel = (MyTableModel) computeModel.getTableModel();
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
        Layer        beatLayer    = computeModel.getLayer("BEAT");
        if (beatLayer == null)
            return;

        MyTableModel tableModel   = (MyTableModel) computeModel.getTableModel();
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
                SimpleFeature featureFast = (SimpleFeature) computeModel.findFeature(beatLayer, "BEAT_N", (String) valueAt);

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
            style = StyleUtils.createDefaultStyle(defaultEntry, geometryAttributeName);
        else
            style = StyleUtils.createSelectedStyle(defaultEntry, selectedEntry, IDs, geometryAttributeName);

        ((FeatureLayer) layer).setStyle(style);
        repaint();
    }

    public void displayCriticalColor()
    {
        Layer        beatLayer    = computeModel.getLayer("BEAT");
        if (beatLayer == null)
            return;
        String geometryAttributeName = beatLayer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();

        Style style = StyleUtils.createStyleFromCritical(computeModel.getSortedBeats(), geometryAttributeName);

        ((FeatureLayer) beatLayer).setStyle(style);
        repaint();
    }

    public void createFile(String fileName, String type) throws IOException, SchemaException
    {
        FileDataStoreFactorySpi factory = new ShapefileDataStoreFactory();

        File file = new File(fileName);
        Map map = Collections.singletonMap( "url", file.toURI().toURL() );

        DataStore myData = factory.createNewDataStore(map);
        SimpleFeatureType featureType =
                DataUtilities.createType("my", "geom:" + type + ",name:String,age:Integer,description:String");
        myData.createSchema( featureType );
    }

    private class ShapeFileHandler implements com.sun.net.httpserver.HttpHandler
    {
        @Override
        public void handle(com.sun.net.httpserver.HttpExchange httpExchange) throws IOException
        {
            Collection<String> sourceLayers = computeModel.getSourceLayers();
            ZipMem             zipMem       = new ZipMem();
            for (String sourceLayer : sourceLayers)
            {
                zipMem.addAllFilesSameName("", sourceLayer);
            }
            zipMem.close();

            System.out.println("Request file " + " shapes.zip");
            MapServer.sendObject(zipMem.toByteArray(), "shapes.zip", httpExchange);
        }
    }

    public void repaint()
    {
        mapPanel.mapPane.repaint();
    }

    public MapContent getMap()
    {
        return mapPanel.mapPane.getMapContent();
    }

    private class PathAction extends AbstractAction
    {

        public PathAction()
        {
            super(Constants.NAME_PATH);
            this.putValue(SHORT_DESCRIPTION, Constants.DESC_PATH);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
//            String latitudeText = mapPanel.latitude.getText();
//            String longitudeText = mapPanel.longitude.getText();
            Patrol firstPatrol = computeModel.getPatrol(0);
            Beat   startLoc = firstPatrol.getGridLocation();

            Dijkstra dijkstra = new Dijkstra(1000);

            String value = JOptionPane.showInputDialog("Please input mark for test 1: ");
            double dist     = Double.parseDouble(value);
            List<Beat> beats  = dijkstra.pathFinding(computeModel.getSortedBeats(), startLoc, dist);


            HashSet<FeatureId> selectedFeatures = new HashSet<>();
            for (Beat beat : beats)
            {
                System.out.println(beat.getName());
                selectedFeatures.add(beat.getId());
            }

            Layer beatLayer = computeModel.getLayer("BEAT");
            String geometryAttributeName = beatLayer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
            displaySelectedFeatures(beatLayer, selectedFeatures, geometryAttributeName);
        }
    }

}
