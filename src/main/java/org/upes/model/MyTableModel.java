package org.upes.model;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by eadgyo on 28/06/17.
 */
public class MyTableModel extends DefaultTableModel
{
    private Map<String, Integer> colNameIndex = new HashMap<>();
    private Vector<String>       colNames     = new Vector();
    private HashMap<String, LayerEntry> layerToMap = new HashMap<>();

    private class LayerEntry
    {
        public String layerName;
        public int startIndex;
        public int endIndex;
    }

    public int getColumn(String columnName)
    {
        Integer index = colNameIndex.get(columnName);
        if (index == null)
        {
            index = addColumn(columnName);
        }
        return index;
    }

    public int addColumn(String columnName)
    {
        int index = colNameIndex.size();
        colNames.add(columnName);
        colNameIndex.put(columnName, index);
        TableColumn tableColumn = new TableColumn(0);
        addColumn(tableColumn);

        return index;
    }

    @Override
    public String getColumnName(int col)
    {
        return colNames.get(col);
    }

    private LayerEntry getLayerEntry(String layerName)
    {
        return layerToMap.get(layerName);
    }

    public void addRow(String name, Vector vector)
    {
        LayerEntry layerEntry = getLayerEntry(name);

        if (layerEntry == null)
        {
            layerEntry = new LayerEntry();
            layerEntry.layerName = name;
            layerToMap.put(layerEntry.layerName, layerEntry);
            layerEntry.startIndex = this.getRowCount();
        }

        addRow(vector);
        layerEntry.endIndex = this.getRowCount();
    }


    public void removeLayer(String name)
    {
        LayerEntry removedLayerEntry = layerToMap.remove(name);

        if (removedLayerEntry == null)
            return;

        for (int i = removedLayerEntry.startIndex; i < removedLayerEntry.endIndex; i++)
        {
            removeRow(removedLayerEntry.startIndex);
        }

        updateLayerEntry(removedLayerEntry);
    }

    public void updateLayerEntry(LayerEntry removedEntry)
    {
        int removedCount = removedEntry.endIndex - removedEntry.startIndex;

        for (LayerEntry entry : layerToMap.values())
        {
            if (entry.startIndex > removedEntry.startIndex)
            {
                entry.startIndex -= removedCount;
                entry.endIndex -= removedCount;
            }
        }
    }

    public void multiplyColumn(Number factor, int row, int column)
    {
        Number valueAt = (Number) getValueAt(row, column);
        setValueAt(NumberOp.multiply(valueAt, factor), row, column);
    }

    public int getLayerStartIndex(String name)
    {
        return layerToMap.get(name).startIndex;
    }

    public int getLayerEndIndex(String name)
    {
        return layerToMap.get(name).endIndex;
    }
}


