package org.upes.view;

import org.geotools.swing.MapLayerTable;
import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eadgyo on 28/06/17.
 */
public class LayerDialog extends JDialog
{
    public MapLayerTable mapLayerTable;
    public JButton okButton = new JButton();
    public JPanel panel = new JPanel(new FlowLayout(FlowLayout.TRAILING));

    public LayerDialog(Frame frame)
    {
        super(frame, Constants.ADD_LAYER_DIALOG_TITLE, true);
        mapLayerTable = new MapLayerTable();
        mapLayerTable.setMinimumSize(Constants.MIN_LAYERS_DIMENSION_SIZE);
        panel.add(mapLayerTable);
        panel.add(okButton);

        add(panel);
    }
}
