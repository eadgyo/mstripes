package org.upes.view;

import javafx.scene.control.SplitPane;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.action.*;
import org.upes.model.SqlOp;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class RemoveDialog extends JDialog {

    JPanel mainpanel = new JPanel();
    JButton deleteButton = new JButton("Remove");
    public JTree tree = new JTree();
    public JMapPane mapPane = new JMapPane();
    public JSplitPane splitPane;
    public Set<JMapFrame.Tool> toolSet;
    public JToolBar toolBar;

    public RemoveDialog(Frame frame) {
        super(frame, true);
        mainpanel.setLayout(new BorderLayout());
        JPanel displayPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        //button Panel
        buttonPanel.add(deleteButton);

        //map Panel
        toolSet = EnumSet.allOf(JMapFrame.Tool.class);
        toolBar = new JToolBar();
        initToolBar();
        displayPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tree);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        displayPanel.setPreferredSize(new Dimension(800, 500));
        JPanel mapPanel = new JPanel(new BorderLayout());
        mapPanel.add(toolBar,BorderLayout.NORTH);
        mapPanel.add(mapPane,BorderLayout.CENTER);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, mapPanel);
        displayPanel.add(splitPane, BorderLayout.CENTER);
        mainpanel.add(displayPanel, BorderLayout.CENTER);
        mainpanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainpanel);
    }

    public void populateTree(SqlOp sqlOp) {
        ArrayList<String> list = sqlOp.getTypes();

        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode subCategory = null;

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Layers");

        for (String type : list) {
            category = new DefaultMutableTreeNode(type);
            top.add(category);
            ArrayList<String> features = sqlOp.getFeaturesFromType(type);

            for (String feature : features) {
                subCategory = new DefaultMutableTreeNode(feature);
                category.add(subCategory);
            }
        }
        TreeModel treeModel = new DefaultTreeModel(top);
        tree.setModel(treeModel);
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
        }

        if(this.toolSet.contains(JMapFrame.Tool.PAN)) {
            btn = new JButton(new PanAction(this.mapPane));
            btn.setName("ToolbarPanButton");
            this.toolBar.add(btn);
            cursorToolGrp.add(btn);
        }

        if(this.toolSet.contains(JMapFrame.Tool.RESET)) {
            btn = new JButton(new ResetAction(this.mapPane));
            btn.setName("ToolbarResetButton");
            this.toolBar.add(btn);
        }
    }
  }
