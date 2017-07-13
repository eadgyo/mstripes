package org.upes.model;

import org.geotools.map.Layer;

import javax.swing.*;

/**
 * Created by eadgyo on 12/07/17.
 */
public class Classification
{
    private DefaultListModel<String> cooperative = new DefaultListModel<>();
    private DefaultListModel<String> defective = new DefaultListModel<>();
    private DefaultListModel<String>  supportive = new DefaultListModel<>();

    public void addCooperative(Layer layer)
    {
        cooperative.add(0, layer.getTitle());
    }

    public void addSupportive(Layer layer)
    {
        supportive.add(0, layer.getTitle());
    }

    public void addDefective(Layer layer)
    {
        defective.add(0, layer.getTitle());
    }

    public DefaultListModel<String> getCooperative()
    {
        return cooperative;
    }

    public DefaultListModel<String> getDefective()
    {
        return defective;
    }

    public DefaultListModel<String> getSupportive()
    {
        return supportive;
    }
}
