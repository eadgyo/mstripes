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
    JRadioButton cooperative=new JRadioButton(Constants.StrCOOPERATIVE);
    JRadioButton defective=new JRadioButton(Constants.StrDEFECTIVE);
    JRadioButton supportive=new JRadioButton(Constants.StrSUPPORTIVE);

    ButtonGroup buttonGroup;

    public OptionsDialog(Frame frame)
    {
        super(frame,true);
        setLocationRelativeTo(frame);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        buttonGroup=new ButtonGroup();
        buttonGroup.add(supportive);
        buttonGroup.add(cooperative);
        buttonGroup.add(defective);
        heading.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(heading);
        supportive.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(supportive);
        cooperative.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(cooperative);
        defective.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(defective);
        ok.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(ok);
        add(panel);

    }

    public int getSelectedOption()
    {
        if (supportive.isSelected())
            return Constants.SUPPORTIVE;
        else if (cooperative.isSelected())
            return Constants.COOPERATIVE;
        else if (defective.isSelected())
            return Constants.DEFECTIVE;

        return 0;
    }
}
