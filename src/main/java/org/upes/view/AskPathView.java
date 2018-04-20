package org.upes.view;

import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

public class AskPathView extends JDialog
{
    public JTextField latitudeStart = new JTextField();
    public JLabel latitudeLabelStart = new JLabel(Constants.START_LATITUDE_LABEL, JLabel.TRAILING);
    public JTextField longitudeStart = new JTextField();
    public JLabel longitudeLabelStart = new JLabel(Constants.START_LONGITUDE_LABEL, JLabel.TRAILING);
    public JComboBox distance = new JComboBox(Constants.Distances);
    public JLabel  distanceLabel = new JLabel(Constants.DISTANCE_LABEL, JLabel.TRAILING);
    public JLabel description = new JLabel(Constants.DESCRIPTION_ASK_DIALOG, JLabel.TRAILING);
    public JButton okButton = new JButton();
    public JTextField latitudeEnd = new JTextField();
    public JLabel latitudeLabelEnd = new JLabel(Constants.END_LATITUDE_LABEL,JLabel.TRAILING);
    public JTextField longitudeEnd = new JTextField();
    public JLabel longitudeLabelEnd = new JLabel(Constants.END_LATITUDE_LABEL, JLabel.TRAILING);

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

        JLabel[] labels = {latitudeLabelStart, longitudeLabelStart ,latitudeLabelEnd ,longitudeLabelEnd };
        JTextField[] fields = {latitudeStart, longitudeStart, latitudeEnd, longitudeEnd };

        int numPairs = labels.length;

        midPane.add(distanceLabel);
        distanceLabel.setLabelFor(distance);
        midPane.add(distance);
        //Create and populate the panel.
        for (int i = 0; i < numPairs; i++)
        {
            midPane.add(labels[i]);
            labels[i].setLabelFor(fields[i]);
            midPane.add(fields[i]);
        }

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(midPane, //parent
                                        5, 2,
                                        3, 3,  //initX, initY
                                        16, 3); //xPad, yPad

        this.setLocationRelativeTo(null);
        this.setVisible(false);
    }
}

