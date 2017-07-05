package org.upes.view;

import org.upes.Constants;

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
    private BufferedImage upesImage;
    private BufferedImage wildLifeImage;

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
        super();
        try
        {
            upesImage = getImage(Constants.IMAGE_UPES_PATH);
            wildLifeImage = getImage(Constants.IMAGE_WILDLIFE_PATH);
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
        int xImage = (int) (clipBounds.getWidth()/2 - wildLifeImage.getWidth()/2);
        int yImage = (int) (clipBounds.getHeight()/2 - wildLifeImage.getHeight() - Constants.WILDLIFE_PADDING);

        graphics.drawImage(wildLifeImage, xImage, yImage, this);

        xImage = (int) (clipBounds.getWidth()/2 - upesImage.getWidth()/2);
        yImage = (int) (clipBounds.getHeight()/2 + Constants.UPES_PADDING);

        graphics.drawImage(upesImage, xImage, yImage, this);
    }
}
