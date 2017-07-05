package org.upes.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by eadgyo on 04/07/17.
 */
public class LoadingPanel extends JPanel
{
    BufferedImage loadingImage;

    public static BufferedImage getImage(final String pathAndFileName) throws IOException
    {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(pathAndFileName);
        if (url != null)
        {
            return ImageIO.read(url);
        }
        return null;
    }

    public LoadingPanel()
    {
        try
        {
            loadingImage = getImage("upes.png");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void paint(Graphics graphics)
    {
        super.paint(graphics);

        // Compute location to display image in center of screen
        Rectangle clipBounds = graphics.getClipBounds();
        int xImage = (int) (clipBounds.getWidth()/2 - loadingImage.getWidth()/2);
        int yImage = (int) (clipBounds.getHeight()/2 - loadingImage.getHeight()/2);

        graphics.drawImage(loadingImage, xImage, yImage, this);
    }
}
