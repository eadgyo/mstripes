package org.upes;

import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.opengis.feature.simple.SimpleFeatureType;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by This Pc on 04-07-2017.
 */
public class MyStyleFactory {

    private ArrayList<PointLayer> pointLayers;
    private ArrayList<LineLayer> lineLayers;
    private ArrayList<PolygonLayer> polygonLayers;

   public  MyStyleFactory()
   {
       pointLayers=new ArrayList<PointLayer>();
       lineLayers=new ArrayList<LineLayer>();
       polygonLayers=new ArrayList<PolygonLayer>();

       // BEAT file
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.LIGHT_GRAY,0.2f));

       //BEAT_IDENTITY1
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.GRAY,0.2f));

       //BOUNDARY
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.CYAN,0.3f));

       //DIVISION
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.magenta,0.2f));

       //FOREST_ROAD
       lineLayers.add(new LineLayer(Color.BLACK,1f));

       //GRID
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.WHITE,0.1f));

       //PATROLLING_CAMP
        pointLayers.add(new PointLayer("circle",Color.RED,Color.RED,0.8f,5));

       //RANGE
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.green,0.3f));

       //STATE
       polygonLayers.add(new PolygonLayer(Color.BLACK,Color.ORANGE,0.1f));

       //WIRELESS_STATION
        pointLayers.add(new PointLayer("circle",Color.BLUE,Color.BLUE,0.8f,5));
   }

    public Style getStyle(SimpleFeatureType ftype,Object l)
    {
        Style style=null;
        if (ftype.getGeometryDescriptor().getType().getName().toString().equals("MultiPolygon"))
        {
            PolygonLayer layer=(PolygonLayer) l;
            style= SLD.createPolygonStyle(layer.getOutline(),layer.getFillcolor(),layer.getOpacity());
        }
        else if(ftype.getGeometryDescriptor().getType().getName().toString().equals("MultiLineString"))
        {
              LineLayer layer=(LineLayer)l;
              style=SLD.createLineStyle(layer.getOutline(),layer.getWidth());
        }
        else if(ftype.getGeometryDescriptor().getType().getName().toString().equals("Point"))
        {
            PointLayer layer=(PointLayer) l;
            style=SLD.createPointStyle(layer.getWellknownname(),layer.getOutline(),layer.getFillcolor(),layer.getOpacity(),layer.getSize());
        }
        else
        {
            System.out.println("Some other Geometry!");
        }
        return style;
    }

    public Style setStyle(SimpleFeatureType ftype)
    {
        Style style=null;
        String layerName=ftype.getTypeName();
        switch (layerName)
        {
            case "BEAT":
                style=getStyle(ftype,polygonLayers.get(Constants.BEAT));
            break;
            case "BEAT_Identity1":
                style=getStyle(ftype,polygonLayers.get(Constants.BEAT_I1));
            break;
            case "BOUNDARY":
                style=getStyle(ftype,polygonLayers.get(Constants.BOUNDARY));
            break;
            case "DIVISION":
                style=getStyle(ftype,polygonLayers.get(Constants.DIVISION));
            break;
            case "forst_road_core":
                style=getStyle(ftype,lineLayers.get(Constants.FOREST_ROAD));
            break;
            case "GRID":
                style=getStyle(ftype,polygonLayers.get(Constants.GRID));
            break;
            case "Patrolling_camp":
                style=getStyle(ftype,pointLayers.get(Constants.PATROLLING_CAMP));
            break;
            case "RANGE":
                style=getStyle(ftype,polygonLayers.get(Constants.RANGE));
            break;
            case "STATE":
                style=getStyle(ftype,polygonLayers.get(Constants.STATE));
            break;
            case "Wireless_station":
                style=getStyle(ftype,pointLayers.get(Constants.WIRELESS_STATION));
            break;
            default:
                System.out.println("Unregistered File!");
        }

        return  style;
    }

}

class PointLayer
{
    private String wellknownname;
    private Color outline;
    private Color fillcolor;
    private float opacity;
    private float size;

    public PointLayer(String wellknownname,Color outline,Color fillcolor,float opacity, float size)
    {
        this.wellknownname=wellknownname;
        this.outline=outline;
        this.fillcolor=fillcolor;
        this.opacity=opacity;
        this.size=size;
    }

    public String getWellknownname() {
        return wellknownname;
    }

    public Color getOutline() {
        return outline;
    }

    public Color getFillcolor() {
        return fillcolor;
    }

    public float getOpacity() {
        return opacity;
    }

    public float getSize() {
        return size;
    }
}

class LineLayer
{
    private Color outline;
    private float width;

    public LineLayer(Color outline, float width) {
        this.outline = outline;
        this.width = width;
    }

    public Color getOutline() {
        return outline;
    }

    public float getWidth() {
        return width;
    }
}

class PolygonLayer
{
    private Color outline;
    private Color fillcolor;
    private float opacity;

    public PolygonLayer(Color outline, Color fillcolor, float opacity) {
        this.outline = outline;
        this.fillcolor = fillcolor;
        this.opacity = opacity;
    }

    public Color getOutline() {
        return outline;
    }

    public Color getFillcolor() {
        return fillcolor;
    }

    public float getOpacity() {
        return opacity;
    }
}
