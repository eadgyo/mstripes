package org.upes.utils;

import org.upes.model.RuleEntry;

import java.awt.*;

public class CriticalGrid
{
    private final static Color BEIGE = new Color(240, 232, 196);

    private final static RuleEntry MOST = new RuleEntry(Color.BLACK, getColor(10), 1.0);
    private final static RuleEntry CRITICAL = new RuleEntry(Color.BLACK, getColor(20), 1.0);
    private final static RuleEntry MODERATE = new RuleEntry(Color.BLACK, getColor(30), 1.0);
    private final static RuleEntry NORMAL = new RuleEntry(Color.BLACK, getColor(40), 1.0);
    private final static RuleEntry LOW = new RuleEntry(Color.BLACK, getColor(50), 1.0);
    private final static RuleEntry LOWEST = new RuleEntry(Color.BLACK, getColor(60), 1.0);

    public static Color getColor(double critical)
    {
        if (critical <= -15)
            return Color.RED;
        else if (critical <= -10)
            return Color.GRAY;
        else if (critical <= -5)
            return Color.ORANGE;
        else if (critical <= 0)
            return Color.BLUE;
        else if (critical <= 5)
            return Color.GREEN;
        else
            return BEIGE;
    }

    public static RuleEntry getStyle(double critical)
    {
        if (critical <= 10)
            return MOST;
        else if (critical <= 20)
            return CRITICAL;
        else if (critical <= 30)
            return MODERATE;
        else if (critical <= 40)
            return NORMAL;
        else if (critical <= 50)
            return LOW;
        else
            return LOWEST;
    }
}