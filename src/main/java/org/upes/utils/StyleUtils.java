package org.upes.utils;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.*;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.upes.model.Beat;
import org.upes.model.RuleEntry;

import java.awt.*;
import java.util.*;

public class StyleUtils
{
    private static StyleFactory   sf = CommonFactoryFinder.getStyleFactory();
    private static FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    public static Rule createRule(Color outlineColor, Color fillColor, double lineWidth, String geometryAttributeName)
    {
        Symbolizer                  symbolizer = null;
        Fill                        fill       = null;
        org.geotools.styling.Stroke stroke     = sf.createStroke(ff.literal(outlineColor), ff.literal(lineWidth));

        // Polygon type
        fill = sf.createFill(ff.literal(fillColor), ff.literal(1.0));
        symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    public static Rule createRule(RuleEntry ruleEntry, String geomAttributeName)
    {
        return createRule(ruleEntry.outlineColor, ruleEntry.fillColor, ruleEntry.lineWidth, geomAttributeName);
    }


    public static Style createSelectedStyle(RuleEntry defaultEntry, RuleEntry selectedEntry, Set<FeatureId> IDs, String
            geometryAttributeName)
    {
        Rule selectedRule = createRule(selectedEntry, geometryAttributeName);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(defaultEntry, geometryAttributeName);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Create a default Style for feature display
     * @param geometryAttributeName
     */
    public static Style createDefaultStyle(RuleEntry ruleEntry, String geometryAttributeName)
    {
        Rule rule = createRule(ruleEntry, geometryAttributeName);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    private static class Sortbyroll implements Comparator<Beat>
    {
        // Used for sorting in ascending order of
        // roll number
        public int compare(Beat a, Beat b)
        {
            return (int)(a.getScore() - b.getScore());
        }
    }


    public static ArrayList<Integer> regroupPerBlock(ArrayList<Beat> beatList)
    {
        ArrayList<Integer> beatsPerBlock = new ArrayList<>();
        beatsPerBlock.add(0);
        int i;
        for (i=1; i < beatList.size(); i++)
        {
            while (i < beatList.size() && beatList.get(i).getScore() == beatList.get(i - 1).getScore())
            {
                i++;
            }
            beatsPerBlock.add(i);
        }

        // If last not added
        if (i <= beatList.size() && beatList.size() != 0)
        {
            // Add in a new block
            beatsPerBlock.add(i);
        }

        return beatsPerBlock;
    }

    public static Style createStyleFromCritical(LinkedList<Beat> rawList, String geometryAttributeName)
    {
        ArrayList<Rule> rules = CriticalGrid.createRuleEntries(geometryAttributeName);
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();

        // Sort beats per score
        ArrayList<Beat> beatList = new ArrayList<>();
        beatList.addAll(rawList);
        beatList.sort(new Sortbyroll());

        // Regroup per block, beat with the same score
        ArrayList<Integer> blocks = regroupPerBlock(beatList);

        int colorChange = (int) Math.ceil(beatList.size() / rules.size());

        int blockIndex = 1;
        for (int ri=0; ri < rules.size(); ri++)
        {
            Rule rule = rules.get(ri);
            Set<FeatureId> ids = new HashSet<FeatureId>();

            if (ri == rules.size() - 1)
            {
                for (;blockIndex < blocks.size(); blockIndex++)
                {
                    for (int beatIndex=blocks.get(blockIndex-1); beatIndex < blocks.get(blockIndex); beatIndex++)
                    {
                        ids.add(beatList.get(beatIndex).getId());
                    }
                }
            }
            else
            {
                for (int ci=0; blockIndex < blocks.size() && ci <= colorChange &&
                        (ci == 0 || ci + blocks.get(blockIndex) - blocks.get(blockIndex-1) < colorChange); blockIndex++)
                {
                    // Add all element with the same score
                    for (int beatIndex=blocks.get(blockIndex-1); beatIndex < blocks.get(blockIndex); beatIndex++, ci++)
                    {
                        ids.add(beatList.get(beatIndex).getId());
                    }
                }
            }

            System.out.println(ri + " = " + ids.size());

            rule.setFilter(ff.id(ids));
            fts.rules().add(rule);
        }
        
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
}
