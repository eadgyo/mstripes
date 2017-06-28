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
}


