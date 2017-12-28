package org.upes.view;

import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

public class TypeDialog extends JDialog{

    public JLabel name = new JLabel("Test");
    public JComboBox<String> nameList = new JComboBox(Constants.TYPES);
    JLabel heading=new JLabel(Constants.OPTIONS_QUESTION);
    JLabel CboxHeading = new JLabel(Constants.CBOXHEADING);
    public JButton ok = new JButton();
    public JPanel panel = new JPanel();
    JPanel dropbox = new JPanel();

    public TypeDialog(Frame frame)
    {
        super (frame, true);
        setLocationRelativeTo(frame);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(heading);
        heading.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(name);
        name.setAlignmentX(Box.CENTER_ALIGNMENT);
        dropbox.setLayout(new FlowLayout(FlowLayout.CENTER ,10,5));
        dropbox.add(CboxHeading);
        dropbox.add(nameList);
        panel.add(dropbox);
        panel.add(ok);
        ok.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        add(panel);
    }
}
