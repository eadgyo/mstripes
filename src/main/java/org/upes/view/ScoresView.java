package org.upes.view;

import org.upes.Constants;
import org.upes.model.JsonOp;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScoresView extends JDialog {

    public JButton OkScore = new JButton();
    public HashMap<String,JTextField> textFields = new HashMap<>();

    public ScoresView(Frame frame)
    {
        super(frame, true);

        this.setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        int it = 0;
        for(String value : Constants.TYPES)
        {
           JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT,20,10));
           temp.add(new JLabel(value));
           JTextField tempField = new JTextField("Nil",3);
           textFields.put(value,tempField);
           temp.add(tempField);
           this.add(temp);
        }

        this.add(OkScore);
    }

    public void setTextFieldValues(HashMap<String,String> hashMap)
    {
        for(Map.Entry m : hashMap.entrySet())
        {
            if(!m.getValue().equals("Nil"))
            {textFields.get(m.getKey()).setText((String) m.getValue());}
        }
    }

    public HashMap<String, String> getTextFieldValues()
    {
        HashMap<String,String> map = new HashMap<>();

        for(Map.Entry m :textFields.entrySet())
        {
            map.put(m.getKey().toString(),((JTextField)m.getValue()).getText());
        }

        return map;
    }

}
