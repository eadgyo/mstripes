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
    public JList cooperativeList = new JList();
    public JList defectiveList = new JList();

    public ClassificationView()
    {
        JScrollPane supportiveScrollPane = new JScrollPane(supportiveList);
        JScrollPane cooperativeScrollPane = new JScrollPane(cooperativeList);
        JScrollPane defectiveScrollPane = new JScrollPane(defectiveList);

        JLabel supportiveLabel = new JLabel(Constants.StrSUPPORTIVE);
        JLabel cooperativeLabel = new JLabel(Constants.StrCOOPERATIVE);
        JLabel defectiveLabel = new JLabel(Constants.StrDEFECTIVE);

        JPanel supportivePane = new JPanel();
        JPanel cooperativePane = new JPanel();
        JPanel defectivePane = new JPanel();

        supportivePane.setLayout(new BoxLayout(supportivePane,BoxLayout.Y_AXIS));
        supportivePane.add(supportiveLabel);
        supportivePane.add(supportiveScrollPane);

        cooperativePane.setLayout(new BoxLayout(cooperativePane,BoxLayout.Y_AXIS));
        cooperativePane.add(cooperativeLabel);
        cooperativePane.add(cooperativeScrollPane);

        defectivePane.setLayout(new BoxLayout(defectivePane,BoxLayout.Y_AXIS));
        defectivePane.add(defectiveLabel);
        defectivePane.add(defectiveScrollPane);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        this.add(supportivePane);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(cooperativePane);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(defectivePane);
    }
}
