package org.upes.controller;

import org.geotools.swing.data.JFileDataStoreChooser;
import org.upes.Constants;
import org.upes.model.Model;
import org.upes.view.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * Created by eadgyo on 27/06/17.
 */
public class Controller
{
    private View view;
    private Model model;

    private LoadAction loadAction = new LoadAction();
    private AddAction addAction = new AddAction();
    private OkAction okAction = new OkAction();

    public Controller(View view, Model model)
    {
        this.view = view;
        this.model = model;

        // Set actions
        view.loadButton.setAction(loadAction);
        view.addButton.setAction(addAction);
        view.layerDialog.okButton.setAction(okAction);

        addAction.setEnabled(false);

        // Link map content
        //view.mapPane.setMapContent(model.getMap());
        view.layerDialog.mapLayerTable.setMapPane(view.mapPane);

        // Link Table
        view.table.setModel(model.getTableModel());
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
            view.layerDialog.setVisible(true);
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
            File initialDir = new File("/home/ronan-j/Documents/Basemaps/");
            File sourceFile = JFileDataStoreChooser.showOpenFile("shp", initialDir, view);
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

            //this.setEnabled(false);
            addAction.setEnabled(true);

            view.mapPane.setMapContent(model.getMap());
            view.mapPane.repaint();
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
