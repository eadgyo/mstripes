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
    JRadioButton neutral=new JRadioButton(Constants.StrNEUTRAL);
    JRadioButton defective=new JRadioButton(Constants.StrDEFECTIVE);
    JRadioButton supportive=new JRadioButton(Constants.StrSUPPORTIVE);
    JRadioButton none=new JRadioButton(Constants.StrNONE);

    ButtonGroup buttonGroup;

    public OptionsDialog(Frame frame)
    {
        super(frame,true);
        setLocationRelativeTo(frame);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        buttonGroup=new ButtonGroup();
        buttonGroup.add(supportive);
        buttonGroup.add(neutral);
        buttonGroup.add(defective);
        buttonGroup.add(none);
        heading.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(heading);
        supportive.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(supportive);
        neutral.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(neutral);
        defective.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(defective);
        none.setAlignmentX(Box.CENTER_ALIGNMENT);
        none.setSelected(true);
        panel.add(none);
        ok.setAlignmentX(Box.CENTER_ALIGNMENT);
        panel.add(ok);
        add(panel);
    }

    public int getSelectedOption()
    {
        if (supportive.isSelected())
            return Constants.SUPPORTIVE;
        else if (neutral.isSelected())
            return Constants.COOPERATIVE;
        else if (defective.isSelected())
            return Constants.DEFECTIVE;

        return 0;
    }
}
