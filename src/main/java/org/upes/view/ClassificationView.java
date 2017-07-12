package org.upes.view;

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

        JLabel supportiveLabel = new JLabel("Supportive");
        JLabel cooperativeLabel = new JLabel("Cooperative");
        JLabel defectiveLabel = new JLabel("Defective");

        JPanel supportivePane = new JPanel();
        JPanel cooperativePane = new JPanel();
        JPanel defectivePane = new JPanel();

        supportivePane.add(supportiveLabel);
        supportivePane.add(supportiveScrollPane);

        cooperativePane.add(cooperativeLabel);
        cooperativePane.add(cooperativeScrollPane);

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
