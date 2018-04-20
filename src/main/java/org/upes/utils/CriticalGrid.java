package org.upes.utils;

import org.geotools.styling.Rule;
import org.upes.model.RuleEntry;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class CriticalGrid
{
    private final static Color BEIGE = new Color(240, 232, 196);

    private final static RuleEntry MOST = new RuleEntry(Color.BLACK, Color.RED, 1.0);
    private final static RuleEntry CRITICAL = new RuleEntry(Color.BLACK, Color.GRAY, 1.0);
    private final static RuleEntry MODERATE = new RuleEntry(Color.BLACK, Color.ORANGE, 1.0);
    private final static RuleEntry NORMAL = new RuleEntry(Color.BLACK, Color.BLUE, 1.0);
    private final static RuleEntry LOW = new RuleEntry(Color.BLACK, Color.GREEN, 1.0);
    private final static RuleEntry LOWEST = new RuleEntry(Color.BLACK, BEIGE, 1.0);

    public static ArrayList<Rule> createRuleEntries(String geomAttribute)
    {
        ArrayList<Rule> rules = new ArrayList<>();
        rules.add(StyleUtils.createRule(MOST, geomAttribute));
        rules.add(StyleUtils.createRule(CRITICAL, geomAttribute));
        rules.add(StyleUtils.createRule(MODERATE, geomAttribute));
        rules.add(StyleUtils.createRule(NORMAL, geomAttribute));
        rules.add(StyleUtils.createRule(LOW, geomAttribute));
        rules.add(StyleUtils.createRule(LOWEST, geomAttribute));
        return rules;
    }

    public static ArrayList<Rule> createRuleEntries(int num,String geomAttribute)
    {
        ArrayList<Rule> rules = new ArrayList<>();
        int diff = 255/num;
        for (int i=0; i<num; i++)
        {
            int green = (255-i*diff)>0?(255-i*diff):0;
            Color color = new Color(255,green,10);
            RuleEntry ruleEntry = new RuleEntry(Color.BLACK,color,1.0);
            rules.add(StyleUtils.createRule(ruleEntry,geomAttribute));
        }
        return rules;
    }

}