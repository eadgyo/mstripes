package org.upes.model;

import java.awt.*;

/**
 * Created by eadgyo on 12/07/17.
 */
public class RuleEntry
{
    public Color outlineColor;
    public Color fillColor;
    public double lineWidth;
    public double opacity;

    public RuleEntry(Color outlineColor, Color fillColor, double lineWidth)
    {
        this.outlineColor = outlineColor;
        this.fillColor = fillColor;
        this.lineWidth = lineWidth;
    }

    public RuleEntry(Color outlineColor, Color fillColor, double lineWidth,double opacity)
    {
        this.outlineColor = outlineColor;
        this.fillColor = fillColor;
        this.lineWidth = lineWidth;
        this.opacity = opacity;
    }
}
