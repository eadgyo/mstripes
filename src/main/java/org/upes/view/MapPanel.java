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
    public ClassificationView classificationView = new ClassificationView();
    public JPanel middlePart = new JPanel(new BorderLayout());
    public JPanel rightPart = new JPanel();

    public JButton loadButton = new JButton();
    public JButton addButton = new JButton();
    public JButton deleteButton = new JButton();
    public JButton calculateButton = new JButton();
    public JButton pathButton = new JButton();

    public JMapPane    mapPane     = new JMapPane();
    public JTable      table       = new JTable();
    public JScrollPane scrollTable = new JScrollPane(table);

    public Set<JMapFrame.Tool> toolSet;
    public JToolBar            toolBar;
    public JSplitPane jSplitPane;


    public MapPanel()
    {
        this.setLayout(new BorderLayout());
        // Left part
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER,12,80));
        buttonsPanel.add(loadButton);
        leftPart.add(buttonsPanel, BorderLayout.NORTH);
        leftPart.add(classificationView, BorderLayout.CENTER);
        leftPart.add(scrollTable, BorderLayout.SOUTH);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Center part
        toolSet = EnumSet.allOf(JMapFrame.Tool.class);
        toolBar = new JToolBar();
        initToolBar();
        middlePart.add(toolBar, BorderLayout.NORTH);
        middlePart.add(mapPane, BorderLayout.CENTER);
        middlePart.setMinimumSize(Constants.MIN_MAP_DIMENSION_SIZE);

        jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPart, middlePart);

        rightPart.setLayout(new BoxLayout(rightPart,BoxLayout.Y_AXIS));
        // Right part
        rightPart.add(Box.createRigidArea(new Dimension(0,10)));
        deleteButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        rightPart.add(deleteButton);
        rightPart.add(Box.createRigidArea(new Dimension(0,10)));
        addButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        rightPart.add(addButton);
        rightPart.add(Box.createRigidArea(new Dimension(0,10)));
        calculateButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        rightPart.add(calculateButton);
        rightPart.add(Box.createRigidArea(new Dimension(0,10)));
        pathButton.setAlignmentX(Box.CENTER_ALIGNMENT);
        rightPart.add(pathButton);
        rightPart.add(Box.createRigidArea(new Dimension(0,10)));
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
