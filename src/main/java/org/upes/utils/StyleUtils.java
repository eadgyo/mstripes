package org.upes.utils;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.styling.*;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.upes.model.Beat;
import org.upes.model.RuleEntry;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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

    public static Style createStyleFromCritical(LinkedList<Beat> beatList, String geometryAttributeName)
    {
        ArrayList<Rule> rules = new ArrayList<>();
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();

        for (Beat beat : beatList)
        {
            double score = beat.getScore();
            RuleEntry myStyle = CriticalGrid.getStyle(score);
            Rule selectedRule = createRule(myStyle, geometryAttributeName);
            Set<FeatureId> id = new HashSet<FeatureId>();
            id.add(new FeatureIdImpl(beat.getId()));
            selectedRule.setFilter(ff.id(id));
            fts.rules().add(selectedRule);
        }


        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
}
