package org.upes.utils;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import org.upes.Constants;
import org.upes.model.Beat;
import org.upes.model.RuleEntry;
import org.upes.model.SqlOp;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        Rule rule = sf.createRule();

        // Polygon type
        fill = sf.createFill(ff.literal(fillColor), ff.literal(0.7));
        symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
        rule.symbolizers().add(symbolizer);

        return rule;
    }

    public static Rule  createDialogRule(Color outlineColor, Color fillColor,double lineWidth,double opacity,GeometryType geometryType,String geometryAttributeName)
    {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(lineWidth));
        Class<?> clazz = geometryType.getBinding();

        if (Polygon.class.isAssignableFrom(clazz) ||MultiPolygon.class.isAssignableFrom(clazz))
        {
            fill = sf.createFill(ff.literal(fillColor), ff.literal(opacity));
            symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
        }
        else if (LineString.class.isAssignableFrom(clazz) || MultiLineString.class.isAssignableFrom(clazz))
        {
            symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
        }
        else
        {
            fill = sf.createFill(ff.literal(outlineColor), ff.literal(opacity));

            Mark mark = sf.getCircleMark();
            mark.setFill(fill);
            mark.setStroke(stroke);

            Graphic graphic = sf.createDefaultGraphic();
            graphic.graphicalSymbols().clear();
            graphic.graphicalSymbols().add(mark);
            graphic.setSize(ff.literal(lineWidth*2.5));

            symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    public static Rule createRule(RuleEntry ruleEntry, String geomAttributeName)
    {
        return createRule(ruleEntry.outlineColor, ruleEntry.fillColor, ruleEntry.lineWidth, geomAttributeName);
    }

    private static Rule createRule(RuleEntry ruleEntry, GeometryType type, String geometryAttributeName)
    {
        return createDialogRule(ruleEntry.outlineColor, ruleEntry.fillColor, ruleEntry.lineWidth,ruleEntry.opacity,type,geometryAttributeName);
    }

    public static Style createSelectedStyle(SqlOp sqlOp,int num,RuleEntry selectedEntry, Set<FeatureId> IDs, String
            geometryAttributeName,String beatName)
    {
        Rule selectedRule = createRule(selectedEntry, geometryAttributeName);
        selectedRule.setFilter(ff.id(IDs));
        ArrayList<Double> list = sqlOp.getSortedScores();
        ArrayList<Rule> rules = CriticalGrid.createRuleEntries(num,geometryAttributeName);
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        System.out.println(list.size());
        for (int i=0; i<list.size(); i++)
        {
            ArrayList<String> tempList = sqlOp.getGridsFromScore(list.get(i),beatName);
            Rule rule = rules.get(i);
            Set<FeatureId> ids  = new HashSet<>();
            for(String name : tempList)
            {
                ids.add(ff.featureId(name));
            }
            rule.setFilter(ff.id(ids));
            fts.rules().add(rule);
        }

        fts.rules().addAll(rules);
        fts.rules().add(selectedRule);

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

    public static Style createDefaultStyle(RuleEntry ruleEntry, GeometryType type, String geometryAttributeName) {

        Rule rule = createRule(ruleEntry,type, geometryAttributeName);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(rule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }


    public static Style createStyleFromCritical(List<Beat> sortedList, Set<FeatureId> IDs, RuleEntry selectedEntry,
                                                String geometryAttributeName)
    {
        FeatureTypeStyle featureTypeFromCritical = createFeatureTypeFromCritical(sortedList, geometryAttributeName);

        Rule selectedRule = createRule(selectedEntry, geometryAttributeName);
        selectedRule.setFilter(ff.id(IDs));
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);


        Style style = sf.createStyle();

        style.featureTypeStyles().add(featureTypeFromCritical);
        style.featureTypeStyles().add(fts);

        return style;
    }


    public static FeatureTypeStyle createFeatureTypeFromCritical(List<Beat> sortedList,
                                                                 String geometryAttributeName)
    {
        ArrayList<Rule> rules = CriticalGrid.createRuleEntries(geometryAttributeName);
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();

        int i = 0;

        for (int rank = 0; rank < Constants.NUMBER_RANKS; rank++)
        {
            Rule           rule = rules.get(rank);
            Set<FeatureId> ids  = new HashSet<>();

            while (i < sortedList.size() && sortedList.get(i).getRank() == rank)
            {
                ids.add(sortedList.get(i).getId());
                i++;
            }
            rule.setFilter(ff.id(ids));
            fts.rules().add(rule);
        }

        return fts;
    }

    public static FeatureTypeStyle createFeatureTypeFromCritical(SqlOp sqlOp,String geometryAttributeName)
    {
        ArrayList<Double> list = sqlOp.getSortedScores();

        ArrayList<Rule> rules = CriticalGrid.createRuleEntries(list.size(),geometryAttributeName);
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();

        for (int i=0; i<list.size(); i++)
        {
            ArrayList<String> tempList = sqlOp.getGridsFromScore(list.get(i));
            Rule rule = rules.get(i);
            Set<FeatureId> ids  = new HashSet<>();
            for(String name : tempList)
            {
                ids.add(ff.featureId(name));
            }
            rule.setFilter(ff.id(ids));
            fts.rules().add(rule);
        }
        return fts;
    }

    public static Style createStyleFromCritical(SqlOp sqlOp, String geometryAttributeName)
    {
        FeatureTypeStyle featureTypeFromCritical = createFeatureTypeFromCritical(sqlOp, geometryAttributeName);
        Style style = sf.createStyle();
        style.featureTypeStyles().add(featureTypeFromCritical);
        return style;
    }

}
