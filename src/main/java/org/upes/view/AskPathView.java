package org.upes.view;

import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

public class AskPathView extends JDialog
{
    public JTextField latitude = new JTextField();
    public JLabel latitudeLabel = new JLabel(Constants.LATITUDE_LABEL, JLabel.TRAILING);
    public JTextField longitude = new JTextField();
    public JLabel longitudeLabel = new JLabel(Constants.LONGITUDE_LABEL, JLabel.TRAILING);
    public JTextField distance = new JTextField(Constants.DEFAULT_VALUE_DISTANCE_PATH);
    public JLabel  distanceLabel = new JLabel(Constants.DISTANCE_LABEL, JLabel.TRAILING);
    public JLabel description = new JLabel(Constants.DESCRIPTION_ASK_DIALOG, JLabel.TRAILING);
    public JButton okButton = new JButton();

    public AskPathView(Frame frame)
    {
        super(frame, Constants.ASK_DIALOG_TITLE, false);
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        JPanel descriptionPane = new JPanel();
        descriptionPane.add(description);
        this.add(descriptionPane);
        this.add(Box.createVerticalStrut(5));

        JPanel midPane = new JPanel();
        SpringLayout layout      = new SpringLayout();
        this.add(midPane);
        midPane.setLayout(layout);

        JPanel lowestPane = new JPanel();
        lowestPane.add(okButton);
        this.add(lowestPane);

        JLabel[] labels = {distanceLabel, latitudeLabel, longitudeLabel};
        JTextField[] fields = {distance, latitude, longitude};

        int numPairs = labels.length;

        //Create and populate the panel.
        for (int i = 0; i < numPairs; i++)
        {
            midPane.add(labels[i]);
            labels[i].setLabelFor(fields[i]);
            midPane.add(fields[i]);
        }

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(midPane, //parent
                                        3, 2,
                                        3, 3,  //initX, initY
                                        16, 3); //xPad, yPad

        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }
}

