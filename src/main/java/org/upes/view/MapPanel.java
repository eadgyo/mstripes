package org.upes.view;

import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.*;
import org.upes.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by eadgyo on 04/07/17.
 */
public class MapPanel extends JPanel
{
    public JPanel leftPart = new JPanel(new BorderLayout());
    public JPanel middlePart = new JPanel(new BorderLayout());
    public JPanel rightPart = new JPanel(new FlowLayout());

    public JButton loadButton = new JButton();
    public JButton addButton = new JButton();
    public JButton deleteButton=new JButton();

    public JMapPane    mapPane     = new JMapPane();
    public JTable      table       = new JTable();
    public JScrollPane scrollTable = new JScrollPane(table);

    public Set<JMapFrame.Tool> toolSet;
    public JToolBar            toolBar;


    public MapPanel()
    {
        this.setLayout(new BorderLayout());
        // Left part
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER,12,90));
        buttonsPanel.add(loadButton);
        leftPart.add(buttonsPanel, BorderLayout.NORTH);
        leftPart.add(scrollTable, BorderLayout.SOUTH);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Center part
        toolSet = EnumSet.allOf(JMapFrame.Tool.class);
        toolBar = new JToolBar();
        initToolBar();
        middlePart.add(toolBar, BorderLayout.NORTH);
        middlePart.add(mapPane, BorderLayout.CENTER);
        middlePart.setMinimumSize(Constants.MIN_MAP_DIMENSION_SIZE);

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPart, middlePart);

        // Right part
        rightPart.add(addButton);
        rightPart.add(deleteButton);

        this.add(jSplitPane, BorderLayout.CENTER);
        this.add(rightPart, BorderLayout.EAST);
    }

    private void initToolBar()
    {
        this.toolBar = new JToolBar();
        this.toolBar.setOrientation(0);
        this.toolBar.setFloatable(false);
        ButtonGroup cursorToolGrp = new ButtonGroup();
        JButton btn;
        if(this.toolSet.contains(JMapFrame.Tool.POINTER)) {
            btn = new JButton(new NoToolAction(this.mapPane));
            btn.setName("ToolbarPointerButton");
            this.toolBar.add(btn);
            cursorToolGrp.add(btn);
        }

        if(this.toolSet.contains(JMapFrame.Tool.ZOOM)) {
            btn = new JButton(new ZoomInAction(this.mapPane));
            btn.setName("ToolbarZoomInButton");
            this.toolBar.add(btn);
            cursorToolGrp.add(btn);
            btn = new JButton(new ZoomOutAction(this.mapPane));
            btn.setName("ToolbarZoomOutButton");
            this.toolBar.add(btn);
            cursorToolGrp.add(btn);
            this.toolBar.addSeparator();
        }

        if(this.toolSet.contains(JMapFrame.Tool.PAN)) {
            btn = new JButton(new PanAction(this.mapPane));
            btn.setName("ToolbarPanButton");
            this.toolBar.add(btn);
            cursorToolGrp.add(btn);
            this.toolBar.addSeparator();
        }

        if(this.toolSet.contains(JMapFrame.Tool.INFO)) {
            btn = new JButton(new InfoAction(this.mapPane));
            btn.setName("ToolbarInfoButton");
            this.toolBar.add(btn);
            this.toolBar.addSeparator();
        }

        if(this.toolSet.contains(JMapFrame.Tool.RESET)) {
            btn = new JButton(new ResetAction(this.mapPane));
            btn.setName("ToolbarResetButton");
            this.toolBar.add(btn);
        }
    }
}
