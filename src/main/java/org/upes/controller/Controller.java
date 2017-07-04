package org.upes.controller;

import org.upes.Constants;
import org.upes.model.Model;
import org.upes.view.MapPanel;
import org.upes.view.View;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


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

    public Controller(View view, Model model)
    {
        this.view = view;
        this.mapPanel = view.mapPanel;
        this.model = model;

        // Set actions
        mapPanel.loadButton.setAction(loadAction);
        mapPanel.addButton.setAction(addAction);
        view.layerDialog.okButton.setAction(okAction);

        addAction.setEnabled(false);
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
            //this.setEnabled(false);
            addAction.setEnabled(true);
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
            view.layerDialog.setVisible(false);
        }
    }
}
