package org.upes.controller;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.SchemaException;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.jts.*;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.Style;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.event.MapMouseListener;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.primitive.PrimitiveFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.upes.Constants;
import org.upes.algo.Astar;
import org.upes.algo.Dijkstra;
import org.upes.algo.NodeFactory;
import org.upes.algo.NodeGrid;
import org.upes.model.*;
import org.upes.utils.MapServer;
import org.upes.utils.StyleUtils;
import org.upes.utils.ZipMem;
import org.upes.view.MapPanel;
import org.upes.view.View;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.soap.Node;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
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
    private  JsonOp jsonOp;
    private  SqlOp sqlOp;

    private LoadAction    loadAction    = new LoadAction();
    private RemoveAction removeAction = new RemoveAction();
    private OkLayerAction okLayerAction = new OkLayerAction();
    private DeleteAction  deleteAction  = new DeleteAction();
    private OkClassificationAction okClassification =new OkClassificationAction();
    private MyTableListener tableListener = new MyTableListener();
    private CalcAction calcAction = new CalcAction();
    private PathAction pathAction = new PathAction();
    private OkPathAction okPathAction = new OkPathAction();
    private AssignScoreAction assignScoreAction = new AssignScoreAction();
    private AssignScoreOkAction assignScoreOkAction = new AssignScoreOkAction();
    boolean fileSelected = false;

    protected MapServer mapServer;

    public Controller(View view, ComputeModel computeModel, JsonOp jsonOp, SqlOp sqlOp)
    {
        this.view = view;
        this.mapPanel = view.mapPanel;
        this.computeModel = computeModel;

        this.jsonOp = jsonOp;
        this.sqlOp = sqlOp;

        // Set actions
        mapPanel.loadButton.setAction(loadAction);
        mapPanel.addButton.setAction(removeAction);
        view.layerDialog.okButton.setAction(okLayerAction);
        mapPanel.deleteButton.setAction(deleteAction);
        view.typeDialog.ok.setAction(okClassification);
        mapPanel.calculateButton.setAction(calcAction);
        mapPanel.pathButton.setAction(pathAction);
        mapPanel.scoreButton.setAction(assignScoreAction);
        view.scoresView.OkScore.setAction(assignScoreOkAction);

        view.askPathView.okButton.setAction(okPathAction);

        // Link map content
        mapPanel.mapPane.setMapContent(computeModel.getMap());
        view.removeDialog.mapPane.setMapContent(computeModel.getMapDialog());
        view.layerDialog.mapLayerTable.setMapPane(view.mapPanel.mapPane);

        // Link Table
        view.mapPanel.table.setModel(computeModel.getTableModel());

//        Classification classification = computeModel.getClassification();
//        view.mapPanel.classificationView.neutralList.setModel(classification.getNeutral());
//        view.mapPanel.classificationView.supportiveList.setModel(classification.getSupportive());
//        view.mapPanel.classificationView.defectiveList.setModel(classification.getDefective());

        // Add listener
        mapPanel.mapPane.addMouseListener(new MouseMapListener());
        mapPanel.table.getSelectionModel().addListSelectionListener(tableListener);

//        try
//        {
//            mapServer = new MapServer(Constants.SERVER_PORT);
//            mapServer.addContext("/", new ShapeFileHandler());
//            mapServer.start();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        // Check if shpfiles are registered
        if(jsonOp.ifCreated())
        {
            computeModel.bulkLoad();
            if(sqlOp.ifexists() && !sqlOp.isDbEmpty())
            {
                displayCriticalColor();
            }
        }
        else
        {
            for(int i=0; i< mapPanel.toolBar.getComponentCount();i++)
            {
                if(mapPanel.toolBar.getComponentAtIndex(i).getClass().equals(JButton.class))
                    mapPanel.toolBar.getComponentAtIndex(i).setEnabled(false);
            }

            mapPanel.calculateButton.setEnabled(false);
            mapPanel.pathButton.setEnabled(false);
            removeAction.setEnabled(false);
            deleteAction.setEnabled(false);
        }
    }

    private class RemoveAction extends AbstractAction {
        public RemoveAction()
        {
            super(Constants.NAME_REMOVE_MAP);
            this.putValue(SHORT_DESCRIPTION, Constants.DESC_REMOVE_MAP);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            view.removeDialog.tree.addTreeSelectionListener(new TreeSelection());
            view.removeDialog.populateTree(sqlOp);
            view.removeDialog.setVisible(true);
        }
    }

    private class AssignScoreAction extends AbstractAction{

        public AssignScoreAction()
        {
            super(Constants.NAME_ASSIGN_SCORE);
            this.putValue(SHORT_DESCRIPTION,Constants.DESC_ASSIGN_SCORE);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            HashMap<String,String> hm = jsonOp.getScores();
            view.scoresView.setTextFieldValues(hm);
            view.scoresView.setVisible(true);
        }
    }

    private class AssignScoreOkAction extends AbstractAction
    {
        public AssignScoreOkAction()
        {
            super(Constants.NAME_OK_DIALOG);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            jsonOp.setScores(view.scoresView.getTextFieldValues());
            jsonOp.updateIndiScore();
            view.scoresView.setVisible(false);
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

            view.typeDialog.name.setText(sourceFile.getName());
            fileSelected = false ;
            view.typeDialog.setVisible(true);

            if(fileSelected == false)
                return;

            try
            {
               if( jsonOp.updateJson(sourceFile,view.typeDialog.nameList.getSelectedItem().toString()))
                computeModel.loadFile(sourceFile);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(view, Constants.TITLE_NOT_VALID_SHP, Arrays.toString(e.getStackTrace()),
                                              JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
            }

            computeModel.setInitPath(sourceFile.getParent());

            removeAction.setEnabled(true);
            deleteAction.setEnabled(true);

            for(int i=0;i<mapPanel.toolBar.getComponentCount();i++)
            {
                if(mapPanel.toolBar.getComponentAtIndex(i).getClass().equals(JButton.class))
                    mapPanel.toolBar.getComponentAtIndex(i).setEnabled(true);
            }

            mapPanel.calculateButton.setEnabled(true);
            mapPanel.pathButton.setEnabled(true);
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
                removeAction.setEnabled(false);
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

    private class OkPathAction extends AbstractAction
    {
        public OkPathAction() {
            super(Constants.NAME_OK_DIALOG);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent)
        {
            view.askPathView.setVisible(false);

            GeometryFactory gBuilder = JTSFactoryFinder.getGeometryFactory();
            String startLatitudeText = view.askPathView.latitudeStart.getText();
            String startLongitudeText = view.askPathView.longitudeStart.getText();
            Coordinate startcoor = new Coordinate(Double.valueOf(startLatitudeText),Double.valueOf(startLongitudeText));
            Point startPoint = gBuilder.createPoint(startcoor);

            SimpleFeatureCollection tempcol = computeModel.getCollidingFeature(startPoint,computeModel.getLayer(jsonOp.getGRID_NAME()),computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getCoordinateReferenceSystem());
            SimpleFeatureIterator sitr = tempcol.features();
            System.out.println(tempcol.size());
            FeatureId startFeatureId = sitr.next().getIdentifier();

            String endLatitudeText = view.askPathView.latitudeEnd.getText();
            String endLongitudeText = view.askPathView.longitudeEnd.getText();
            Coordinate endcoor = new Coordinate(Double.valueOf(endLatitudeText),Double.valueOf(endLongitudeText));
            Point endPoint = gBuilder.createPoint(endcoor);

            tempcol = computeModel.getCollidingFeature(endPoint,computeModel.getLayer(jsonOp.getGRID_NAME()),computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getCoordinateReferenceSystem());
            sitr = tempcol.features();
            FeatureId endFeatureId = sitr.next().getIdentifier();

//            if (!startLatitudeText.isEmpty() && !startLongitudeText.isEmpty())
//                startLoc = selectBeat(Double.valueOf(startLatitudeText),Double.valueOf(startLongitudeText));
//
//            if (!endLatitudeText.isEmpty() && !endLongitudeText.isEmpty())
//                endLoc = selectBeat(Double.valueOf(endLatitudeText),Double.valueOf(endLongitudeText));

            int maxdistance = Constants.maxDistance.get(view.askPathView.distance.getSelectedItem());
            int mindistance = Constants.minDistance.get(view.askPathView.distance.getSelectedItem());
            try {
                double dis= JTS.orthodromicDistance(startcoor,endcoor,computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getCoordinateReferenceSystem());
                System.out.print("fd "+dis+"    ");
            } catch (TransformException e) {
                e.printStackTrace();
            }
//            SimpleFeatureCollection grids = computeModel.getFeaturesinCircle(Double.valueOf(startLatitudeText),Double.valueOf(startLongitudeText),maxdistance);
            System.out.println(sqlOp.getLongitude(startFeatureId.getID())+"   gg   "+startLongitudeText);
            Astar astar = new Astar(sqlOp,jsonOp,computeModel.getLayer(jsonOp.getGRID_NAME()));
            NodeGrid end = astar.calculatePath(startFeatureId,endFeatureId,mindistance,maxdistance);
            Set<FeatureId> ids = astar.getPath(end);
            displaySelectedFeatures(computeModel.getLayer(jsonOp.getGRID_NAME()),ids,computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getGeometryDescriptor().getLocalName());
            System.out.println("Total Dist: "+end.getG());
            /*
            \\Diplaying Features inside the circle.
            SimpleFeatureIterator itr = grids.features();
            HashSet<FeatureId> set = new HashSet<>();
            while (itr.hasNext())
            {
                SimpleFeature temp = itr.next();
                set.add(temp.getIdentifier());
            }
            displaySelectedFeatures(computeModel.getLayer(jsonOp.getGRID_NAME()),set,computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getGeometryDescriptor().getLocalName());
*/
//            if (startLoc==null)
//            {
// //               startLoc = firstPatrol.getGridLocation();
//                System.out.println("No Feature Returned");
//            }

//            Dijkstra dijkstra = new Dijkstra(Constants.FACTOR_SCORE);

           // double dist     = Double.parseDouble(view.askPathView.distance.getText());

           // List<Beat> beats  = dijkstra.pathFinding(computeModel.getSortedBeats(), startLoc, endLoc, dist);


//            HashSet<FeatureId> selectedFeatures = new HashSet<>();
//            for (Beat beat : beats)
//            {
//                selectedFeatures.add(beat.getId());
//            }
//
//            Layer beatLayer = computeModel.getLayer(jsonOp.getGRID_NAME());
//            String geometryAttribut1eName = beatLayer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
//            displaySelectedFeatures(beatLayer, selectedFeatures, geometryAttributeName);
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
            fileSelected = true;
            view.typeDialog.setVisible(false);
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
            if(sqlOp.ifexists()==false)
            {
                sqlOp.createDB();
            }

            computeModel.calculateScore();
//            testDisplaySelected(set);
            displayCriticalColor();

        }
    }

    SimpleFeatureCollection grabFeaturesInMouseBox(double x, double y) throws Exception
    {
        ReferencedEnvelope bbox = new ReferencedEnvelope(computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getCoordinateReferenceSystem());
        bbox.init(x-1, x+1, y-1, y+1);
        return computeModel.grabFeaturesInBoundingBox(bbox, computeModel.getLayer(jsonOp.getGRID_NAME()));
    }

    public SimpleFeature  selectBeat(double x, double y)
    {
        try {
            SimpleFeatureCollection collection = grabFeaturesInMouseBox(x,y);
            SimpleFeatureIterator iterator = collection.features();
            Beat finalBeat;
            while (iterator.hasNext())
            {
                SimpleFeature feature=iterator.next();
                return feature;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class MouseMapListener implements MapMouseListener
    {

        SimpleFeatureCollection grabFeaturesInMouseBox(MapMouseEvent ev) throws Exception
        {
            ReferencedEnvelope bbox = ev.getEnvelopeByPixels(2);
            return computeModel.grabFeaturesInBoundingBox(bbox, computeModel.getLayer(jsonOp.getGRID_NAME()));
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

                   System.out.println(simpleFeatureCollection.size());

                   Set<FeatureId> Ids=new HashSet<>();

                   while (features.hasNext()) {
                       SimpleFeature fa = features.next();

                       Geometry geometry = (Geometry) fa.getDefaultGeometry();
                       Point         centroid = geometry.getCentroid();
                       System.out.println(centroid.getCoordinate());
                       if(ev.getModifiers()== InputEvent.BUTTON1_MASK)
                       {
                           view.askPathView.latitudeStart.setText(String.valueOf(centroid.getX()));
                           view.askPathView.longitudeStart.setText(String.valueOf(centroid.getY()));
                           computeModel.setStartGrid(fa.getIdentifier());
                       }
                       else if (ev.getModifiers()== InputEvent.BUTTON3_MASK)
                       {
                           view.askPathView.latitudeEnd.setText(String.valueOf(centroid.getX()));
                           view.askPathView.longitudeEnd.setText(String.valueOf(centroid.getY()));
                           computeModel.setEndGrid(fa.getIdentifier());
                       }

                       if (computeModel.getStartGrid()!=null)
                       {
                           Ids.add(computeModel.getStartGrid());
                       }
                       if (computeModel.getEndGrid()!=null)
                       {
                           Ids.add(computeModel.getEndGrid());
                       }

/*
                       if (fa.getName().toString().equals(jsonOp.getGRID_NAME()))
                       {
                           int col_index = view.mapPanel.table.getColumn(Constants.BEAT_FEATURE_NAME).getModelIndex();
                           MyTableModel tableModel = (MyTableModel) computeModel.getTableModel();
                           int index = -1;

                           int startIndex = tableModel.getLayerStartIndex(jsonOp.getBEAT_NAME());
                           int endIndex       = tableModel.getLayerEndIndex(jsonOp.getBEAT_NAME());

                           for (int row = startIndex; row < endIndex; row++)
                           {
                               if (tableModel.getValueAt(row, col_index) != null)
                               {
                                   String temp = tableModel.getValueAt(row, col_index).toString();
                                   if (temp.equalsIgnoreCase(fa.getAttribute(Constants.BEAT_FEATURE_NAME).toString()))
                                   {
                                       index = row;
                                       break;
                                   }
                               }
                           }
                        //   if (index >= 0)
                               view.mapPanel.table.changeSelection(index, col_index, false, false);
                       }*/
                   }
                   displaySelectedFeatures(computeModel.getLayer(jsonOp.getGRID_NAME()),Ids,computeModel.getLayer(jsonOp.getGRID_NAME()).getFeatureSource().getSchema().getGeometryDescriptor().getLocalName());
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

    private class TreeSelection implements TreeSelectionListener
    {
        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) view.removeDialog.tree.getLastSelectedPathComponent();

            if (node == null)
                return;

            String type;

            if (node.isRoot())
            {
                return;
            }
            if (node.isLeaf())
            {
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                type = (String) parentNode.getUserObject();
            }
            else
            {
                type = (String) node.getUserObject();
            }

            ArrayList<String> list = jsonOp.getFilesFromType(type);
            try {
                computeModel.clearMapDialog();
                computeModel.loadMapDialog(list);
                repaintMapDialog();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

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
        Layer        beatLayer    = computeModel.getLayer(jsonOp.getBEAT_NAME());
        if (beatLayer == null)
            return;

        MyTableModel tableModel   = (MyTableModel) computeModel.getTableModel();
        int[]        selectedRows = mapPanel.table.getSelectedRows();
        int          column       = tableModel.getColumn(Constants.BEAT_FEATURE_NAME);
        int          startBeat    = tableModel.getLayerStartIndex(jsonOp.getBEAT_NAME());
        int          endBeat      = tableModel.getLayerEndIndex(jsonOp.getBEAT_NAME());

        Set<FeatureId> selectedFeatures = new HashSet<>();

        for (int selectedRow : selectedRows)
        {
            Object         valueAt          = tableModel.getValueAt(selectedRow, column);

            // If it's a row from beat layer
            if (selectedRow >= startBeat && selectedRow < endBeat)
            {
                // Get layer feature
                SimpleFeature featureFast = (SimpleFeature) computeModel.findFeature(beatLayer, Constants.BEAT_FEATURE_NAME, (String) valueAt);

                // Change color of this feature
                selectedFeatures.add(featureFast.getIdentifier());
            }
            String geometryAttributeName = beatLayer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
            displaySelectedFeatures(beatLayer, selectedFeatures, geometryAttributeName);
        }
    }

    public void testDisplaySelected(Set<FeatureId> Ids)
    {
        Style style;
        RuleEntry selectedEntry ;
        RuleEntry defaultEntry;
        defaultEntry = new RuleEntry(Color.BLACK, Color.LIGHT_GRAY, 1.0);
        selectedEntry = new RuleEntry(Color.BLUE, Color.LIGHT_GRAY, 2.0);
        Layer layer = computeModel.getLayer(jsonOp.getGRID_NAME());
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

//        style = StyleUtils.createSelectedStyle(sqlOp,sqlOp.getSortedScores().size(),selectedEntry, Ids, layer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName());
        try {
            SimpleFeatureCollection col = (SimpleFeatureCollection) layer.getFeatureSource().getFeatures(ff.id(Ids));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ((FeatureLayer) layer).setStyle(style);
        repaint();

    }

    public void displaySelectedFeatures(Layer layer, Set<FeatureId> IDs, String geometryAttributeName) {
        Style style;
        RuleEntry defaultEntry = null;
        RuleEntry selectedEntry = null;
        String beatName = null;
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        SimpleFeatureCollection col = null;
        try {
            col = (SimpleFeatureCollection) layer.getFeatureSource().getFeatures(ff.id(IDs));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (layer.getTitle().equals(jsonOp.getBEAT_NAME()))
        {
            defaultEntry = new RuleEntry(Color.BLACK, Color.LIGHT_GRAY, 1.0);
            selectedEntry = new RuleEntry(Color.BLUE, Color.LIGHT_GRAY, 2.5);
            beatName = (String) col.features().next().getAttribute(Constants.BEAT_FEATURE_NAME);

        }
        else if (layer.getTitle().equals(jsonOp.getGRID_NAME()))
        {
            defaultEntry = new RuleEntry(Color.BLACK, Color.LIGHT_GRAY, 1.0);
            selectedEntry = new RuleEntry(Color.WHITE, Color.BLACK, 1.2);
            beatName = sqlOp.getParentBeat(IDs.iterator().next().getID().toString());
        }

        
        // Displaying selected features.
        if (!sqlOp.isDbEmpty())
        {
            if (IDs.isEmpty())
            {
              style = StyleUtils.createStyleFromCritical(sqlOp, geometryAttributeName);
            }
            else
            {
                style = StyleUtils.createSelectedStyle(sqlOp,(sqlOp.getSortedScores()).size(),selectedEntry, IDs, geometryAttributeName,beatName);
            }

        }
        else
        {
            style = StyleUtils.createDefaultStyle(defaultEntry,geometryAttributeName);
        }

        if (layer.getTitle().equals(jsonOp.getBEAT_NAME()) && computeModel.isInvalidData()) {

                org.opengis.geometry.Envelope region = col.getBounds();
                computeModel.setRegion(region);
                mapPanel.mapPane.setDisplayArea(region);

        }

        ((FeatureLayer) computeModel.getLayer(jsonOp.getGRID_NAME())).setStyle(style);
        repaint();
    }

    public void displayCriticalColor()
    {
        Layer gridLayer    = computeModel.getLayer(jsonOp.getGRID_NAME());
        if (gridLayer == null)
            return;
        String geometryAttributeName = gridLayer.getFeatureSource().getSchema().getGeometryDescriptor().getLocalName();

        Style style = StyleUtils.createStyleFromCritical(sqlOp, geometryAttributeName);

        ((FeatureLayer) gridLayer).setStyle(style);
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

    public void repaintMapDialog()
    {
        view.removeDialog.mapPane.repaint();
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
            if (sqlOp.isDbEmpty())
            {
                computeModel.calculateScore();
                displayCriticalColor();
            }

            view.askPathView.setVisible(true);

        }
    }

}
