package org.upes.view;

import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by This Pc on 12-07-2017.
 */
public class OptionsDialog extends JDialog {

    public JButton ok = new JButton();
    public JPanel panel = new JPanel();
    JLabel heading=new JLabel(Constants.OPTIONS_QUESTION);
    JRadioButton cooperative=new JRadioButton(Constants.COOPERATIVE);
    JRadioButton defective=new JRadioButton(Constants.DEFECTIVE);
    JRadioButton supportive=new JRadioButton(Constants.SUPPORTIVE);

    ButtonGroup buttonGroup;

    public OptionsDialog(Frame frame)
    {
        super(frame,true);
        setLocationRelativeTo(frame);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        buttonGroup=new ButtonGroup();
        buttonGroup.add(cooperative);
        buttonGroup.add(defective);
        buttonGroup.add(supportive);
        heading.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(heading);
        cooperative.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(cooperative);
        defective.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(defective);
        supportive.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(supportive);
        ok.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(ok);
        add(panel);

    }

    public int getSelectedOption()
    {
        if (supportive.isSelected())
            return 3;
        else if (cooperative.isSelected())
            return 1;
        else if (defective.isSelected())
            return 2;

        return 0;
    }
}
