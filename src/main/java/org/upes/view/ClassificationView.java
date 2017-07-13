package org.upes.view;

import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eadgyo on 12/07/17.
 */
public class ClassificationView extends JPanel
{
    public JList supportiveList = new JList();
    public JList neutralList = new JList();
    public JList defectiveList = new JList();

    public ClassificationView()
    {
        JScrollPane supportiveScrollPane = new JScrollPane(supportiveList);
        JScrollPane neutralScrollPane = new JScrollPane(neutralList);
        JScrollPane defectiveScrollPane = new JScrollPane(defectiveList);

        JLabel supportiveLabel = new JLabel(Constants.StrSUPPORTIVE);
        JLabel neutralLabel = new JLabel(Constants.StrNEUTRAL);
        JLabel defectiveLabel = new JLabel(Constants.StrDEFECTIVE);

        JPanel supportivePane = new JPanel();
        JPanel neutralPane = new JPanel();
        JPanel defectivePane = new JPanel();

        supportivePane.setLayout(new BoxLayout(supportivePane,BoxLayout.Y_AXIS));
        supportivePane.add(supportiveLabel);
        supportivePane.add(supportiveScrollPane);

        neutralPane.setLayout(new BoxLayout(neutralPane,BoxLayout.Y_AXIS));
        neutralPane.add(neutralLabel);
        neutralPane.add(neutralScrollPane);

        defectivePane.setLayout(new BoxLayout(defectivePane,BoxLayout.Y_AXIS));
        defectivePane.add(defectiveLabel);
        defectivePane.add(defectiveScrollPane);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.add(supportivePane);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(neutralPane);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(defectivePane);
    }
}
