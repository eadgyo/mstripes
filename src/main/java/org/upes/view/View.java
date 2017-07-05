package org.upes.view;

import org.upes.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eadgyo on 27/06/17.
 */
public class View extends JFrame
{
    public LayerDialog layerDialog = new LayerDialog(this);
    public CardLayout  card        = new CardLayout();

    public JPanel mainPanel          = new JPanel();
    public MapPanel     mapPanel     = new MapPanel();
    public LoadingPanel loadingPanel = new LoadingPanel();

    public View()
    {
        // Set the frame
        super(Constants.MAIN_WINDOW_TILE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BorderLayout());
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Add switched panel to card
        this.add(mainPanel);
        mainPanel.setLayout(card);
        mainPanel.add("a", loadingPanel);
        mainPanel.add("b", mapPanel);
    }


    public void swapCard()
    {
        card.next(mainPanel);
    }

    public void startLoading()
    {
        this.validate();
        this.pack();

        this.layerDialog.pack();
        this.layerDialog.setLocationRelativeTo(null);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
